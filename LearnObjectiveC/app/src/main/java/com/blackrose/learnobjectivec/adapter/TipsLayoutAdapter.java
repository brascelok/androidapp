/*
 * Copyright (C) 2016 blackrose
 */
package com.blackrose.learnobjectivec.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackrose.learnobjectivec.R;
import com.blackrose.learnobjectivec.gui.TitleProvider;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TipsLayoutAdapter extends BaseAdapter implements TitleProvider {

    private LayoutInflater mInflater;

    public List<String> mlistURL;
    public List<String> mlistText;
    public List<String> mlistTitle;

    private Context _context;

    public TipsLayoutAdapter(Context context, List<String> listTitle, List<String> listText, List<String> listURL) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mlistText = listText;
        this.mlistTitle = listTitle;
        this.mlistURL = listURL;
        this._context = context;
    }

    @Override
    public int getCount() {
        return mlistTitle.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.tips_detail, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.tips_text_description);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.tips_image_view);
            viewHolder.button = (ImageButton) convertView.findViewById(R.id.ib_show_tips);
            viewHolder.imgLeft = (ImageView) convertView.findViewById(R.id.iv_tips_left);
            viewHolder.imgRight = (ImageView) convertView.findViewById(R.id.iv_tips_right);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //set text description and image
        viewHolder.textView.setText(mlistText.get(position));
        Picasso.with(_context)
                .load(mlistURL.get(position))
                .placeholder(R.drawable.tips_placeholder)
                .error(R.drawable.tips_noimage)
                .into(viewHolder.imageView);
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            viewHolder.imgLeft.setVisibility(View.VISIBLE);
            viewHolder.imgRight.setVisibility(View.VISIBLE);
            viewHolder.textView.setVisibility(View.VISIBLE);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
        ImageButton button;
        ImageView imgLeft, imgRight;
    }

    @Override
    public String getTitle(int position) {
        return mlistTitle.get(position);
    }

}
