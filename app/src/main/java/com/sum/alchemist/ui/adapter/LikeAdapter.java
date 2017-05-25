package com.sum.alchemist.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sum.alchemist.R;
import com.sum.alchemist.model.entity.Provision;
import com.sum.alchemist.model.entity.Requirement;
import com.sum.xlog.core.XLog;

/**
 * Created by Qiu on 2016/11/3.
 */

public class LikeAdapter extends BaseRecyclerAdapter<Object> {
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleViewHolder(View.inflate(parent.getContext(), R.layout.item_mission2, null));
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {
        final Object object = getItem(position);
        try {
            if(object instanceof Provision) {
                Provision provision = (Provision) object;
                ((TextView)holder.getView(R.id.mission_title)).setText(provision.title);
                ((TextView)holder.getView(R.id.mission_date)).setText(provision.updated_at);
                holder.getView(R.id.mission_detail, TextView.class).setText(provision.desc);
                holder.getView(R.id.see_number, TextView.class).setText(String.valueOf(provision.show_times));
                holder.getView(R.id.like_number, TextView.class).setText(String.valueOf(provision.like_times));
                ImageView imageView = holder.getView(R.id.logo_img);
                Glide.with(imageView.getContext()).load(provision.logo_img).into(imageView);
            }else if(object instanceof Requirement) {
                Requirement requirement = (Requirement) object;
                ((TextView)holder.getView(R.id.mission_title)).setText(requirement.title);
                ((TextView)holder.getView(R.id.mission_date)).setText(requirement.updated_at);
                holder.getView(R.id.mission_detail, TextView.class).setText(requirement.desc);
                holder.getView(R.id.see_number, TextView.class).setText(String.valueOf(requirement.show_times));
                holder.getView(R.id.like_number, TextView.class).setText(String.valueOf(requirement.like_times));
                ImageView imageView = holder.getView(R.id.logo_img);
                Glide.with(imageView.getContext()).load(requirement.logo_img).into(imageView);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(hasItemListener()){
                        adapterItemListener.onItemClickListener(object, holder.getAdapterPosition(), 0);
                    }
                }
            });

        } catch (Exception e) {
            XLog.e("=== 加载收藏信息出错===", e);
        }
    }
}
