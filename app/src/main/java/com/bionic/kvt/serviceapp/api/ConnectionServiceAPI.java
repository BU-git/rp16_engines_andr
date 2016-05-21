package com.bionic.kvt.serviceapp.api;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ConnectionServiceAPI {
    // Request User data
    // Expecting JSON
    @FormUrlEncoded
    @POST("user")
    Call<User> getUser(@Field("email") String email);

    // Request list of all Orders currently available for {email} in brief format
    // Expecting JSON
    @FormUrlEncoded
    @POST("orders/brief")
    Call<List<OrderBrief>> getOrdersBrief(@Field("email") String email);

    // Request one order with {number} for {email}
    // Expecting JSON
    @FormUrlEncoded
    @POST("orders/get")
    Call<Order> getOrder(@Field("number") long number,
                         @Field("email") String email);

    // Request one custom template with {customTemplateID}
    // Expecting JSON
    @FormUrlEncoded
    @POST("templates/get")
    Call<CustomTemplate> getTemplate(@Field("customTemplateID") long customTemplateID);


    // Send order update information for order {number}, user {email}
    // Expecting standard HTTP response [200, 400, 500]
    @FormUrlEncoded
    @POST("orders/update")
    Call<ResponseBody> updateOrder(@Field("number") long number,
                                   @Field("email") String email,
                                   @Field("lastAndroidChangeDate") long lastAndroidChangeDate,
                                   @Field("orderStatus") int orderStatus);


    // Upload file for order {number}
    // Fields in MultipartBody:
    // {type} - <String> type of file [DEFAULT_PDF_REPORT, LMRA_PHOTO, XML_ZIP_REPORT]
    // {checksum} - <String> md5 sum for file
    // {file} - <String> file name and <image/png, application/pdf, application/octet-stream> file content
    @POST("upload/{number}")
    Call<ResponseBody> uploadFile(@Path("number") long number,
                                  @Body MultipartBody requestBody);


    // Request user password reset
    @FormUrlEncoded
    @POST("user/reset")
    Call<ResponseBody> passwordReset(@Field("email") String email,
                                     @Field("key") String key);

}