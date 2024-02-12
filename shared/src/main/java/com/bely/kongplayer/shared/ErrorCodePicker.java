package com.bely.kongplayer.shared;

import static android.support.v4.media.session.PlaybackStateCompat.ERROR_CODE_ACTION_ABORTED;
import static android.support.v4.media.session.PlaybackStateCompat.ERROR_CODE_APP_ERROR;
import static android.support.v4.media.session.PlaybackStateCompat.ERROR_CODE_AUTHENTICATION_EXPIRED;
import static android.support.v4.media.session.PlaybackStateCompat.ERROR_CODE_CONCURRENT_STREAM_LIMIT;
import static android.support.v4.media.session.PlaybackStateCompat.ERROR_CODE_CONTENT_ALREADY_PLAYING;
import static android.support.v4.media.session.PlaybackStateCompat.ERROR_CODE_END_OF_QUEUE;
import static android.support.v4.media.session.PlaybackStateCompat.ERROR_CODE_NOT_AVAILABLE_IN_REGION;
import static android.support.v4.media.session.PlaybackStateCompat.ERROR_CODE_NOT_SUPPORTED;
import static android.support.v4.media.session.PlaybackStateCompat.ERROR_CODE_PARENTAL_CONTROL_RESTRICTED;
import static android.support.v4.media.session.PlaybackStateCompat.ERROR_CODE_PREMIUM_ACCOUNT_REQUIRED;
import static android.support.v4.media.session.PlaybackStateCompat.ERROR_CODE_SKIP_LIMIT_REACHED;
import static android.support.v4.media.session.PlaybackStateCompat.ERROR_CODE_UNKNOWN_ERROR;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ErrorCodePicker extends androidx.appcompat.widget.AppCompatSpinner {

    int mSelectedCode;
    String mSelectedErrorMsg;
    private final HashMap<String, Integer> mErrorCode = new HashMap<String, Integer>()
    {
        {
            put("ERROR_CODE_UNKNOWN_ERROR", ERROR_CODE_UNKNOWN_ERROR);
            put("ERROR_CODE_APP_ERROR", ERROR_CODE_APP_ERROR);
            put("ERROR_CODE_NOT_SUPPORTED", ERROR_CODE_NOT_SUPPORTED);
            put("ERROR_CODE_AUTHENTICATION_EXPIRED", ERROR_CODE_AUTHENTICATION_EXPIRED);
            put("ERROR_CODE_PREMIUM_ACCOUNT_REQUIRED", ERROR_CODE_PREMIUM_ACCOUNT_REQUIRED);
            put("ERROR_CODE_CONCURRENT_STREAM_LIMIT", ERROR_CODE_CONCURRENT_STREAM_LIMIT);
            put("ERROR_CODE_PARENTAL_CONTROL_RESTRICTED", ERROR_CODE_PARENTAL_CONTROL_RESTRICTED);
            put("ERROR_CODE_NOT_AVAILABLE_IN_REGION", ERROR_CODE_NOT_AVAILABLE_IN_REGION);
            put("ERROR_CODE_CONTENT_ALREADY_PLAYING", ERROR_CODE_CONTENT_ALREADY_PLAYING);
            put("ERROR_CODE_SKIP_LIMIT_REACHED", ERROR_CODE_SKIP_LIMIT_REACHED);
            put("ERROR_CODE_ACTION_ABORTED", ERROR_CODE_ACTION_ABORTED);
            put("ERROR_CODE_END_OF_QUEUE", ERROR_CODE_END_OF_QUEUE);
        }
    };


    public ErrorCodePicker(Context context) {
        super(context);
        init(context);
    }

    public ErrorCodePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ErrorCodePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // 创建适配器并设置数据
        List<String> data = new ArrayList<>();
        for (String key : mErrorCode.keySet()) {
            data.add(key);
        }

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(context, data);

        // 将适配器设置到 Spinner
        setAdapter(adapter);

        super.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 获取选中项的值
                String selectedValue = getItemAtPosition(position).toString();

                //Toast.makeText(context, "selected " + selectedValue + ", code=" + mErrorCode.get(selectedValue), Toast.LENGTH_SHORT).show();
                mSelectedCode = mErrorCode.get(selectedValue);
                mSelectedErrorMsg = selectedValue;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 未选中任何项的逻辑

            }
        });
    }

    public void reset() {
        mSelectedCode = -1;
        setSelection(0);
    }

    public String getSeletedErrorMsg() {
        return mSelectedErrorMsg;
    }
    public int getSelectedErrorCode() {
        return mSelectedCode;
    }

    @Override
    public void setSelection(int errorcode) {

        int index = 0;
        for (String key : mErrorCode.keySet()) {
            if (errorcode == mErrorCode.get(key)) {
                super.setSelection(index);
                break;
            }
            index ++;
        }
    }

    private static class CustomSpinnerAdapter extends ArrayAdapter<String> {

        public CustomSpinnerAdapter(Context context, List<String> items) {
            super(context, android.R.layout.simple_spinner_item, items);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            }

            TextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            }

            TextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return convertView;
        }

    }
}
