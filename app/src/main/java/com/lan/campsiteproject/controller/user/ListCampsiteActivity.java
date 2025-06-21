package com.lan.campsiteproject.controller.user;


import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lan.campsiteproject.R;
import com.lan.campsiteproject.controller.user.CampsiteAdapter;
import com.lan.campsiteproject.dbcontext.ApiClient;
import com.lan.campsiteproject.dbcontext.CampsiteApi;
import com.lan.campsiteproject.model.Campsite;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListCampsiteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CampsiteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listcampsie);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CampsiteApi api = ApiClient.getClient().create(CampsiteApi.class);
        api.getAllCampsites().enqueue(new Callback<List<Campsite>>() {
            @Override
            public void onResponse(Call<List<Campsite>> call, Response<List<Campsite>> response) {
                if (response.isSuccessful()) {
                    List<Campsite> list = response.body();
                    adapter = new CampsiteAdapter(list);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(ListCampsiteActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Campsite>> call, Throwable t) {
                Toast.makeText(ListCampsiteActivity.this, "Không kết nối được server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
