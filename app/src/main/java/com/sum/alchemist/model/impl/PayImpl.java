package com.sum.alchemist.model.impl;

import com.sum.alchemist.model.api.RetrofitHelper;
import com.sum.alchemist.model.entity.GoldPackage;
import com.sum.alchemist.model.entity.Order;
import com.sum.xlog.core.XLog;

import java.util.List;

import rx.Observable;

import static com.sum.alchemist.Config.HttpConfig.getToken;

/**
 * Created by Administrator on 2016/11/24.
 */

public class PayImpl {

    private static final String TAG = "PayImpl";
    public static PayImpl mInstance;
    public static PayImpl getInstance(){
        if(mInstance == null){
            synchronized(PayImpl.class){
                if(mInstance == null)
                    mInstance = new PayImpl();
            }
        }
        return mInstance;
    }
    private PayImpl(){}

    /**
     * 获取金币包
     */
    public Observable<List<GoldPackage>> getPayProduct() {
        XLog.d(TAG, "=== 获取金币包 ===");
        return RetrofitHelper.getInstance().getPayApiService().getPayProduct();
    }

    /**
     * 创建一个购买订单
     */
    public Observable<Order> createOrder(String type, String id) {
        XLog.d(TAG, "=== 创建一个购买订单 ===");
        return RetrofitHelper.getInstance().getPayApiService().createOrder(getToken(), type, id);
    }

    /**
     * 查询用户订单列表
     */
    public Observable<List<Order>> getOrderList(String offset, String limit) {
        XLog.d(TAG, "=== 查询用户订单列表 ===");
        return RetrofitHelper.getInstance().getPayApiService().getOrderList(getToken(), offset, limit);
    }

    /**
     * 查询订单列表详情
     */
    public Observable<Order> getOrder(String id) {
        XLog.d(TAG, "=== 查询订单列表详情 ===");
        return RetrofitHelper.getInstance().getPayApiService().getOrder(getToken(), id);
    }
}
