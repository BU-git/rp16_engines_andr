package com.bionic.kvt.serviceapp.api;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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
    // URL format /orders/get/{number}/{email}
    // Expecting JSON
    @POST("orders/get/{number}/{email}")
    Call<Order> getOrder(@Path("number") long number,
                         @Path("email") String email);

    // Request one custom template with {customTemplateID}
    // URL format /template/{customTemplateID}
    // Expecting JSON
    @POST("template/{customTemplateID}")
    Call<CustomTemplate> getTemplate(@Path("customTemplateID") long customTemplateID);


    // Send order update information for order {number}, user {email}
    // URL format /orders/update/{number}/{email}
    // Expecting standard HTTP response [200, 400, 500]
    @FormUrlEncoded
    @POST("orders/update/{number}/{email}")
    Call<ResponseBody> updateOrder(@Path("number") long number,
                                   @Path("email") String email,
                                   @Field("lastAndroidChangeDate") long lastAndroidChangeDate,
                                   @Field("orderStatus") int orderStatus);


    // Upload file for order {number}
    // {checksum} - md5 sum for file
    // MultipartBody.Part consist of file Name and file Data
    @Multipart
    @POST("upload/{number}")
    Call<ResponseBody> uploadFile(@Path("number") long number,
                                  @Part("fileData") MultipartBody requestBody);


}