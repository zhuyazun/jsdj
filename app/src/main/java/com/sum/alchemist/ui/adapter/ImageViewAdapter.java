package com.sum.alchemist.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sum.alchemist.R;

import java.util.List;

/**
 * Created by Qiu on 2016/11/10.
 */

public class ImageViewAdapter extends BaseAdapter{
    private List<String> lists;

    private AdapterItemListener<String> itemListener;

    public void setData(List<String> lists) {
        this.lists = lists;
    }

    public void setItemListener(AdapterItemListener<String> itemListener){
        this.itemListener = itemListener;
    }

    public void insetData(String string){
        if(lists != null)
            lists.add(0, string);
    }

    public List<String> getLists(){
        return lists;
    }

    public void removeData(int index){
        if(lists != null)
            lists.remove(index);
    }

    @Override
    public int getCount() {
        if (lists != null)
            return lists.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (position < getCount())
            return lists.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        final String imageUrl = (String) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(parent.getContext(), R.layout.item_image, null);
            holder.image = (ImageView)convertView.findViewById(R.id.image_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(imageUrl.startsWith("drawable://")){
            Glide.with(convertView.getContext()).load(Integer.valueOf(imageUrl.substring(11))).into(holder.image);
        }else{
            Glide.with(convertView.getContext()).load(imageUrl).into(holder.image);
        }



        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemListener != null){
                    itemListener.onItemClickListener(imageUrl, position, 0);
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        private ImageView image;
    }
}
