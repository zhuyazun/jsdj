package com.sum.alchemist.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sum.alchemist.R;
import com.sum.alchemist.model.entity.Label;
import com.sum.alchemist.model.entity.News;
import com.sum.alchemist.model.entity.Provision;
import com.sum.alchemist.model.entity.Requirement;

/**
 * Created by Qiu on 2016/10/19.
 */
public class HomeAdapter extends BaseRecyclerAdapter<Object> {


    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SimpleViewHolder simpleViewHolder = super.onCreateViewHolder(parent, viewType);
        if(simpleViewHolder != null)
            return simpleViewHolder;
        if(viewType == R.layout.item_mission2){
            simpleViewHolder = new SimpleViewHolder(View.inflate(parent.getContext(), R.layout.item_mission2, null));
        }else if(viewType == R.layout.item_news){
            simpleViewHolder = new SimpleViewHolder(View.inflate(parent.getContext(), R.layout.item_news, null));
        }else if(viewType == R.layout.item_title){
            simpleViewHolder = new SimpleViewHolder(View.inflate(parent.getContext(), R.layout.item_title, null));
        }else if(viewType == R.layout.item_more){
            simpleViewHolder = new SimpleViewHolder(View.inflate(parent.getContext(), R.layout.item_more, null));
        }
        return simpleViewHolder;
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {
        final Object object = getItem(position);
        if(object instanceof Label) {
            Label label = (Label) object;
            if(!TextUtils.isEmpty(label.name)){
                ((TextView)holder.getView(R.id.text_view)).setText(label.name);
                holder.getView(R.id.logo_img, ImageView.class).setImageResource(label.icon);
            }
        }else if(object instanceof Provision){
            Provision mission = (Provision) object;
            ((TextView)holder.getView(R.id.mission_title)).setText(mission.title);
            ((TextView)holder.getView(R.id.mission_date)).setText(mission.updated_at);
            holder.getView(R.id.mission_detail, TextView.class).setText(mission.desc);
            holder.getView(R.id.see_number, TextView.class).setText(String.valueOf(mission.show_times));
            holder.getView(R.id.like_number, TextView.class).setText(String.valueOf(mission.like_times));
            ImageView imageView = (ImageView) holder.getView(R.id.logo_img);
            Glide.with(imageView.getContext()).load(mission.logo_img).into(imageView);
        }else if(object instanceof Requirement){
            Requirement mission = (Requirement) object;
            ((TextView)holder.getView(R.id.mission_title)).setText(mission.title);
            ((TextView)holder.getView(R.id.mission_date)).setText(mission.updated_at);
            holder.getView(R.id.mission_detail, TextView.class).setText(mission.desc);
            holder.getView(R.id.see_number, TextView.class).setText(String.valueOf(mission.show_times));
            holder.getView(R.id.like_number, TextView.class).setText(String.valueOf(mission.like_times));
            ImageView imageView = (ImageView) holder.getView(R.id.logo_img);
            Glide.with(imageView.getContext()).load(mission.logo_img).into(imageView);
        }else if(object instanceof News){
            News news = (News) object;
            ((TextView)holder.getView(R.id.news_title)).setText(news.title);
            ((TextView)holder.getView(R.id.news_date)).setText(news.updated_at);
            ((TextView)holder.getView(R.id.news_detail)).setText(news.desc);
            holder.getView(R.id.see_number, TextView.class).setText(String.valueOf(news.show_times));
            holder.getView(R.id.like_number, TextView.class).setText(String.valueOf(news.like_times));
            ImageView imageView = (ImageView) holder.getView(R.id.logo_img);
            Glide.with(imageView.getContext()).load(news.logo_img).into(imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasItemListener()){
                    adapterItemListener.onItemClickListener(object, holder.getAdapterPosition(), 0);
                }
            }
        });

    }

    @Override
    public int getItemViewType(int position) {

        Object object = getItem(position);
        if(object instanceof Label) {
            Label label = (Label) object;
            return label.type == 0?R.layout.item_title:R.layout.item_more;
        }
        if(object instanceof Requirement)
            return R.layout.item_mission2;
        if(object instanceof Provision)
            return R.layout.item_mission2;
        if(object instanceof News)
            return R.layout.item_news;
        return super.getItemViewType(position);
    }



}
