package com.bionic.kvt.serviceapp.api;


import com.bionic.kvt.serviceapp.models.Order;
import com.bionic.kvt.serviceapp.models.OrderBrief;
import com.bionic.kvt.serviceapp.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderServiceApi {

    // Request list of all Users currently available in system
    // URL format /users
    // Expecting JSON
    @POST("users")
    Call<List<User>> getAllUsers();
//    @GET("users")
//    Call<List<User>> getAllUsers();


    // Request list of all Orders currently available for {user} in brief format
    // URL format /orders/brief?user={email}
    // Expecting JSON
//    @GET("orders/brief")
//    Call<List<OrderBrief>> getOrdersBrief(@Query("user") String email);
    @FormUrlEncoded
    @POST("orders/brief")
    Call<List<OrderBrief>> getOrdersBrief(@Field("user") String email);

    // Request one order with {number} for {user}
    // URL format /orders/{number}?user={email}
    // Expecting JSON
    @FormUrlEncoded
    @POST("orders/{number}")
    Call<Order> getOrder(@Path("number") long number, @Field("user") String email);

//    @GET("orders/{number}")
//    Call<Order> getOrder(@Path("number") long number, @Query("user") String email);

}
