package com.wealthwise.app.api;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ========== 认证接口 ==========
    @POST("auth/register")
    Call<Result<Void>> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<Result<TokenResponse>> login(@Body LoginRequest request);

    // ========== 账单接口 ==========
    @GET("bill/list")
    Call<Result<BillPage>> getBillList(
            @Query("page") int page,
            @Query("size") int size,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @POST("bill/add")
    Call<Result<Void>> addBill(@Body AddBillRequest request);

    @DELETE("bill/delete/{id}")
    Call<Result<Void>> deleteBill(@Path("id") Long id);

    @retrofit2.http.PUT("bill/update")
    Call<Result<Void>> updateBill(@Body UpdateBillRequest request);

    // ========== 分类接口 ==========
    @GET("category/list")
    Call<Result<List<Category>>> getCategoryList(@Query("type") Integer type);

    @GET("category/list")
    Call<Result<List<Category>>> getCategoryListWithDirection(
            @Query("type") Integer type,
            @Query("direction") Integer direction);

    @POST("category/add")
    Call<Result<Void>> addCategory(@Body AddCategoryRequest request);

    @DELETE("category/delete/{id}")
    Call<Result<Void>> deleteCategory(@Path("id") Integer id);

    // ========== 统计接口 ==========
    @GET("statistics/overview")
    Call<Result<Map<String, Object>>> getOverview(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("statistics/byCategory")
    Call<Result<List<Map<String, Object>>>> getByCategory(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("statistics/trend")
    Call<Result<List<Map<String, Object>>>> getTrend(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("granularity") String granularity
    );

    @GET("statistics/categoryCompare")
    Call<Result<List<Map<String, Object>>>> getCategoryCompare(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("statistics/byPaymentMethod")
    Call<Result<List<Map<String, Object>>>> getByPaymentMethod(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );
}
