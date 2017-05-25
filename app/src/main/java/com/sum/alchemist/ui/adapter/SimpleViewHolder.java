package com.sum.alchemist.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by Qiu on 2016/11/7.
 */

public class SimpleViewHolder extends RecyclerView.ViewHolder {


    private SparseArray<View> mViews;

    public SimpleViewHolder(View itemView) {
        super(itemView);
    }
    @SuppressWarnings("unchecked")
    public <V extends View> V getView(int id){

        if(mViews == null)
            mViews = new SparseArray<>();

        View view = mViews.get(id);
        if(view == null) {
            view = itemView.findViewById(id);
            mViews.put(id, view);
        }

        return (V) view;

    }

    public <V extends View> V getView(int id, Class<V> vClass){
        return (V)getView(id);
    }
}
