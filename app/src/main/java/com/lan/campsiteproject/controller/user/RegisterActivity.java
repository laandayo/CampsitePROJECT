package com.lan.campsiteproject.controller.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.lan.campsiteproject.MainActivity;
import com.lan.campsiteproject.R;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        String idToken = getIntent().getStringExtra("idToken");
        String gmail = getIntent().getStringExtra("gmail");

        EditText gmailField = findViewById(R.id.gmailField);
        EditText firstNameField = findViewById(R.id.firstNameField);
        EditText lastNameField = findViewById(R.id.lastNameField);
        EditText phoneField = findViewById(R.id.phoneField);
        EditText passwordField = findViewById(R.id.passwordField);
        EditText passwordAgainField = findViewById(R.id.passwordAgainField);
        Button completeRegisterButton = findViewById(R.id.completeRegisterButton);

        if (gmail != null) {
            gmailField.setText(gmail);
            gmailField.setEnabled(false);
        } else {
            gmailField.setEnabled(true);
            gmailField.setText("");
        }

        completeRegisterButton.setOnClickListener(v -> {
            String firstName = firstNameField.getText().toString().trim();
            String lastName = lastNameField.getText().toString().trim();
            String phone = phoneField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String passwordAgain = passwordAgainField.getText().toString().trim();

            if (password.isEmpty() || passwordAgain.isEmpty()) {
                Toast.makeText(this, "Please enter password in both fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(passwordAgain)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (idToken != null) {
                // Google registration: sign in to Firebase with Google
                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    saveUserToBackend(user, gmailField.getText().toString().trim(), firstName, lastName, phone, password);
                                }
                            } else {
                                Toast.makeText(this, "Google sign-in failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                // Email/password registration: create Firebase user
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(gmailField.getText().toString().trim(), password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    saveUserToBackend(user, gmailField.getText().toString().trim(), firstName, lastName, phone, password);
                                }
                            } else {
                                Toast.makeText(this, "Registration failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private void saveUserToBackend(FirebaseUser user, String gmail, String firstName, String lastName, String phone, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("firebase_uid", user.getUid());
            json.put("Gmail", gmail);
            json.put("first_name", firstName);
            json.put("last_name", lastName);
            json.put("phone_number", phone);
            json.put("isAdmin", false);
            json.put("isOwner", false);
            json.put("passwordHash", password);
            json.put("deactivated", false);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:3000/accounts";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
                response -> {
                    Toast.makeText(this, "Registration complete!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                },
                error -> {
                    String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown error";
                    Toast.makeText(this, "Registration failed: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(request);
    }
}