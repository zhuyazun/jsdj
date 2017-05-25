package com.sum.alchemist.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sum.alchemist.R;
import com.sum.alchemist.model.db.Topic;
import com.sum.xlog.core.XLog;

/**
 * Created by Qiu on 2017/3/4.
 */

public class TopicAdapter extends BaseRecyclerAdapter<Topic>{



    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false));
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {
        final Topic topic = getItem(position);
        try {

            holder.getView(R.id.title, TextView.class).setText(String.valueOf(topic.topic));
            holder.getView(R.id.desc, TextView.class).setText(String.valueOf(topic.desc));
            if(!TextUtils.isEmpty(topic.img))
                Glide.with(holder.itemView.getContext()).load(topic.img).into(holder.getView(R.id.img, ImageView.class));


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasItemListener()) {
                        adapterItemListener.onItemClickListener(topic, holder.getAdapterPosition(), 0);
                    }
                }
            });

        } catch (Exception e) {
            XLog.e("=== 加载话题信息出错===", e);
        }
    }

}
