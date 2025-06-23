package com.lan.campsiteproject.api;


import com.lan.campsiteproject.model.Campsite;
import com.lan.campsiteproject.model.Gear;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("campsites")
    Call<List<Campsite>> getAllCampsites();

    @GET("gear")
    Call<List<Gear>> getAllGear();
}
