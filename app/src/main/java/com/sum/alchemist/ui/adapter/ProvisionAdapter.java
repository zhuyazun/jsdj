package com.sum.alchemist.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sum.alchemist.R;
import com.sum.alchemist.model.entity.Provision;

/**
 * Created by Qiu on 2016/10/22.
 */

public class ProvisionAdapter extends BaseRecyclerAdapter<Provision>{

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleViewHolder(View.inflate(parent.getContext(), R.layout.item_mission2, null));
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        final Provision provision = getItem(position);
        ((TextView)holder.getView(R.id.mission_title)).setText(provision.title);
        ((TextView)holder.getView(R.id.mission_date)).setText(provision.updated_at);
        holder.getView(R.id.mission_detail, TextView.class).setText(provision.desc);
        holder.getView(R.id.see_number, TextView.class).setText(String.valueOf(provision.show_times));
        holder.getView(R.id.like_number, TextView.class).setText(String.valueOf(provision.like_times));
        ImageView imageView = (ImageView) holder.getView(R.id.logo_img);
        Glide.with(imageView.getContext()).load(provision.logo_img).into(imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasItemListener()){
                    adapterItemListener.onItemClickListener(provision, position, 0);
                }
            }
        });
    }
}
