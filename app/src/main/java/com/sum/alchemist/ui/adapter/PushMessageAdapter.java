package com.sum.alchemist.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sum.alchemist.R;
import com.sum.alchemist.model.entity.PushMsg;
import com.sum.alchemist.utils.StringUtil;

/**
 * Created by Administrator on 2016/6/8.
 */
public class PushMessageAdapter extends BaseRecyclerAdapter<PushMsg>{


    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_push_message, parent, false));
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {
        final PushMsg pushMsg = getItem(position);
        holder.getView(R.id.message_title, TextView.class).setText(pushMsg.getTitle());
        holder.getView(R.id.message_content, TextView.class).setText(StringUtil.filter(pushMsg.getContent(), "<.*?>"));
        holder.getView(R.id.message_time, TextView.class).setText(pushMsg.getReceiverTime());

        if(hasItemListener()) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterItemListener.onItemClickListener(pushMsg, holder.getAdapterPosition(), 0);
                }
            });
        }
    }
}
