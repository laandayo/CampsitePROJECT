package com.lan.campsiteproject.test;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.lan.campsiteproject.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class test1st extends AppCompatActivity {
    private static final String TAG = "test1st";
    private RequestQueue queue;
    private static final String BASE_URL = "http://10.0.2.2:3000"; // Emulator

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1st);

        queue = Volley.newRequestQueue(this);

        // Test database connection
        getAccounts();
    }

    // New method: Fetch all accounts
    private void getAccounts() {
        String url = BASE_URL + "/accounts";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    StringBuilder result = new StringBuilder("Accounts:\n");
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject account = response.getJSONObject(i);
                            result.append("ID: ").append(account.getInt("Account_id"))
                                    .append(", Name: ").append(account.getString("first_name"))
                                    .append(" ").append(account.getString("last_name"))
                                    .append(", Email: ").append(account.getString("Gmail"))
                                    .append(", Admin: ").append(account.getBoolean("isAdmin"))
                                    .append("\n");
                        }
                        Log.d(TAG, result.toString());
                        Toast.makeText(test1st.this, result.toString(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        Toast.makeText(test1st.this, "Error parsing accounts", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching accounts: " + error.getMessage());
                    Toast.makeText(test1st.this, "Failed to fetch accounts: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }
}