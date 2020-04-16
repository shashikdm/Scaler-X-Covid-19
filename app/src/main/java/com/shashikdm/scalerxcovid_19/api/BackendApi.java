package com.shashikdm.scalerxcovid_19.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BackendApi {
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST("auth/login/verify")
    Call<LoginVerifyResponse> loginVerify(@Body LoginVerifyRequest loginVerifyRequest);

    @PUT("users")
    Call<Object> updateUser(@Header("Authorization") String token, @Body UpdateUserRequest updateUserRequest);

    @POST("posts")
    Call<CreatePostResponse> createPost(@Header("Authorization") String token, @Body CreatePostRequest createPostRequest);

    @GET("posts")
    Call<List<Post>> getPostsNearUser(@Header("Authorization") String token,
                                      @Query("pageNo") Integer pageNo,
                                      @Query("pageSize") Integer pageSize,
                                      @Query("radius") Integer radius);

    @GET("users/me")
    Call<User> getUserDetails(@Header("Authorization") String token);

    @GET("posts/me")
    Call<List<Post>> getMyPosts(@Header("Authorization") String token,
                          @Query("pageNo") Integer pageNo,
                          @Query("pageSize") Integer pageSize);

    @POST("help-offers")
    Call<Void> offerHelp(@Header("Authorization") String token,
                           @Body OfferHelpRequest offerHelpRequest);

    @GET("posts/{postId}/help-offers")
    Call<List<Help>> getHelps(@Header("Authorization") String token, @Path("postId") String postId);

    @POST("auth/logout")
    Call<Object> logOut(@Header("Authorization") String token);
}
