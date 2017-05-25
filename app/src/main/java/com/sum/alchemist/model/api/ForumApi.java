package com.sum.alchemist.model.api;

import com.google.gson.JsonObject;
import com.sum.alchemist.model.db.Topic;
import com.sum.alchemist.model.entity.Forum;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Qiu on 2016/11/6.
 */

public interface ForumApi {


    @GET("forum/thread/list/{type}/{offset}/{limit}")
    Observable<List<Forum>> getForumList(
            @Header("Authorization") String token,
            @Path("type") String type,
            @Path("offset") int offset,
            @Path("limit") int limit
    );


    @GET("forum/thread/content/{id}")
    Observable<Forum> getForum(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    @FormUrlEncoded
    @POST("forum/thread")
    Observable<JsonObject> putForum(
            @Header("Authorization") String token,
            @Field("title") String title,
            @Field("location") String location,
            @Field("content") String content,
            @Field("topic_id") int topic_id,
            @Field("img") String img
    );

    @FormUrlEncoded
    @POST("forum/thread/comment")
    Observable<JsonObject> putForumComment(
            @Header("Content-Type") String content_type,
            @Header("Authorization") String token,
            @Field("tid") int tid,
            @Field("content") String content
    );


    @FormUrlEncoded
    @POST("forum/thread/like")
    Observable<JsonObject> putForumLike(
            @Header("Authorization") String token,
            @Field(value = "tid") int tid
    );

    @FormUrlEncoded
    @POST("forum/thread/search")
    Observable<List<Forum>> search(
            @Field("str") String titleKey,
            @Field("offset") int offset,
            @Field("limit") int limit
    );

    @FormUrlEncoded
    @POST("forum/thread/comment/like")
    Observable<JsonObject> commentLike(
            @Header("Authorization") String token,
            @Field("cid") int tid
    );

    @GET("forum/topic/list/{offset}/{limit}")
    Observable<List<Topic>> getTopic(@Path("offset") int offset, @Path("limit") int limit, @Query("type") String type);

    @GET("forum/topic/thread/list/{topic}/{offset}/{limit}")
    Observable<List<Forum>> getForumByTopic(@Header("Authorization") String token, @Path("topic") String topic, @Path("offset") int offset, @Path("limit") int limit);

}
