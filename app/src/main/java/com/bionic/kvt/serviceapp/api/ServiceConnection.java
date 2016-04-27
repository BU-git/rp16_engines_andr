package com.bionic.kvt.serviceapp.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServiceConnection {
    // Request User data
    // URL format /user/{userId}
    // Expecting JSON
    @POST("user/{userId}")
    Call<User> getUser(@Path("userId") String userId);

    // Request list of all Orders currently available for {userId} in brief format
    // URL format /orders/brief/{userId}
    // Expecting JSON
    @POST("orders/brief/{userId}")
    Call<List<OrderBrief>> getOrdersBrief(@Path("userId") String userId);

    // Request one order with {number} for {userId}
    // URL format /orders/{number}/{userId}
    // Expecting JSON
    @POST("orders/{number}/{userId}")
    Call<Order> getOrder(@Path("number") long number, @Path("userId") String userId);
}
