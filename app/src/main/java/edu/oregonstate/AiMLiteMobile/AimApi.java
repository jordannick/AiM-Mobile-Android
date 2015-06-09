package edu.oregonstate.AiMLiteMobile;

import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedString;

/**
 * Created by sellersk on 6/9/2015.
 */
public interface AimApi {

    // LOGIN   //"/User/login" + '/' + username;
    @Headers({
            "Content-type: application/x-www-form-urlencoded"
    })
    @FormUrlEncoded
    @POST("/User/login/crosst")//    /User/login/crosst
    void login(@Field("password") String pass, Callback<String> callback);

    @Headers({
            "Content-type: x-www-form-urlencoded"
    })
    @POST("/1111fki1")
    void testPost(@Body TypedString str, Callback<String> callback);

}
