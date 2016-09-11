package com.scripton.in.test;

/**
 * Created by Nitin on 10/19/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<String> {
    customButtonListener customListner;

    public interface customButtonListener {
        public void onButtonClickListnerID(int position, String value);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }
    public void setData(ArrayList<String> id, int[] img){
        this.id = id;
        this.img = img;
    }

    private Context context;
    private ArrayList<String> id = new ArrayList<String>();
    int[] img ;

    public ListAdapter(Context context, ArrayList<String> id, int[] img) {
        super(context, R.layout.child_listview, id);
        this.id = id;
        this.context = context;
        this.img = img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.child_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.tv = (TextView) convertView
                    .findViewById(R.id.tv);
            viewHolder.iv = (ImageView) convertView
                    .findViewById(R.id.iv);
            viewHolder.lay = (LinearLayout) convertView
                    .findViewById(R.id.lay);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String temp = getItem(position);
        viewHolder.tv.setText(temp);
        viewHolder.iv.setImageResource(img[position]);



        viewHolder.lay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListnerID(position,temp);
                }

            }
        });


        return convertView;
    }

    public class ViewHolder {
        TextView tv;
        ImageView iv;
        LinearLayout lay;
    }
}