package com.example.recipebook;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiRequests {
    @Multipart
    @POST("storage/v1/object/images/{path}")
    Call<ResponseBody> uploadFile(
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Path("path") String path,
            @Part MultipartBody.Part file
    );
}