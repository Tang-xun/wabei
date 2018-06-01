package com.wabei.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wabei.R;
import com.wabei.ui.ChatFragment;

import java.util.List;


public class ChatRecorderAdapter extends ArrayAdapter<ChatFragment.ChatRecorder> {

    private List<ChatFragment.ChatRecorder> mDatas;

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public ChatRecorderAdapter(Context context, List<ChatFragment.ChatRecorder> datas) {
        super(context, -1, datas);
        this.mContext = context;
        this.mDatas = datas;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    class ViewHolder {
        ImageView headerIv;
        TextView nickNameTv;
        TextView lastMsg;
        TextView lastTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.chat_list_item, parent, false);
            holder.headerIv = (ImageView) convertView.findViewById(R.id.chat_list_item_hearder);
            holder.nickNameTv = (TextView) convertView.findViewById(R.id.chat_list_item_name);
            holder.lastMsg = (TextView) convertView.findViewById(R.id.chat_list_item_cotent);
            holder.lastTime = (TextView) convertView.findViewById(R.id.chat_list_item_time);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ChatFragment.ChatRecorder recorder = mDatas.get(position);
        holder.headerIv.setImageResource(R.drawable.header_icon_right);
        holder.nickNameTv.setText(recorder.getNickName());
        holder.lastMsg.setText(recorder.getLastMsg());
        holder.lastTime.setText(recorder.getLastTime());

        convertView.setTag(holder);
        mDatas.get(position).getHeader();
        return convertView;
    }
}
