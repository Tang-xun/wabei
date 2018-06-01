package com.wabei;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.wabei.adapter.RecorderAdapter;
import com.wabei.manager.MediaManager;
import com.wabei.ui.AudioRecordButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressLint("Registered")
public class ChatViewActivity extends Activity implements OnClickListener {

    public class Recorder {
        float time;
        String filePath;
        String msg;

        Recorder(float time, String filePath) {
            super();
            this.time = time;
            this.filePath = filePath;
        }

        public Recorder(String msg) {
            this.msg = msg;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getMsg() {
            return msg;
        }

        public float getTime() {
            return time;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public void setTime(float time) {
            this.time = time;
        }

    }

    private ListView mListView;
    private ImageView mInputKeyBoardIv;
    private ImageView mInputVoiceIv;
    private Button mInputSendBt;
    private LinearLayout mLinearLayoutInputVoice;

    private LinearLayout mLinearLayoutInputText;
    private ArrayAdapter<Recorder> mAdapter;

    private List<Recorder> mDatas = new ArrayList<Recorder>();
    private AudioRecordButton mAudioRecordButton;
    View mAnimView = null;
    private String TAG = "TANK";
    private Context mContext;

    private EditText mInputEditorText;

    private void initView() {
        Log.i(TAG, "initView()");
        mListView = (ListView) findViewById(R.id.chat_listview);
        mInputKeyBoardIv = (ImageView) findViewById(R.id.chat_input_keyboard);
        mInputVoiceIv = (ImageView) findViewById(R.id.chat_input_voice);
        mInputSendBt = (Button) findViewById(R.id.chat_input_send);
        mInputEditorText = (EditText) findViewById(R.id.chat_record_et);
        mLinearLayoutInputVoice = (LinearLayout) findViewById(R.id.input_layout_voice);
        mLinearLayoutInputText = (LinearLayout) findViewById(R.id.input_layout_text);
        mInputSendBt.setOnClickListener(this);
        mInputKeyBoardIv.setOnClickListener(this);
        mInputVoiceIv.setOnClickListener(this);
        mAudioRecordButton = (AudioRecordButton) findViewById(R.id.chat_record_bt);
        mAudioRecordButton.setFinishRecordListener(new AudioRecordButton.AudioFinishRecordListener() {

            @Override
            public void onFinish(float sencond, String filePath) {
                Log.i(TAG, "onFinish " + sencond + " " + filePath);
                sendVoiceMsg(sencond, filePath);
            }
        });
        mAdapter = new RecorderAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDatas.get(position).getMsg() != null) {
                    Toast.makeText(mContext, mDatas.get(position).getMsg(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // 先停止正在播放的动画
                if (mAnimView != null) {
                    mAnimView.setBackgroundResource(R.drawable.adj);
                    mAnimView = null;
                }

                // 播放动画
                mAnimView = view.findViewById(R.id.recorder_anim);
                mAnimView.setBackgroundResource(R.drawable.play_anim);
                AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
                anim.start();
                // 播放音频
                MediaManager.playSound(mDatas.get(position).filePath, new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAnimView.setBackgroundResource(R.drawable.adj);
                    }
                });

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_input_keyboard:
                mLinearLayoutInputText.setVisibility(View.VISIBLE);
                mLinearLayoutInputVoice.setVisibility(View.GONE);
                break;
            case R.id.chat_input_voice:
                mLinearLayoutInputText.setVisibility(View.GONE);
                mLinearLayoutInputVoice.setVisibility(View.VISIBLE);
                break;
            case R.id.chat_input_send:
                sendTextMsg();
            default:
                break;
        }
    }

    /**
     * 发送文本信息
     */
    private void sendTextMsg() {
        String msg = mInputEditorText.getText().toString();
        // 检测空消息串，如果为空则不发送
        if (msg.length() == 0) {
            Toast.makeText(this, "发送的信息为空，发送失败", Toast.LENGTH_SHORT).show();
        }
        Recorder recorder = new Recorder(msg);
        mDatas.add(recorder);
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(mDatas.size() - 1);
        Toast.makeText(this, mInputEditorText.getText().toString(), Toast.LENGTH_SHORT).show();
        mInputEditorText.setText("");
        // 隐藏输入法键盘
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                mInputEditorText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 发送语音消息
     */
    private void sendVoiceMsg(float sencond, String filePath) {
        Recorder recorder = new Recorder(sencond, filePath);
        mDatas.add(recorder);
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(mDatas.size() - 1);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_chat);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            String nickName = bundle.getString("nickName");

            Objects.requireNonNull(getActionBar()).setIcon(R.drawable.ic_back);
            Objects.requireNonNull(getActionBar()).setDisplayShowHomeEnabled(true);
            Objects.requireNonNull(getActionBar()).setTitle(nickName);

        }
        initView();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pasue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

}
