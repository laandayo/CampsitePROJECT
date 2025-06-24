package com.lan.campsiteproject.controller.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lan.campsiteproject.App;
import com.lan.campsiteproject.MainActivity;
import com.lan.campsiteproject.R;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private static final String BASE_URL = "http://10.0.2.2:3000"; // Emulator
    private FirebaseFirestore db;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = App.db;
        queue = Volley.newRequestQueue(this);

        EditText firstNameField = findViewById(R.id.first_name);
        EditText lastNameField = findViewById(R.id.last_name);
        EditText phoneNumberField = findViewById(R.id.phone_number);
        Button registerButton = findViewById(R.id.registerButton);
        EditText passwordField = findViewById(R.id.password);
        EditText passwordAgainField = findViewById(R.id.password_again);


        Intent intent = getIntent();
        String firebaseUid = intent.getStringExtra("firebase_uid");
        String email = intent.getStringExtra("gmail");
        String firstName = intent.getStringExtra("first_name");
        String lastName = intent.getStringExtra("last_name");
        String phoneNumber = intent.getStringExtra("phone_number");

        EditText emailField = findViewById(R.id.email);
        emailField.setText(email);

        firstNameField.setText(firstName);
        lastNameField.setText(lastName);
        phoneNumberField.setText(phoneNumber);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        registerButton.setOnClickListener(v -> {
            String password = passwordField.getText().toString().trim();
            String passwordAgain = passwordAgainField.getText().toString().trim();
            String phone = phoneNumberField.getText().toString().trim();
            if (phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty() || passwordAgain.isEmpty()) {
                Toast.makeText(this, "Please enter password and confirm it", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(passwordAgain)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }



            // Firestore data
            Map<String, Object> userData = new HashMap<>();
            userData.put("firebaseUid", firebaseUid);
            userData.put("email", email);
            userData.put("firstName", firstNameField.getText().toString().trim());
            userData.put("lastName", lastNameField.getText().toString().trim());
            userData.put("phoneNumber", phone);
            userData.put("isAdmin", false);
            userData.put("isOwner", false);
            // Add this line after creating userData map
            userData.put("profileImageUrl", "https://firebasestorage.googleapis.com/v0/b/your-app.appspot.com/o/default_profile.png?alt=media");

            // SQL Server data
            JSONObject sqlData = new JSONObject();
            try {
                sqlData.put("firebase_uid", firebaseUid);
                sqlData.put("Gmail", email);
                sqlData.put("first_name", firstNameField.getText().toString().trim());
                sqlData.put("last_name", lastNameField.getText().toString().trim());
                sqlData.put("phone_number", phone);
                sqlData.put("isAdmin", false);
                sqlData.put("isOwner", false);
                sqlData.put("passwordHash", password);
                sqlData.put("deactivated", false);
            } catch (JSONException e) {
                Log.e(TAG, "JSON error: " + e.getMessage());
                Toast.makeText(this, "Registration failed: Invalid data", Toast.LENGTH_LONG).show();
                return;
            }

            if (firebaseUid != null) {
                // User already has a Firebase Auth account (from login/Google)
                userData.put("firebaseUid", firebaseUid);
                try { sqlData.put("firebase_uid", firebaseUid); } catch (JSONException e) { /* handle */ }

                db.collection("users").document(firebaseUid)
                        .set(userData)
                        .addOnSuccessListener(aVoid -> {
                            // Link email/password to Google account
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null && email != null && !password.isEmpty()) {
                                AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                                currentUser.linkWithCredential(credential)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                // Now user can log in with email/password too
                                                // Proceed to save to SQL Server
                                                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL + "/accounts", sqlData,
                                                        response -> {
                                                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(RegisterActivity.this, ListCampsiteActivity.class));
                                                            finish();
                                                        },
                                                        error -> {
                                                            String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown error";
                                                            Toast.makeText(this, "SQL Server error: " + errorMsg, Toast.LENGTH_LONG).show();
                                                            Log.e(TAG, "SQL Server sync error: " + errorMsg);
                                                        });
                                                queue.add(request);
                                            } else {
                                                // Handle link error (e.g., email already linked)
                                                Exception e = task.getException();
                                                Toast.makeText(this, "Link failed: " + (e != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                                                Log.e("RegisterActivity", "linkWithCredential failed", e);
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Firestore registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Firestore registration failed: " + e.getMessage());
                        });
            } else {
                // New registration: create Firebase Auth account
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String newUid = task.getResult().getUser().getUid();
                                userData.put("firebaseUid", newUid);
                                try { sqlData.put("firebase_uid", newUid); } catch (JSONException e) { /* handle */ }

                                db.collection("users").document(newUid)
                                        .set(userData)
                                        .addOnSuccessListener(aVoid -> {
                                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL + "/accounts", sqlData,
                                                    response -> {
                                                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(RegisterActivity.this, ListCampsiteActivity.class));
                                                        finish();
                                                    },
                                                    error -> {
                                                        String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown error";
                                                        Toast.makeText(this, "SQL Server error: " + errorMsg, Toast.LENGTH_LONG).show();
                                                        Log.e(TAG, "SQL Server sync error: " + errorMsg);
                                                    });
                                            queue.add(request);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Firestore registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            Log.e(TAG, "Firestore registration failed: " + e.getMessage());
                                        });
                            } else {
                                Toast.makeText(this, "Firebase Auth failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        Button backToLoginButton = findViewById(R.id.backToLoginButton);
        backToLoginButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}