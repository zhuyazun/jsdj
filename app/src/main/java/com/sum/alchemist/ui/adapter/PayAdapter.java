package com.sum.alchemist.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sum.alchemist.R;
import com.sum.alchemist.model.entity.GoldPackage;

/**
 * Created by Qiu on 2016/11/24.
 */

public class PayAdapter extends BaseRecyclerAdapter<GoldPackage>{

    public int selectId = -1;

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleViewHolder(View.inflate(parent.getContext(), R.layout.item_pay_product, null));
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {
        final GoldPackage goldPackage = getItem(position);

        holder.getView(R.id.sub_title, TextView.class).setText(String.format("售价%s元", goldPackage.price));
        holder.getView(R.id.title, TextView.class).setText(goldPackage.body);
//        holder.itemView.setBackgroundResource(selectId == goldPackage.id?R.drawable.rec_stroke_corners_red2:R.drawable.rec_stroke_corners_grey);
        holder.getView(R.id.background).setSelected(selectId == goldPackage.id);
        if(hasItemListener()){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterItemListener.onItemClickListener(goldPackage, holder.getAdapterPosition(), 0);
                }
            });
        }
    }
}
