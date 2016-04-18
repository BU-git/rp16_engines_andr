package com.bionic.kvt.serviceapp.api;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServiceConnection {
    // Request list of all Users currently available in system
    // URL format /users
    // Expecting JSON
    @GET("users")
    Call<List<User>> getAllUsers();

    // Request list of all Orders currently available for {userId} in brief format
    // URL format /orders/brief/{userId}
    // Expecting JSON
    @GET("orders/brief/{userId}")
    Call<List<OrderBrief>> getOrdersBrief(@Path("userId") String userId);

    // Request one order with {number} for {userId}
    // URL format /orders/{number}/{userId}
    // Expecting JSON
    @GET("orders/{number}/{userId}")
    Call<Order> getOrder(@Path("number") long number, @Path("userId") String userId);
}