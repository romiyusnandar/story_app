package com.koaladev.storryapp.data.retrofit

import com.koaladev.storryapp.data.response.LoginResponse
import com.koaladev.storryapp.data.response.SignupResponse
import com.koaladev.storryapp.data.response.StoryResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServices {
    @FormUrlEncoded
    @POST("register")
    suspend fun signup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SignupResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token: String,
        @Query("location") location: String
    ): Call<StoryResponse>
}