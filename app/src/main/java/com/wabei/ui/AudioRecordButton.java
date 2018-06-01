package com.wabei.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wabei.R;
import com.wabei.manager.AudioManger;
import com.wabei.manager.AudioManger.AudioStateListener;
import com.wabei.manager.DialogManager;

public class AudioRecordButton extends android.support.v7.widget.AppCompatButton implements AudioStateListener {

    private static final int STATE_NORMAL = 0x00001;

    private static final int STATE_RECORDING = 0x00002;

    private static final int STATE_CANCEL = 0x00003;

    private int mCurrentState;

    private boolean isRecording = false;

    private static final int DISTANCE_CANCEL_Y = 50;

    private DialogManager mDialog;

    private long lasttime = 0l;

    private static final String TAG = "TANK";

    private AudioManger mAudioManager;

    private float mRecordTime;

    private boolean isAllReady;

    public AudioRecordButton(Context context) {
        this(context, null);
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRecordButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDialog = new DialogManager(getContext());
        String dir = Environment.getExternalStorageDirectory() + "/webchat_recorder_audios";

        mAudioManager = AudioManger.getInstance(dir);
        mAudioManager.setOnAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                isAllReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    /**
     * 录音结束时回调
     */
    public interface AudioFinishRecordListener {
        void onFinish(float sencond, String filePath);
    }

    private AudioFinishRecordListener mFinishRecordListener;

    public void setFinishRecordListener(AudioFinishRecordListener finishRecordListener) {
        this.mFinishRecordListener = finishRecordListener;
    }

    @Override
    public void wellPrepared() {
        mHander.sendEmptyMessage(MSG_AUDIO_PREPARE);
    }

    public static final int MSG_AUDIO_PREPARE = 0x0001;
    public static final int MSG_VOICE_CHANGE = 0x0010;
    public static final int MSG_DIALOG_DIMISS = 0x0011;

    @SuppressLint("HandlerLeak")
    private Handler mHander = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARE:
                    mDialog.showRecordingDialog();
                    isRecording = true;

                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGE:
                    mDialog.updateVoiceLeve(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DIMISS:
                    mDialog.dimissDialog();
                    break;

                default:
                    break;
            }

        };
    };

    private Runnable mGetVoiceLevelRunnable = new Runnable() {

        @Override
        public void run() {
            while (isRecording) {
                try {
                    mHander.sendEmptyMessage(MSG_VOICE_CHANGE);
                    Thread.sleep(100);
                    mRecordTime += 0.1f;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "ACTION_DOWN RECORDING");
                changeState(STATE_RECORDING);
                break;

            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    if (wantCancel(x, y)) {
                        changeState(STATE_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isAllReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!isRecording || mRecordTime < 0.6f) {
                    mDialog.tooShort();
                    mAudioManager.cancel();
                    // 显示dialog1.3s后关闭
                    mHander.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
                } else if (mCurrentState == STATE_RECORDING) {
                    Log.i(TAG, "ACTION_UP RECORDING");
                    mDialog.dimissDialog();
                    mAudioManager.release();
                    if (mFinishRecordListener != null) {
                        Log.i(TAG, mRecordTime + "" + mAudioManager.getCurrentFilePath());
                        mFinishRecordListener.onFinish(mRecordTime, mAudioManager.getCurrentFilePath());
                    }

                } else if (mCurrentState == STATE_CANCEL) {
                    Log.i(TAG, "ACTION_UP CANCEL");
                    mDialog.dimissDialog();
                    mAudioManager.cancel();
                }
                reset();
                break;

            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 状态充值
     */
    private void reset() {
        mRecordTime = 0.0f;
        isAllReady = false;
        isRecording = false;
        changeState(STATE_NORMAL);
    }

    /**
     * 判断用户是否想取消
     * 
     * @param x
     * @param y
     * @return
     */
    private boolean wantCancel(float x, float y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }
        if (y < -DISTANCE_CANCEL_Y || y > getHeight() + DISTANCE_CANCEL_Y) {
            return true;
        }
        return false;
    }

    private void changeState(int stateRecording) {
        if (mCurrentState != stateRecording) {
            mCurrentState = stateRecording;
            switch (stateRecording) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.chat_record_bt_bg_normal);
                    setText(R.string.chat_record_nomal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.chat_record_bt_bg_recording);
                    setText(R.string.chat_record_recording);
                    if (isRecording) {
                        mDialog.recording();
                    }
                    break;
                case STATE_CANCEL:
                    setBackgroundResource(R.drawable.chat_record_bt_bg_recording);
                    setText(R.string.chat_record_cancel);
                    mDialog.wantCancel();
                    break;
                default:
                    break;
            }
        }
    }

}