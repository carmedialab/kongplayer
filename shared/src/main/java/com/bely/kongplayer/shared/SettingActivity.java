package com.bely.kongplayer.shared;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {

    CheckBox mCheckBox_NormalPlay;
    CheckBox mCheckBox_supportBrowse;
    CheckBox mCheckBox_Browsable;
    EditText mCustomButtonNum;
    EditText mQueueListSize;
    ErrorCodePicker mErrorCodePicker;
    MediaBrowseTreee mBrowseTree;

    private final HashMap<Integer, Long> mActionList = new HashMap<Integer, Long>() {
        {
            put(R.id.checkBox_ACTION_STOP, PlaybackStateCompat.ACTION_STOP);
            put(R.id.checkBox_ACTION_PAUSE, PlaybackStateCompat.ACTION_PAUSE);
            put(R.id.checkBox_ACTION_PLAY, PlaybackStateCompat.ACTION_PLAY);
            put(R.id.checkBox_ACTION_REWIND, PlaybackStateCompat.ACTION_REWIND);
            put(R.id.checkBox_ACTION_SKIP_TO_PREVIOUS, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
            put(R.id.checkBox_ACTION_SKIP_TO_NEXT, PlaybackStateCompat.ACTION_SKIP_TO_NEXT);
            put(R.id.checkBox_ACTION_FAST_FORWARD, PlaybackStateCompat.ACTION_FAST_FORWARD);
            put(R.id.checkBox_ACTION_SET_RATING, PlaybackStateCompat.ACTION_SET_RATING);
            put(R.id.checkBox_ACTION_SEEK_TO, PlaybackStateCompat.ACTION_SEEK_TO);
            put(R.id.checkBox_ACTION_PLAY_PAUSE, PlaybackStateCompat.ACTION_PLAY_PAUSE);
            put(R.id.checkBox_ACTION_SET_REPEAT_MODE, PlaybackStateCompat.ACTION_SET_REPEAT_MODE);
            put(R.id.checkBox_ACTION_SET_SHUFFLE_MODE, PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitysetting);

        initViews();
    }

    private void initViews() {
        mCheckBox_NormalPlay = findViewById(R.id.checkBox_normal_play);
        mCheckBox_supportBrowse = findViewById(R.id.checkBox_browsable);
        mCustomButtonNum = findViewById(R.id.editText_custom_button_number);
        mQueueListSize = findViewById(R.id.editText_queue_number);
        mErrorCodePicker = findViewById(R.id.errorpicker);
        mCheckBox_Browsable = findViewById(R.id.checkBox_browsable);
//        mBrowseTree = findViewById(R.id.browsetree);

        mCheckBox_NormalPlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Utils.setNormalPlay(b);
                updateViews();
            }
        });
/*
        mCheckBox_Browsable.setOnCheckedChangeListener((c, b) -> {
            mBrowseTree.setVisibility(b ? View.VISIBLE : View.GONE);
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();

        updateViews();
    }

    private void updateViews() {
        mCheckBox_NormalPlay.setChecked(Utils.isNormalPlay());
        mQueueListSize.setEnabled(Utils.isNormalPlay());
        mCustomButtonNum.setEnabled(Utils.isNormalPlay());
        mCheckBox_Browsable.setChecked(Utils.isSupportBrowse());
        for (Integer key : mActionList.keySet()) {
            findViewById(key).setEnabled(Utils.isNormalPlay());
        }
        mErrorCodePicker.setEnabled(!Utils.isNormalPlay());

        if (Utils.isNormalPlay()) {
            mQueueListSize.setText(String.valueOf(Utils.getQueueListSize()));
            mCustomButtonNum.setText(String.valueOf(Utils.getCustomButtonNumber()));

            for (Integer key : mActionList.keySet()) {
                ((CheckBox)findViewById(key)).setChecked((Utils.getActions() & mActionList.get(key)) != 0);
            }
            mErrorCodePicker.setSelection(0);
        } else {
            mErrorCodePicker.setSelection(Utils.getErrorCode());

            for (Integer key : mActionList.keySet()) {
                ((CheckBox)findViewById(key)).setChecked(false);
            }
        }
    }

    public void onClose(View view) {
        Utils.setNormalPlay(mCheckBox_NormalPlay.isChecked());
        Utils.setSupportBrowse(mCheckBox_supportBrowse.isChecked());
        if (!Utils.isNormalPlay()) {
            Utils.setCustomButtonNumber(0);
            Utils.setActions(0);
            Utils.setErrorCode(mErrorCodePicker.getSelectedErrorCode(), mErrorCodePicker.getSeletedErrorMsg());
        } else {
            Editable e = mQueueListSize.getText();
            Utils.setQueueListSize(TextUtils.isEmpty(e.toString()) ? 0 : Integer.valueOf(e.toString()));
            e = mCustomButtonNum.getText();
            Utils.setCustomButtonNumber(TextUtils.isEmpty(e.toString()) ? 0 : Integer.valueOf(e.toString()));

            //find actions
            long action = 0;
            for (Integer key : mActionList.keySet()) {
                if (((CheckBox)findViewById(key)).isChecked()) {
                    action |= mActionList.get(key);
                }
            }
            Utils.setActions(action);
            Utils.play();
        }
        finish();
    }

    @Override
    public void onStop() {

        super.onStop();
        onClose(null);
    }

    public void onOpenLogin(View view) {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
    }
}