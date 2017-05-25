package com.sum.alchemist.model.api;

import com.google.gson.JsonObject;
import com.sum.alchemist.model.entity.GoldLog;
import com.sum.alchemist.model.entity.Message;
import com.sum.alchemist.model.entity.User;
import com.sum.alchemist.model.entity.WebContent;

import java.util.List;

import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Qiu on 2016/10/25.
 */

public interface UserApi {

    @FormUrlEncoded
    @POST("oauth/access_token")
    Observable<JsonObject> accessToken(
            @Field("grant_type") String grant_type,
            @Field("client_secret") String client_secret,
            @Field("username") String username,
            @Field("password") String password,
            @Field("client_id") String client_id
    );
    @FormUrlEncoded
    @POST("oauth/access_token")
    Observable<JsonObject> refreshToken(
            @Field("grant_type") String grant_type,
            @Field("client_secret") String client_secret,
            @Field("refresh_token") String refresh_token,
            @Field("client_id") String client_id
    );

    @FormUrlEncoded
    @POST("user")
    Observable<JsonObject> reg(
            @Field("username") String username,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("sign") String sign,
            @Field("timestamp") long timestamp,
            @Field("password") String password
    );

    @GET("user")
    Observable<User> getUserInfo(@Header("Authorization") String token);


    @FormUrlEncoded
    @PUT("user/password")
    Observable<JsonObject> changePassword(
            @Header("Authorization") String token,
            @Field("username") String username,
            @Field("password") String password,
            @Field("sign") String sign,
            @Field("timestamp") long timestamp,
            @Field("old") String old
    );

    @FormUrlEncoded
    @PUT("user/info")
    Observable<JsonObject> putUserInfo(
            @Header("Authorization") String token,
            @Field("nickname") String nickname,
            @Field("avatar") String avatar,
            @Field("full_name") String full_name,
            @Field("gender") String gender,
            @Field("age") String age,
            @Field("card_number") String card_number,
            @Field("contact") String contact,
            @Field("qq") String qq,
            @Field("wechat") String wechat,
            @Field("company") String company,
            @Field("job_duties") String job_duties,
            @Field("job_title") String job_title,
            @Field("graduated_school") String graduated_school,
            @Field("education") String education,
            @Field("profession") String profession,
            @Field("research_areas") String research_areas,
            @Field("paper_works") String paper_works,
            @Field("patented_soft") String patented_soft,
            @Field("completed_technology_projects") String completed_technology_projects,
            @Field("doing_technology_projects") String doing_technology_projects
    );

    @POST("user/sign")
    Observable<JsonObject> sign(
            @Header("Authorization") String token
    );

    @GET("user/gold/logs/{offset}/{limit}")
    Observable<List<GoldLog>> getGold(
            @Header("Authorization") String token,
            @Path("offset") int offset,
            @Path("limit") int limit
    );

    @GET("user/msg/logs/{status}/{offset}/{limit}")
    Observable<List<Message>> getMessageLogs(
            @Header("Authorization") String token,
            @Path("status") String status,
            @Path("offset") int offset,
            @Path("limit") int limit
    );
    @FormUrlEncoded
    @PUT("user/msg/status")
    Observable<JsonObject> putMessageLogsStatus(
            @Header("Authorization") String token,
            @Field("id") String id
    );

    @DELETE("user/session")
    Observable<JsonObject> logout(
            @Header("Authorization") String token
    );

    @GET("user/provision/requirement/like/{offset}/{limit}")
    Observable<JsonObject> getUserLike(@Header("Authorization") String token,
                                       @Path("offset") int offset,
                                       @Path("limit") int limit);

    @GET("user/provision/requirement/publish/{offset}/{limit}")
    Observable<JsonObject> getUserSendList(@Header("Authorization") String token,
                                           @Path("offset") int offset,
                                           @Path("limit") int limit);

    @GET("other/yhxy")
    Observable<WebContent> getUserUseTk();

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "user/provision/requirement", hasBody = true)
    Observable<JsonObject> deleteMission(@Header("Authorization") String token,
                                         @Field("type") String type,
                                         @Field("id") int id);


}
