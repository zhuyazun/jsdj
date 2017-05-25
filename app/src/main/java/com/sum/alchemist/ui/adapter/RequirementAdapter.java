package com.sum.alchemist.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sum.alchemist.R;
import com.sum.alchemist.model.entity.Requirement;

/**
 * Created by Qiu on 2016/10/22.
 */

public class RequirementAdapter extends BaseRecyclerAdapter<Requirement>{

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleViewHolder(View.inflate(parent.getContext(), R.layout.item_mission2, null));
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {
        final Requirement requirement = getItem(position);
        ((TextView)holder.getView(R.id.mission_title)).setText(requirement.title);
        ((TextView)holder.getView(R.id.mission_date)).setText(requirement.updated_at);
        holder.getView(R.id.mission_detail, TextView.class).setText(requirement.desc);
        holder.getView(R.id.see_number, TextView.class).setText(String.valueOf(requirement.show_times));
        holder.getView(R.id.like_number, TextView.class).setText(String.valueOf(requirement.like_times));
        ImageView imageView = (ImageView) holder.getView(R.id.logo_img);
        Glide.with(imageView.getContext()).load(requirement.logo_img).into(imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasItemListener()){
                    adapterItemListener.onItemClickListener(requirement, holder.getAdapterPosition(), 0);
                }
            }
        });
    }
}
