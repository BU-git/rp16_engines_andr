package com.bionic.kvt.serviceapp.api;


import com.bionic.kvt.serviceapp.models.Order;
import com.bionic.kvt.serviceapp.models.OrderBrief;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OrderServiceApi {

    //Request list of all Orders currently available for {user} in brief format
    @GET("/api/{user}/order/brief_list")
    Call<List<OrderBrief>> getOrdersByUser(@Path("user") String user);


    //Request one order with {number} for {user}
    @GET("/api/{user}/order/{number}")
    Call<Order> getOrdersByUser(@Path("user") String user, @Path("number") int number);


}
