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
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONObject;
import com.lan.campsiteproject.MainActivity;
import com.lan.campsiteproject.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import android.widget.LinearLayout;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String BASE_URL = "http://10.0.2.2:3000"; // Emulator
    private FirebaseAuth mAuth;
    private RequestQueue queue;

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        LinearLayout googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        mAuth = FirebaseAuth.getInstance();
        queue = Volley.newRequestQueue(this);

        EditText emailField = findViewById(R.id.email);
        EditText passwordField = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            loginUser(email, password);
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserInBackend(user);
                        }
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkUserInBackend(FirebaseUser user) {
        String url = BASE_URL + "/accounts?firebase_uid=" + user.getUid();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // If user exists in backend, go to MainActivity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                },
                error -> {
                    // If not found (404), go to RegisterActivity for extra info
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    intent.putExtra("gmail", user.getEmail());
                    startActivity(intent);
                    finish();
                });
        queue.add(request);
    }

    private void syncUserWithSqlServer(FirebaseUser user) {
        String url = BASE_URL + "/accounts";
        JSONObject json = new JSONObject();
        try {
            json.put("firebase_uid", user.getUid());
            json.put("Gmail", user.getEmail());
            json.put("first_name", user.getDisplayName() != null ? user.getDisplayName() : "");
            json.put("last_name", "");
            json.put("phone_number", user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
            json.put("isAdmin", false);
            json.put("isOwner", false);
            json.put("passwordHash", ""); // Not used with Firebase
            json.put("deactivated", false);
        } catch (Exception e) {
            Log.e(TAG, "JSON error: " + e.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
                response -> Log.d(TAG, "User synced with SQL Server: " + response.toString()),
                error -> {
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                    if (error.networkResponse != null) {
                        errorMessage += " (HTTP " + error.networkResponse.statusCode + ")";
                        if (error.networkResponse.data != null) {
                            errorMessage += " - " + new String(error.networkResponse.data);
                        }
                    }
                    Log.e(TAG, "Sync error: " + errorMessage);
                    Toast.makeText(LoginActivity.this, "Failed to sync user: " + errorMessage, Toast.LENGTH_LONG).show();
                });
        queue.add(request);
    }

    private void signInWithGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    // Pass idToken and email to RegisterActivity, do NOT call firebaseAuthWithGoogle here
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    intent.putExtra("gmail", account.getEmail());
                    intent.putExtra("idToken", account.getIdToken());
                    startActivity(intent);
                    finish();
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserInBackend(user);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Firebase Auth failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                    }
                });
    }
}