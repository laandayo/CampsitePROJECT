package com.lan.campsiteproject.api;

import com.lan.campsiteproject.dbcontext.CampsiteApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    private static Retrofit retrofit = null;

    private static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)  // Giả sử bạn đã khai báo biến này đúng ở ApiConstants
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static final CampsiteApi campsiteApi = getClient().create(CampsiteApi.class);
    public static final GearApi gearApi = getClient().create(GearApi.class);
}
