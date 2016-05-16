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
    @POST("templates/get/{customTemplateID}")
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
    // Fields in MultipartBody:
    // {type} - <String> type of file [DEFAULT_PDF_REPORT, CUSTOM_PDF_REPORT, LMRA_PHOTO, XML_ZIP]
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