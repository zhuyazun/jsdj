package com.sum.alchemist.model.api;

import com.sum.alchemist.model.entity.GoldPackage;
import com.sum.alchemist.model.entity.Order;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Administrator on 2016/11/24.
 */

public interface PayApi {


    /**
     * 获取金币包列表
     */
    @GET("pay/product")
    Observable<List<GoldPackage>> getPayProduct();

    /**
     * 创建订单
     * @param token 用户token
     * @param type <li>weixin:微信</li><li>zhifubao:支付宝</li>
     * @param id 金币包ID
     */
    @FormUrlEncoded
    @POST("pay/order")
    Observable<Order> createOrder(@Header("Authorization") String token, @Field("type") String type, @Field("id")String id);

    /**
     * 获取订单列表
     */
    @GET("pay/order/list/{offset}/{limit}")
    Observable<List<Order>> getOrderList(@Header("Authorization") String token, @Path("offset")String offset, @Path("limit")String limit);

    /**
     * 获取订单详情
     * @param token 用户token
     * @param id 订单id
     */
    @GET("pay/order/content/{id}")
    Observable<Order> getOrder(@Header("Authorization") String token, @Path("id") String id);
}
