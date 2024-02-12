package com.bely.kongplayer.shared;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.Random;

public class Utils {
    private static boolean mNormalPlay = false;
    private static int mCustomButtonNumber = 0;
    private static int mQueueListSize = 10;
    private static int mErrorCode = PlaybackStateCompat.ERROR_CODE_AUTHENTICATION_EXPIRED;
    private static boolean mSupportBrowse = true;
    private static String mErrorMsg = "ERROR_CODE_AUTHENTICATION_EXPIRED";

    private static long mActions = 0;

    private static Handler mServiceHandler;

    public static boolean isSupportBrowse() {
        return mSupportBrowse;
    }

    public static void setSupportBrowse(boolean support) {
        mSupportBrowse = support;
    }
    public static boolean isNormalPlay() {
        return mNormalPlay;
    }

    public static void setNormalPlay(boolean normalPlay) {
        Utils.mNormalPlay = normalPlay;
    }

    public static void setServiceHandler(Handler handler) {
        mServiceHandler = handler;
    }

    public static void play() {
        if (mServiceHandler == null) return;
        mServiceHandler.sendEmptyMessage(Constants.MSG_ACTION_PLAY);
    }

    public static void pause() {
        if (mServiceHandler == null) return;
        mServiceHandler.sendEmptyMessage(Constants.MSG_ACTION_PAUSE);
    }

    public static void stop() {
        if (mServiceHandler == null) return;
        mServiceHandler.sendEmptyMessage(Constants.MSG_ACTION_STOP);
    }

    public static void setError(int error, String errMsg) {
        if (mServiceHandler == null) return;
        Message msg = Message.obtain();
        msg.what = Constants.MSG_ACTION_SET_ERROR_STATE;
        msg.arg1 = error;
        msg.obj = errMsg;
        mServiceHandler.sendMessage(msg);
    }

    public static void setCustomBtnCount(int count) {
        if (mServiceHandler == null) return;
        Message msg = Message.obtain();
        msg.what = Constants.MSG_ACTION_SET_CUSTOM_BUTTON_NUM;
        msg.arg1 = count;
        mServiceHandler.sendMessage(msg);
    }

    public static int getCustomButtonNumber() {
        return mCustomButtonNumber;
    }

    public static void setCustomButtonNumber(int mCustomButtonNumber) {
        Utils.mCustomButtonNumber = mCustomButtonNumber;
        setCustomBtnCount(mCustomButtonNumber);
    }

    public static int getErrorCode() {
        return mErrorCode;
    }

    public static void setErrorCode(int mErrorCode, String errmsg) {
        Utils.mErrorCode = mErrorCode;
        setError(mErrorCode, errmsg);
    }

    public static long getActions() {
        return mActions;
    }

    public static void setActions(long mActions) {
        Utils.mActions = mActions;
    }

    public static int getQueueListSize() {
        return mQueueListSize;
    }

    public static void setQueueListSize(int mQueueListSize) {
        Utils.mQueueListSize = mQueueListSize;
    }

    public static Bitmap generateRandomBitmap(int number) {
        int width = 200;
        int height = 200;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        Random random = new Random();
        int backgroundColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        canvas.drawRect(0, 0, width, height, backgroundPaint);

        int textColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

        Paint textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(50);
        String text = String.valueOf(number);
        float textWidth = textPaint.measureText(text);
        float x = (width - textWidth) / 2;
        float y = height / 2;
        canvas.drawText(text, x, y, textPaint);

        return bitmap;
    }
}
