package com.dicoding.picodiploma.loginwithanimation.data.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

    interface ApiService {
        // Register a user with name, email, and password
        @FormUrlEncoded
        @POST("register")
        fun register(
            @Field("name") name: String,
            @Field("email") email: String,
            @Field("password") password: String
        ): Call<FileUploadResponse>

        @FormUrlEncoded
        @POST("login")
        fun login(
            @Field("email") email: String,
            @Field("password") password: String
        ): Call<LoginResponse>
    }
