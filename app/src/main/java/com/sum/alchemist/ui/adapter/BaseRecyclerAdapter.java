package com.sum.alchemist.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Qiu on 2016/6/15.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<SimpleViewHolder>{

    private List<T> datas;

    private View haederView;

    public static int HEADER_TYPE = 1001;

    public AdapterItemListener adapterItemListener;

    public List<T> getDatas(){
        return datas;
    }

    public void setDatas(List<T> datas){
        this.datas = datas;
    }

    public void addDatas(List<T> datas){
        if(this.datas == null) {
            setDatas(datas);
        }else {
            this.datas.addAll(datas);
        }

    }

    public void setAdapterItemListener(AdapterItemListener adapterItemListener) {
        this.adapterItemListener = adapterItemListener;
    }

    public boolean hasItemListener(){
        return adapterItemListener != null;
    }

    @Override
    public int getItemCount() {
        int headerCount = haederView != null ? 1:0;
        return headerCount + (datas != null?datas.size():0);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == HEADER_TYPE && haederView != null)
            return new SimpleViewHolder(haederView);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if(haederView != null && position == 0)
            return HEADER_TYPE;
        return super.getItemViewType(position);
    }

    public T getItem(int position){
        int headerCount = haederView != null ? 1:0;
        position = position - headerCount;
        return position < getItemCount() && position >= 0?datas.get(position):null;
    }

    public void setHaederView(View haederView) {
        this.haederView = haederView;
    }
}
