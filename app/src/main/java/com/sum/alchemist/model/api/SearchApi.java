package com.sum.alchemist.model.api;

import com.sum.alchemist.model.entity.Provision;
import com.sum.alchemist.model.entity.Requirement;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Qiu on 2016/11/20.
 */

public interface SearchApi {

    @FormUrlEncoded
    @POST("provision/requirement/search")
    Observable<List<Provision>> getProvisionList(
            @Field("type") String type,
            @Field("search_type") String search_type,
            @Field("created_at") String created_at,
            @Field("location") String location,
            @Field("company_extent") String company_extent,
            @Field("price_range") String price_range,
            @Field("company_property") String company_property,
            @Field("str") String key,
            @Field("offset") int offset,
            @Field("limit") int limit
    );

    @FormUrlEncoded
    @POST("provision/requirement/search")
    Observable<List<Requirement>> getRequirementList(
            @Field("type") String type,
            @Field("search_type") String search_type,
            @Field("created_at") String created_at,
            @Field("location") String location,
            @Field("company_extent") String company_extent,
            @Field("price_range") String price_range,
            @Field("company_property") String company_property,
            @Field("str") String key,
            @Field("offset") int offset,
            @Field("limit") int limit
    );

}
