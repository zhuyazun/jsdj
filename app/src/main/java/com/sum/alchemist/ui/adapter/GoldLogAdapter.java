package com.sum.alchemist.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sum.alchemist.R;
import com.sum.alchemist.model.entity.GoldLog;
import com.sum.xlog.core.XLog;

/**
 * Created by Qiu on 2016/11/3.
 */

public class GoldLogAdapter extends BaseRecyclerAdapter<GoldLog> {
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleViewHolder(View.inflate(parent.getContext(), R.layout.item_gold_log, null));
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        GoldLog goldLog = getItem(position);
        try {

            ((TextView) holder.getView(R.id.date)).setText(goldLog.updated_at.substring(0, 10));
            ((TextView) holder.getView(R.id.time)).setText(goldLog.updated_at.substring(11, 19));
            ((TextView) holder.getView(R.id.detail)).setText(goldLog.mark);
            if(goldLog.num < 0){
                ((TextView) holder.getView(R.id.gold_num)).setText(String.valueOf(goldLog.num));
            }else{
                ((TextView) holder.getView(R.id.gold_num)).setText(String.format("+ %s", goldLog.num));
            }


        } catch (Exception e) {
            XLog.e("=== 加载金币信息出错===", e);
        }
    }
}
