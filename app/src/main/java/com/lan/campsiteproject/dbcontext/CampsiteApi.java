package com.lan.campsiteproject.dbcontext;

import com.lan.campsiteproject.model.Campsite;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CampsiteApi {
    @GET("campsites")
    Call<List<Campsite>> getAllCampsites();
}
