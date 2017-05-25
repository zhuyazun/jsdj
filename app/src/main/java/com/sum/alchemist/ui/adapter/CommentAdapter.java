package com.sum.alchemist.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sum.alchemist.R;
import com.sum.alchemist.model.entity.Forum;
import com.sum.xlog.core.XLog;

/**
 * Created by Qiu on 2016/11/12.
 */

public class CommentAdapter extends BaseRecyclerAdapter<Forum.CommentsBean> {

    public CommentAdapter(CommentCall commentCall) {
        this.commentCall = commentCall;
    }

    public interface CommentCall{
        void setCommentCount(int count);
    }

    private CommentCall commentCall;

    private TextView likeTv;

    @Override
    public int getItemCount() {
        int count = super.getItemCount();
        if(commentCall != null)
            commentCall.setCommentCount(count - 1);
        return count;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SimpleViewHolder simpleViewHolder = super.onCreateViewHolder(parent, viewType);
        return simpleViewHolder != null ? simpleViewHolder:new SimpleViewHolder(View.inflate(parent.getContext(), R.layout.item_comment, null));
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {
        final Forum.CommentsBean commentsBean = getItem(position);
        if(commentsBean == null)
            return;
        try{
            ImageView avatar = holder.getView(R.id.user_avatar);
            if(!TextUtils.isEmpty(commentsBean.user.avatar)){
                Glide.with(avatar.getContext()).load(commentsBean.user.avatar).into(avatar);
            }else{
                avatar.setImageResource(R.mipmap.ic_launcher);
            }

            holder.getView(R.id.user_name, TextView.class).setText(commentsBean.user.getDiaplayName());
            holder.getView(R.id.commentTv, TextView.class).setText(commentsBean.content);
            holder.getView(R.id.data, TextView.class).setText(commentsBean.updated_at);
            holder.getView(R.id.likeTv, TextView.class).setText(String.valueOf(commentsBean.like_times));

            if(hasItemListener()){
                holder.getView(R.id.likeTv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeTv = (TextView) v;
                        adapterItemListener.onItemClickListener(commentsBean, holder.getAdapterPosition(), R.id.likeTv);
                    }
                });
            }
        }catch (Exception e){
            XLog.e("初始化评论出错", e);
        }
    }

    public void setLikeText(String text){
        likeTv.setText(text);
    }
}
