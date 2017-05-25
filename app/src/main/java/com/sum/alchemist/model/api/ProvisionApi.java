package com.sum.alchemist.model.api;

import com.google.gson.JsonObject;
import com.sum.alchemist.model.entity.Provision;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Qiu on 2016/10/31.
 */

public interface ProvisionApi {


    @Multipart
    @POST("file")
    Observable<JsonObject> putFile(
            @Header("Authorization") String token,
            @Part MultipartBody.Part file
    );

    @GET("provision/list/{offset}/{limit}")
    Observable<List<Provision>> getProvisionList(
            @Header("Authorization") String token,
            @Path("offset") int offset,
            @Path("limit") int limit
    );

    @GET("provision/content/{id}")
    Observable<Provision> getProvision(
            @Header("Authorization") String token,
            @Path("id") int id
    );


    @GET("provision/contact/{id}")
    Observable<JsonObject> getProvisionContact(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    @FormUrlEncoded
    @POST("provision/like")
    Observable<JsonObject> putProvisionLike(
            @Header("Authorization") String token,
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("provision/collection")
    Observable<JsonObject> addProvisionCollection(
            @Header("Authorization") String token,
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("provision")
    Observable<JsonObject> putProvision(
            @Header("Authorization") String token,
            @Field("type") String type,
            @Field("location") String location,
            @Field("price_range") String price_range,
            @Field("company_property") String company_property,
            @Field("company_extent") String company_extent,
            @Field("contact") String contact,
            @Field("title") String title,
            @Field("price") String price,
            @Field("content") String content
    );
}
