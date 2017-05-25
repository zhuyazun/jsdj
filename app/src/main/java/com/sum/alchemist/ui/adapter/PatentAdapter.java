package com.sum.alchemist.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sum.alchemist.R;
import com.sum.alchemist.model.entity.Patent;
import com.sum.xlog.core.XLog;

/**
 * Created by Qiu on 2016/11/3.
 */

public class PatentAdapter extends BaseRecyclerAdapter<Patent> {
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleViewHolder(View.inflate(parent.getContext(), R.layout.item_patent, null));
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {
        final Patent patent = getItem(position);
        try {
            ((TextView)holder.getView(R.id.title)).setText(patent.getTitle());
            holder.getView(R.id.price, TextView.class).setText(patent.getPrice());
            ImageView imageView = holder.getView(R.id.image_view);
            Glide.with(imageView.getContext()).load(patent.getLogo_img()).into(imageView);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(hasItemListener()){
                        adapterItemListener.onItemClickListener(patent, holder.getAdapterPosition(), 0);
                    }
                }
            });

        } catch (Exception e) {
            XLog.e("=== 加载专利信息出错===", e);
        }
    }

}
