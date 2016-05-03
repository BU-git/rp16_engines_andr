package com.bionic.kvt.serviceapp.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ConnectionServiceAPI {
    // Request User data
    // URL format /user/{email}
    // Expecting JSON
    @POST("user/{email}")
    Call<User> getUser(@Path("email") String email);

    // Request list of all Orders currently available for {email} in brief format
    // URL format /orders/brief/{email}
    // Expecting JSON
    @POST("orders/brief/{email}")
    Call<List<OrderBrief>> getOrdersBrief(@Path("email") String email);

    // Request one order with {number} for {email}
    // URL format /orders/{number}/{email}
    // Expecting JSON
    @POST("orders/{number}/{email}")
    Call<Order> getOrder(@Path("number") long number, @Path("email") String email);

    // Request one custom template with {customTemplateID}
    // URL format /template/{customTemplateID}
    // Expecting JSON
    @POST("template/{customTemplateID}")
    Call<CustomTemplate> getTemplate(@Path("customTemplateID") long customTemplateID);
}
