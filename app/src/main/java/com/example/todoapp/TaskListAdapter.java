package com.example.todoapp;

import android.app.Activity;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskListAdapter extends BaseAdapter {
    private Activity mActivity;
    private ArrayList<HashMap<String, String>> mData;

    TaskListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        mActivity = a;
        mData=d;
    }
    public int getCount() {
        return mData.size();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ListTaskViewHolder holder;
        if (convertView == null) {
            holder = new ListTaskViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(
                    R.layout.list_row_task, parent, false);
            holder.task_image = convertView.findViewById(R.id.task_image);
            holder.task_name = convertView.findViewById(R.id.task_name);
            holder.task_date = convertView.findViewById(R.id.task_date);
            holder.task_time = convertView.findViewById(R.id.task_time);
            holder.task_desc = convertView.findViewById(R.id.task_desc);
            convertView.setTag(holder);
        } else {
            holder = (ListTaskViewHolder) convertView.getTag();
        }
        holder.task_image.setId(position);
        holder.task_name.setId(position);
        holder.task_date.setId(position);
        holder.task_time.setId(position);
        holder.task_desc.setId(position);

        HashMap<String, String> mSong;
        mSong = mData.get(position);

        try{
            holder.task_name.setText(mSong.get(TaskHome.KEY_TASK));
            holder.task_date.setText(mSong.get(TaskHome.KEY_DATE));
            holder.task_time.setText(mSong.get(TaskHome.KEY_TIME));
            holder.task_desc.setText(mSong.get(TaskHome.KEY_DESC));

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(getItem(position));
            holder.task_image.setTextColor(color);

            if (Build.VERSION.SDK_INT >= 24) {
                holder.task_image.setText(Html.fromHtml("&#11044;", Build.VERSION.SDK_INT)); // for 24 api and more
            } else {
                holder.task_image.setText(Html.fromHtml("&#11044;")); // or for older api
            }

        }catch(Exception ignored) {

        }
        return convertView;
    }
}

class ListTaskViewHolder {
    TextView task_image;
    TextView task_name, task_date, task_time, task_desc;
}
