package com.wabei.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wabei.ChatViewActivity;
import com.wabei.R;
import com.wabei.utils.StringUtils;

import java.util.List;

public class RecorderAdapter extends ArrayAdapter<ChatViewActivity.Recorder> {

    private List<ChatViewActivity.Recorder> mDatas;
    private Context mContext;

    private int mMinItemWidth;
    private int mMaxItemWidth;

    private LayoutInflater mLayoutInflater;
    private String TAG = "TANK";

    public RecorderAdapter(Context context, List<ChatViewActivity.Recorder> datas) {
        super(context, -1, datas);
        this.mDatas = datas;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        getWinodwWidth();

    }

    private void getWinodwWidth() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWidth = (int) (outMetrics.widthPixels * 0.15f);
    }

    private class ViewHolder {
        TextView seconds;
        View length;
        TextView msg;
        View recordView;
    }

    @Override
    public ChatViewActivity.Recorder getItem(int position) {
        return super.getItem(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.recorder_item, parent, false);
            holder = new ViewHolder();
            holder.seconds = convertView.findViewById(R.id.recorder_time);
            holder.length = convertView.findViewById(R.id.record_lenght);
            holder.msg = convertView.findViewById(R.id.recorder_msg);
            holder.recordView = convertView.findViewById(R.id.recorder_anim);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mDatas.get(position).getMsg() != null) {
            String msg = mDatas.get(position).getMsg();
            holder.msg.setText(msg);
            ViewGroup.LayoutParams lp = holder.length.getLayoutParams();
            int chineseCount = StringUtils.calChinaeseCount(msg);
            int charSize = chineseCount * 2 + (msg.length() - chineseCount);
            Log.i(TAG, "chinaeseCount : " + chineseCount + " charSize:" + charSize);
            lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 40f * charSize));
            Log.i(TAG, "lp.width" + lp.width + " mMinItemWidth" + mMinItemWidth + " mMaxItemwidth " + mMaxItemWidth
                    + " msg.length" + mDatas.get(position).getMsg().length());
            holder.msg.setVisibility(View.VISIBLE);
            holder.seconds.setVisibility(View.GONE);
            holder.recordView.setVisibility(View.GONE);
        } else {
            holder.seconds.setText(Math.round(getItem(position).getTime()) + "\"");
            ViewGroup.LayoutParams lp = holder.length.getLayoutParams();
            lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * getItem(position).getTime()));
            holder.msg.setVisibility(View.GONE);
            holder.seconds.setVisibility(View.VISIBLE);
            holder.recordView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

}
