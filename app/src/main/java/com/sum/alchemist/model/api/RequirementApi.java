package com.sum.alchemist.model.api;

import com.google.gson.JsonObject;
import com.sum.alchemist.model.entity.Requirement;

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

public interface RequirementApi {

    @GET("requirement/list/{offset}/{limit}")
    Observable<List<Requirement>> getRequirementList(
            @Header("Authorization") String token,
            @Path("offset") int offset,
            @Path("limit") int limit
    );

    @GET("requirement/content/{id}")
    Observable<Requirement> getRequirement(
            @Header("Authorization") String token,
            @Path("id") int id
    );


    @GET("requirement/contact/{id}")
    Observable<JsonObject> getRequirementContact(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    @FormUrlEncoded
    @POST("requirement/like")
    Observable<JsonObject> putRequirementLike(
            @Header("Authorization") String token,
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("requirement/collection")
    Observable<JsonObject> addRequirementCollection(
            @Header("Authorization") String token,
            @Field("id") int id
    );


    @FormUrlEncoded
    @POST("requirement")
    Observable<JsonObject> putRequirement(
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
