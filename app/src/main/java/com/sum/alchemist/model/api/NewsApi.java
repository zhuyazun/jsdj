package com.sum.alchemist.model.api;

import com.google.gson.JsonObject;
import com.sum.alchemist.model.entity.Banner;
import com.sum.alchemist.model.entity.News;
import com.sum.alchemist.model.entity.Patent;
import com.sum.alchemist.model.entity.Version;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Qiu on 2016/10/31.
 */

public interface NewsApi {

    @GET("banner")
    Observable<List<Banner>> getBanner();

    @GET("patent/list/{offset}/{limit}")
    Observable<List<Patent>> getPatentList(
            @Path("offset") int offset,
            @Path("limit") int limit
    );

    @GET("patent/content/{id}")
    Observable<JsonObject> getPatent(
            @Path("id") int id
    );

    @GET("patent/contact/{id}")
    Observable<JsonObject> getPatentContact(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    @GET("article/list/{offset}/{limit}")
    Observable<List<News>> getArticleList(
            @Path("offset") int offset,
            @Path("limit") int limit
    );

    @GET("article/content/{id}")
    Observable<News> getArticle(
            @Path("id") int id
    );

    @FormUrlEncoded
    @POST("article/like")
    Observable<JsonObject> putArticleLike(
            @Header("Authorization") String token,
            @Field("id") int id
    );

    @GET("version/{app_version}")
    Observable<Version> getNewVersion(@Path("app_version") int app_version);

}
