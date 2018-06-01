package com.wabei.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.wabei.R;
import com.wabei.adapter.ChatRecorderAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private ListView mChatLv;

    private ChatRecorderAdapter mChatRecorderAdapter;

    private List<ChatRecorder> mDatas = new ArrayList<>();

    private static final String TAG = "TANK";

    public class ChatRecorder {
        String header;
        String nickName;
        String lastMsg;
        String lastTime;

        public ChatRecorder(String header, String nickName, String lastMsg, String lastTime) {
            super();
            this.header = header;
            this.nickName = nickName;
            this.lastMsg = lastMsg;
            this.lastTime = lastTime;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getLastMsg() {
            return lastMsg;
        }

        public void setLastMsg(String lastMsg) {
            this.lastMsg = lastMsg;
        }

        public String getLastTime() {
            return lastTime;
        }

        public void setLastTime(String lastTime) {
            this.lastTime = lastTime;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        View root = inflater.inflate(R.layout.fragment_chat, container, false);
        initView(root);
        initData();
        return root;
    }

    private void initData() {

        mChatRecorderAdapter = new ChatRecorderAdapter(getActivity(), mDatas);
        Log.i(TAG, "initData() " + mDatas.size());
        if (mDatas.size() == 0) {
            for (int i = 0; i < 10; i++) {
                mDatas.add(new ChatRecorder("", "nickName_" + i, "lastMsg_" + i, "lastTime_" + i));
            }
        }
        mChatLv.setAdapter(mChatRecorderAdapter);
        mChatRecorderAdapter.notifyDataSetChanged();
    }

    private void initView(View root) {
        mChatLv = root.findViewById(R.id.chat_fragment_chat_list);
        mChatLv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), com.wabei.ChatViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("nickName", mDatas.get(position).getNickName());
                bundle.putString("lastMsg", mDatas.get(position).getLastMsg());
                bundle.putString("lastTime", mDatas.get(position).getLastTime());
                intent.putExtras(bundle);
                startActivity(intent);
                Toast.makeText(getActivity(), mDatas.get(position).getNickName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}