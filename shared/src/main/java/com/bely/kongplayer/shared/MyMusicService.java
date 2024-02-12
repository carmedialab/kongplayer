package com.bely.kongplayer.shared;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.media.MediaBrowserCompat.MediaItem;

import androidx.media.MediaBrowserServiceCompat;

import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a MediaBrowser through a service. It exposes the media library to a browsing
 * client, through the onGetRoot and onLoadChildren methods. It also creates a MediaSession and
 * exposes it through its MediaSession.Token, which allows the client to create a MediaController
 * that connects to and send control commands to the MediaSession remotely. This is useful for
 * user interfaces that need to interact with your media session, like Android Auto. You can
 * (should) also use the same service from your app's UI, which gives a seamless playback
 * experience to the user.
 * <p>
 * To implement a MediaBrowserService, you need to:
 *
 * <ul>
 *
 * <li> Extend {@link MediaBrowserServiceCompat}, implementing the media browsing
 *      related methods {@link MediaBrowserServiceCompat#onGetRoot} and
 *      {@link MediaBrowserServiceCompat#onLoadChildren};
 * <li> In onCreate, start a new {@link MediaSessionCompat} and notify its parent
 *      with the session"s token {@link MediaBrowserServiceCompat#setSessionToken};
 *
 * <li> Set a callback on the {@link MediaSessionCompat#setCallback(MediaSessionCompat.Callback)}.
 *      The callback will receive all the user"s actions, like play, pause, etc;
 *
 * <li> Handle all the actual music playing using any method your app prefers (for example,
 *      {@link android.media.MediaPlayer})
 *
 * <li> Update playbackState, "now playing" metadata and queue, using MediaSession proper methods
 *      {@link MediaSessionCompat#setPlaybackState(android.support.v4.media.session.PlaybackStateCompat)}
 *      {@link MediaSessionCompat#setMetadata(android.support.v4.media.MediaMetadataCompat)} and
 *      {@link MediaSessionCompat#setQueue(java.util.List)})
 *
 * <li> Declare and export the service in AndroidManifest with an intent receiver for the action
 *      android.media.browse.MediaBrowserService
 *
 * </ul>
 * <p>
 * To make your app compatible with Android Auto, you also need to:
 *
 * <ul>
 *
 * <li> Declare a meta-data tag in AndroidManifest.xml linking to a xml resource
 *      with a &lt;automotiveApp&gt; root element. For a media app, this must include
 *      an &lt;uses name="media"/&gt; element as a child.
 *      For example, in AndroidManifest.xml:
 *          &lt;meta-data android:name="com.google.android.gms.car.application"
 *              android:resource="@xml/automotive_app_desc"/&gt;
 *      And in res/values/automotive_app_desc.xml:
 *          &lt;automotiveApp&gt;
 *              &lt;uses name="media"/&gt;
 *          &lt;/automotiveApp&gt;
 *
 * </ul>
 */
public class MyMusicService extends MediaBrowserServiceCompat {

    private MediaSessionCompat mSession;

    private KongPlayer mPlayer;
    private Handler mBGHandler;
    @Override
    public void onCreate() {
        super.onCreate();

        mSession = new MediaSessionCompat(this, "KongPlayer");
        setSessionToken(mSession.getSessionToken());
        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mPlayer = new KongPlayer(mSession);

        HandlerThread thread = new HandlerThread("KongPlayerThread");
        thread.start();
        mBGHandler = new Handler(thread.getLooper(), mMsgHandlerCallback);
        Utils.setServiceHandler(mBGHandler);
        Log.i("MyMusicService", "service created...");
    }

    private Handler.Callback mMsgHandlerCallback = message -> {
        switch(message.what) {
            case Constants.MSG_ACTION_PLAY:
                mSession.setActive(true);
                if (Utils.isNormalPlay()) {
                    mPlayer.startPlay();
                } else {
                    mPlayer.setErrorCode();
                }
                break;
            case Constants.MSG_ACTION_PAUSE:
                mPlayer.pause();
                break;
            case Constants.MSG_ACTION_STOP:
                mSession.setActive(false);
                mPlayer.stop();
                break;
            case Constants.MSG_ACTION_SET_ERROR_STATE:
                int code = message.arg1;
                String errMsg = (String) message.obj;
                mPlayer.stop();
                mPlayer.setErrorCode(code, errMsg);
                break;
            case Constants.MSG_ACTION_SET_CUSTOM_BUTTON_NUM:
                int customButtonNum = message.arg1;
                mPlayer.setCustomButtons(customButtonNum);
                break;
        }
        return false;
    };

    @Override
    public void onDestroy() {
        mSession.setActive(false);
        mSession.release();
        mPlayer.stop();
    }

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName,
                                 int clientUid,
                                 Bundle rootHints) {
        return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(@NonNull final String parentMediaId,
                               @NonNull final Result<List<MediaItem>> result) {
        if (!Utils.isSupportBrowse()) {
            result.sendResult(new ArrayList<MediaItem>());
        } else {
            BrowseListProvider.loadChildrenImpl_Testing(parentMediaId, result);
        }
    }

    private final class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mSession.setActive(true);
            if (Utils.isNormalPlay()) {
                mPlayer.startPlay();
            } else {
                mPlayer.setErrorCode();
            }
        }

        @Override
        public void onSkipToQueueItem(long queueId) {
        }

        @Override
        public void onSeekTo(long position) {
            mPlayer.seekTo(position);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
        }

        @Override
        public void onPause() {
            mPlayer.pause();
        }

        @Override
        public void onStop() {
            mSession.setActive(false);
            mPlayer.stop();
        }

        @Override
        public void onSkipToNext() {
            mPlayer.skipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            mPlayer.skipToPrevious();
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            Toast.makeText(KongPlayerApplication.getContext(), action, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPlayFromSearch(final String query, final Bundle extras) {
        }

        @Override
        public void onSetShuffleMode(int mode) {
            mPlayer.setShuffleMode(mode);
            mSession.setShuffleMode(mode);
        }

        @Override
        public void onSetRepeatMode(int mode) {
            mPlayer.setRepeatMode(mode);
            mSession.setRepeatMode(mode);
        }
    }

}