package com.sum.alchemist.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sum.alchemist.R;
import com.sum.alchemist.model.entity.Message;

/**
 * Created by Qiu on 2016/11/12.
 */

public class MessageAdapter extends BaseRecyclerAdapter<Message> {
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleViewHolder(View.inflate(parent.getContext(), R.layout.item_message, null));
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        Message message = getItem(position);
        holder.getView(R.id.message_content, TextView.class).setText(message.content);
        holder.getView(R.id.message_time, TextView.class).setText(message.updated_at);
    }
}
