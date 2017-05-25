package com.sum.alchemist.ui.adapter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sum.alchemist.R;
import com.sum.alchemist.model.entity.Forum;
import com.sum.alchemist.ui.activity.ImageViewActivity;
import com.sum.alchemist.utils.StringUtil;
import com.sum.xlog.core.XLog;

import java.util.Arrays;

/**
 * Created by Qiu on 2016/11/3.
 */

public class ForumAdapter extends BaseRecyclerAdapter<Forum> {

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == R.layout.item_forum)
            return new SimpleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum, parent, false));
        else
            return new SimpleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum_text, parent, false));
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {
        final Forum forum = getItem(position);
        try {

            holder.getView(R.id.user_name, TextView.class).setText(forum.user.getDiaplayName());
            holder.getView(R.id.date, TextView.class).setText(StringUtil.getDate(forum.updated_at));
            holder.getView(R.id.content, TextView.class).setText(forum.title);
            holder.getView(R.id.desc, TextView.class).setText(forum.desc);
            holder.getView(R.id.commentTv, TextView.class).setText(String.valueOf(forum.comment_num));
            holder.getView(R.id.likeTv, TextView.class).setText(String.valueOf(forum.like_num));
            holder.getView(R.id.showTv, TextView.class).setText(String.valueOf(forum.show_times));
            Glide.with(holder.itemView.getContext()).load(forum.user.avatar).into(holder.getView(R.id.user_avatar, ImageView.class));


            if (!TextUtils.isEmpty(forum.img)) {
                final GridView gridView = holder.getView(R.id.grid_view);
                ListAdapter adapter = gridView.getAdapter();
                if (adapter == null) {
                    adapter = new ImageViewAdapter();
                    ((ImageViewAdapter)adapter).setItemListener(new AdapterItemListener<String>() {
                        @Override
                        public void onItemClickListener(String data, int position, int id) {
                            Intent intent = ImageViewActivity.getIntent(gridView.getContext(), data);
                            if(intent != null){
                                gridView.getContext().startActivity(intent);
                            }
                        }
                    });
                    gridView.setAdapter(adapter);
                }
                ((ImageViewAdapter) adapter).setData(Arrays.asList(forum.img.split("\\|")));
                ((ImageViewAdapter) adapter).notifyDataSetChanged();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasItemListener()) {
                        adapterItemListener.onItemClickListener(forum, holder.getAdapterPosition(), 0);
                    }
                }
            });

        } catch (Exception e) {
            XLog.e("=== 加载专利信息出错===", e);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Forum forum = getItem(position);
        return TextUtils.isEmpty(forum.img)?R.layout.item_forum_text:R.layout.item_forum;
    }
}
