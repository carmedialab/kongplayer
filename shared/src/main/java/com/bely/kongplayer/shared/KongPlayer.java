package com.bely.kongplayer.shared;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.media.utils.MediaConstants;

import com.bely.kongplayer.shared.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class KongPlayer {
    private final static int MSG_SEND_PLAYING_STATE_DATA = 2000;
    private final static int MSG_SEND_META_DATA = 2001;
    private final static int MSG_SEND_PAUSE_DATA = 2002;
    private final static int TIME_TO_SEND_PLAYBACK_STATE = 1000;

    private final static int SONG_MAX_LENGTH = 400;
    private final static int SONG_MIN_LENGTH = 30;

    private final static int MAX_CUSTOM_BTN_NUM = 9;
    private static final int MAX_TITLE_LENGTH = 30;
    private static final int MAX_ARTIST_LENGTH = 20;
    private static final String TAG = KongPlayer.class.getSimpleName();
    private MediaSessionCompat mSession;
    private Handler mPlayHandler;
    private long mPlayingPosition = 0;
    private int mPlaySpeed = 1;
    private int mTotalDuration = 0;
    private int mCurrentPlayingIndex = -1;
    private int mRepeatMode = PlaybackStateCompat.REPEAT_MODE_NONE;
    private int mShuffleMode = PlaybackStateCompat.SHUFFLE_MODE_NONE;

    private int mPlaybackState = PlaybackStateCompat.STATE_NONE;
    private String mTitle;
    private String mArtist;
    private Bitmap mCurAlbumArt;
    private int mErrorCode = PlaybackStateCompat.ERROR_CODE_AUTHENTICATION_EXPIRED;
    private String mErrMsg;

    private List<PlaybackStateCompat.CustomAction> mCustomActions = new ArrayList<>();
    private List<Song> mQueue;
    private List<Song> mNoneShuffleQueue = new ArrayList<>();
    public KongPlayer(MediaSessionCompat session) {
        mSession = session;
        HandlerThread playThread = new HandlerThread("KongPlayer");
        playThread.start();
        mPlayHandler = new Handler(playThread.getLooper(), mPlayerHandlingCallback);
        startNewList();
    }

    private Handler.Callback mPlayerHandlingCallback = message -> {
        switch (message.what) {
            case MSG_SEND_PLAYING_STATE_DATA:
                sendPlayingStateData();
                break;
            case MSG_SEND_META_DATA:
                sendMetadata();
                break;
            case MSG_SEND_PAUSE_DATA:
                sendPauseStateData();
                break;
        }
        return false;
    };

    public void seekTo(long position) {
        mPlayingPosition = position;
    }

    public void setShuffleMode(int shuffleMode) {
        mShuffleMode = shuffleMode;
        shuffleQueue();
        Log.i(TAG, "change shuffle to " + shuffleMode);
    }

    public void setRepeatMode(int repeatMode) {
        mRepeatMode = repeatMode;
        Log.i(TAG, "change repeat to " + repeatMode);
    }

    private static class Song {
        public String title;
        public String artist;
        public int duration;
        public Bitmap albumart;

        public Song(String t, String ar, int dur, Bitmap b) {
            title = t;
            artist = ar;
            duration = dur;
            albumart = b;
        }
    }

    private void sendErrorInfo() {
        if (mErrorCode == PlaybackStateCompat.ERROR_CODE_AUTHENTICATION_EXPIRED) {
            sendRequestLogin();
        } else {
            PlaybackStateCompat.Builder sb = new PlaybackStateCompat.Builder();
            sb.setErrorMessage(mErrorCode, mErrMsg);
            sb.setState(PlaybackStateCompat.STATE_ERROR, 0, 0);
            mSession.setPlaybackState(sb.build());
        }
    }

    private void sendPlayingStateData() {
        setCustomButtons(Utils.getCustomButtonNumber());
        PlaybackStateCompat.Builder sb = new PlaybackStateCompat.Builder();
        sb.setState(PlaybackStateCompat.STATE_PLAYING, mPlayingPosition*1000, mPlaySpeed);
        mPlayingPosition++;
        for (PlaybackStateCompat.CustomAction action : mCustomActions) {
            sb.addCustomAction(action);
        }
        Bundle extras = new Bundle();
        //extras.putInt(PlaybackStateCompat.EXTRA_SHUFFLE_MODE, mShuffleMode);
        //extras.putInt(PlaybackStateCompat.EXTRA_REPEAT_MODE, mRepeatMode);
        sb.setExtras(extras);
        sb.setActions(Utils.getActions());
        mSession.setPlaybackState(sb.build());

        mPlayHandler.sendEmptyMessageDelayed(MSG_SEND_PLAYING_STATE_DATA, TIME_TO_SEND_PLAYBACK_STATE);
        if (mPlayingPosition >= mTotalDuration) {
            mPlayingPosition = 0;
            determineNextSong();
        }
    }
    //when one song ends
    private void determineNextSong() {
        if (mRepeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
            //do nothing
        } else { //not repeat one, can be repeat all, or none
            if(mCurrentPlayingIndex >= (mQueue.size()-1)) { //all played
                if (mRepeatMode == PlaybackStateCompat.REPEAT_MODE_NONE) { //not repeat
                    stop();
                } else if (mRepeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) { //repeat all
                    mCurrentPlayingIndex = 0; //restart
                    nextSong();
                }
            } else {
                //not finished yet
                mCurrentPlayingIndex++;
                nextSong();
            }
        }
    }
    private void nextSong() {
        if (mCurrentPlayingIndex < 0) mCurrentPlayingIndex = 0;
        if (mCurrentPlayingIndex >= mQueue.size()) mCurrentPlayingIndex = 0;
        mPlayingPosition = 0;
        mTotalDuration = mQueue.get(mCurrentPlayingIndex).duration;
        //get new metadata
        mTitle = mQueue.get(mCurrentPlayingIndex).title;
        mArtist = mQueue.get(mCurrentPlayingIndex).artist;
        mCurAlbumArt = mQueue.get(mCurrentPlayingIndex).albumart;
        mPlayHandler.sendEmptyMessageDelayed(MSG_SEND_META_DATA, 500);
    }

    private void shuffleQueue() {
        if (mShuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
            mQueue = new ArrayList<>(mNoneShuffleQueue);
            Collections.shuffle(mQueue);
        } else { //no shuffle
            mQueue = mNoneShuffleQueue; //original
        }
    }
    private void sendPauseStateData() {
        setCustomButtons(Utils.getCustomButtonNumber());
        PlaybackStateCompat.Builder sb = new PlaybackStateCompat.Builder();
        sb.setState(PlaybackStateCompat.STATE_PAUSED, mPlayingPosition*1000, mPlaySpeed);
        for (PlaybackStateCompat.CustomAction action : mCustomActions) {
            sb.addCustomAction(action);
        }
        mSession.setPlaybackState(sb.build());
    }

    private void sendStopStateData() {
        PlaybackStateCompat.Builder sb = new PlaybackStateCompat.Builder();
        sb.setState(PlaybackStateCompat.STATE_STOPPED, 0, mPlaySpeed);
        mSession.setPlaybackState(sb.build());
    }



    private void sendMetadata() {
        MediaMetadataCompat mediaMetadata = new MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, mTitle)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, mArtist)
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, mCurAlbumArt)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, mTotalDuration * 1000)
                .build();

        mSession.setMetadata(mediaMetadata);
    }
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 *";
        StringBuilder randomString = new StringBuilder();

        Random random = new Random();
        int len = random.nextInt(length) + 3;
        for (int i = 0; i < len; i++) {
            int index = random.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }

        return randomString.toString();
    }

    private void sendRequestLogin() {
        Intent signInIntent = new Intent(KongPlayerApplication.getContext(), LoginActivity.class);
        PendingIntent signInActivityPendingIntent = PendingIntent.getActivity(KongPlayerApplication.getContext(), 0,
                signInIntent, 0);
        Bundle extras = new Bundle();
        extras.putString(
                MediaConstants.PLAYBACK_STATE_EXTRAS_KEY_ERROR_RESOLUTION_ACTION_LABEL,
                "Sign in");
        extras.putParcelable(
                MediaConstants.PLAYBACK_STATE_EXTRAS_KEY_ERROR_RESOLUTION_ACTION_INTENT,
                signInActivityPendingIntent);

        PlaybackStateCompat playbackState = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_ERROR, 0, 0f)
                .setErrorMessage(
                        PlaybackStateCompat.ERROR_CODE_AUTHENTICATION_EXPIRED,
                        "Authentication required"
                )
                .setExtras(extras)
                .build();
        mSession.setPlaybackState(playbackState);
    }

    private void prepareQueue() {
        mNoneShuffleQueue.clear();
        for (int i=0;i< Utils.getQueueListSize();i++) {
            mNoneShuffleQueue.add(new Song(generateRandomString(MAX_TITLE_LENGTH), generateRandomString(MAX_ARTIST_LENGTH), randomDuration(), Utils.generateRandomBitmap(i+1)));
        }
        shuffleQueue();
    }


    private int randomDuration() {
        Random r = new Random();
        return r.nextInt(SONG_MAX_LENGTH) + SONG_MIN_LENGTH;
    }

    //=======================public============

    //called when select a folder
    public void startNewList() {
        prepareQueue();
    }

    public void startPlay() {
        mPlaybackState = PlaybackStateCompat.STATE_PLAYING;
        if (mCurrentPlayingIndex == -1) { //no song is playing
            mCurrentPlayingIndex = 0;
            nextSong();
        }
        mPlayHandler.removeMessages(MSG_SEND_PLAYING_STATE_DATA);
        mPlayHandler.sendEmptyMessage(MSG_SEND_PLAYING_STATE_DATA);
        mPlayHandler.sendEmptyMessageDelayed(MSG_SEND_META_DATA, 500);
    }

    public void pause() {
        mPlaybackState = PlaybackStateCompat.STATE_PAUSED;
        mPlayHandler.removeCallbacksAndMessages(null);
        mPlayHandler.sendEmptyMessageDelayed(MSG_SEND_PAUSE_DATA, 100);
    }

    public void setErrorCode(int code, String errMsg) {
        mErrorCode = code;
        mErrMsg = errMsg;
        mPlayHandler.postDelayed(() -> sendErrorInfo(), 500);
    }

    public void setErrorCode() {
        mPlayHandler.postDelayed(() -> sendErrorInfo(), 500);
    }
    public void stop() {
        mPlayingPosition = 0;
        mTotalDuration = 0;
        mCurrentPlayingIndex = 0;
        mPlayHandler.removeCallbacksAndMessages(null);
        mPlayHandler.postDelayed(() -> sendStopStateData(), 500);
    }

    public void setCustomButtons(int num) {
        int btnnum = num > MAX_CUSTOM_BTN_NUM ? MAX_CUSTOM_BTN_NUM : num;

        int[] icons = {R.drawable.btn1,
                R.drawable.btn2,
                R.drawable.btn3,
                R.drawable.btn4,
                R.drawable.btn5,
                R.drawable.btn6,
                R.drawable.btn7,
                R.drawable.btn8,
                R.drawable.btn9
        };
        mCustomActions.clear();
        for (int i=0; i<btnnum;i++) {
            PlaybackStateCompat.CustomAction.Builder cab = new PlaybackStateCompat.CustomAction.Builder("CustomAction_"+(i+1),"CustomAction_"+(i+1), icons[i]);
            mCustomActions.add(cab.build());
        }
    }

    public void skipToNext() {
        mCurrentPlayingIndex ++;
        nextSong();
        mPlayHandler.removeMessages(MSG_SEND_PLAYING_STATE_DATA);
        mPlayHandler.sendEmptyMessage(MSG_SEND_PLAYING_STATE_DATA);
    }

    public void skipToPrevious() {
        mCurrentPlayingIndex --;
        nextSong();
        mPlayHandler.removeMessages(MSG_SEND_PLAYING_STATE_DATA);
        mPlayHandler.sendEmptyMessage(MSG_SEND_PLAYING_STATE_DATA);
    }


}
