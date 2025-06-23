package com.lan.campsiteproject.api;

import com.lan.campsiteproject.model.Gear;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface GearApi {

    @GET("gears")
        // Hoặc endpoint phù hợp với API của bạn
    Call<List<Gear>> getAllGear();
}
