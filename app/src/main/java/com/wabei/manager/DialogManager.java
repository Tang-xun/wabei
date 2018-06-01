package com.wabei.manager;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wabei.R;


public class DialogManager {

    private Dialog mDialog;
    private ImageView mVoiceIcon;
    private ImageView mVoiceLevel;
    private TextView mVoiceTv;

    private Context mContext;
    private static final String TAG = "DialogManager";

    public DialogManager(Context context) {
        this.mContext = context;
    }

    public void showRecordingDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.recorder_dialog, null);
        mDialog = new Dialog(mContext, R.style.AudioDialogTheme);
        mDialog.setContentView(view);
        mVoiceIcon = mDialog.findViewById(R.id.chat_dialog_voice);
        mVoiceLevel = mDialog.findViewById(R.id.chat_dialog_voice_level);
        mVoiceTv = mDialog.findViewById(R.id.chat_dialog_tv);
        mDialog.show();
        Log.i(TAG, "showRecordingDialog()");
    }

    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            Log.i(TAG, "recording()");
            mVoiceIcon.setVisibility(View.VISIBLE);
            mVoiceIcon.setImageResource(R.drawable.chat_record_icon);
            mVoiceTv.setVisibility(View.VISIBLE);
            mVoiceLevel.setVisibility(View.VISIBLE);
            mVoiceTv.setText(R.string.chat_record_recording);
        }
    }

    public void wantCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            Log.i(TAG, "wantCancel()");
            mVoiceIcon.setVisibility(View.VISIBLE);
            mVoiceTv.setVisibility(View.VISIBLE);
            mVoiceIcon.setImageResource(R.drawable.chat_dialog_cancel);
            mVoiceLevel.setVisibility(View.GONE);
            mVoiceTv.setText(R.string.chat_record_cancel);
        }
    }

    public void tooShort() {
        Log.i(TAG, "tooShort()");
        if (mDialog != null && mDialog.isShowing()) {
            Log.i(TAG, "tooShort()");
            mVoiceLevel.setVisibility(View.GONE);
            mVoiceIcon.setVisibility(View.VISIBLE);
            mVoiceTv.setVisibility(View.VISIBLE);
            mVoiceIcon.setImageResource(R.drawable.chat_voice_too_short);
            mVoiceTv.setText(R.string.chat_dialog_too_short);
        }
    }

    public void dimissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void updateVoiceLeve(int level) {
        if (mDialog != null && mDialog.isShowing()) {
            int resId = mContext.getResources().getIdentifier("chat_voice_level_" + level, "drawable",
                    mContext.getPackageName());
            mVoiceLevel.setImageResource(resId);
        }
    }
}
