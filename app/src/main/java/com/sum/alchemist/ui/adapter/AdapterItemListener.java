package com.sum.alchemist.ui.adapter;

/**
 * Created by Qiu on 2016/5/8.
 */
public interface AdapterItemListener<T> {
    /**
     * 适配器回调事件
     * @param data 回调数据
     * @param id 回调事件Id
     */
    void onItemClickListener(T data, int position, int id);
}
