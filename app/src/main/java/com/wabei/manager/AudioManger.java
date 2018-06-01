package com.wabei.manager;

import android.media.MediaRecorder;

import java.io.File;
import java.util.UUID;

/**
 * @author tangxun E-mail:tangxun@pingan.com.cn
 * @version 1.1.0
 */
public class AudioManger {

    /**
     * 回调准备结束
     */
    public interface AudioStateListener {
        void wellPrepared();
    }

    private MediaRecorder mMediaRecorder;
    private String mDir;

    private String mCurrentFilePath;
    private AudioStateListener mListener;
    private boolean isPrepared;

    private static final String TAG = "TANK";

    private AudioManger(String dir) {
        mDir = dir;
    };

    private static AudioManger mInstance;

    public static AudioManger getInstance(String dir) {
        if (mInstance == null) {
            synchronized (AudioManger.class) {
                if (mInstance == null) {
                    mInstance = new AudioManger(dir);
                }
            }
        }

        return mInstance;
    }

    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

    public void prepareAudio() {
        isPrepared = false;
        File dir = new File(mDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = generateFileName();
        File file = new File(dir, fileName);
        mCurrentFilePath = file.getAbsolutePath();
        try {
            mMediaRecorder = new MediaRecorder();
            // 设置输出文件，绝对路径
            mMediaRecorder.setOutputFile(mCurrentFilePath);
            // 设置音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            // 设置音频编码
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isPrepared = true;
            if (mListener != null) {
                mListener.wellPrepared();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 随机生成文件名称
     * 
     * @return
     */
    private String generateFileName() {

        return UUID.randomUUID().toString() + ".amr";
    }

    /**
     * @param maxLevel
     * @return
     */
    public int getVoiceLevel(int maxLevel) {
        try {
            if (isPrepared) {
                // mMediaRecorder.getMaxAmplitude() return (1-32767)
                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            }
        } catch (Exception e) {
        }
        return 1;
    }

    public void release() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    public void cancel() {
        release();
        try {
            if (mCurrentFilePath != null) {
                File file = new File(mCurrentFilePath);
                file.delete();
                mCurrentFilePath = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

}