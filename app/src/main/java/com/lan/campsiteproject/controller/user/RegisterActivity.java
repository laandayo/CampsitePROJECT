package com.lan.campsiteproject.controller.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lan.campsiteproject.App;
import com.lan.campsiteproject.R;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.lan.campsiteproject.controller.campsite.ListCampsiteActivity;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = App.db;

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
            userData.put("profileImageUrl", "https://firebasestorage.googleapis.com/v0/b/your-app.appspot.com/o/default_profile.png?alt=media");

            if (firebaseUid != null) {
                // User already has a Firebase Auth account (from login/Google)
                userData.put("firebaseUid", firebaseUid);

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
                                                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RegisterActivity.this, ListCampsiteActivity.class));
                                                finish();
                                            } else {
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

                                db.collection("users").document(newUid)
                                        .set(userData)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterActivity.this, ListCampsiteActivity.class));
                                            finish();
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