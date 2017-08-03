package com.winmobi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.winmobi.R;
import com.winmobi.bean.JPush;
import com.winmobi.helper.FormatHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linguobiao on 16/8/20.
 */
public class MsgListAdapter extends BaseAdapter{

    private Context context;
    private List<JPush> lists;

    public MsgListAdapter(Context context, List<JPush> lists) {
        if (lists != null) {
            this.lists = lists;
        } else {
            this.lists = new ArrayList<JPush>();
        }
        this.context = context;
    }

    public void notifymDataSetChanged(List<JPush> lists){
        if(lists!=null){
            this.lists = lists;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.view_item_msg, null);
            holder.txt_msg = (TextView) convertView.findViewById(R.id.txt_msg);
            holder.txt_time = (TextView) convertView.findViewById(R.id.txt_time);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        JPush jPush = lists.get(i);
        if (jPush != null) {
            holder.txt_msg.setText(jPush.getContent());
            holder.txt_time.setText(FormatHelper.sdf_MM_dd_HH_mm.format(jPush.getDate().getTime()));
        }
        return convertView;
    }

    public class Holder {
        public TextView txt_msg;
        public TextView txt_time;
    }
}
