package com.lan.campsiteproject.controller.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.lan.campsiteproject.App;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.controller.campsite.ListCampsiteActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore db;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = App.db;
        if (db == null) {
            Log.e(TAG, "Firestore not initialized");
            finish();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            startActivity(new Intent(LoginActivity.this, ListCampsiteActivity.class));
                            finish();
                        } else {
                            user.delete();
                        }
                    });
            return;
        }

        progressBar = findViewById(R.id.progress_bar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        LinearLayout googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        EditText emailField = findViewById(R.id.email);
        EditText passwordField = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }
            loginUser(email, password);
        });
    }

    private void loginUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserInFirestore(user);
                        } else {
                            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                        Toast.makeText(this, "Đăng nhập thất bại: " + error, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Login failed: " + error);
                    }
                });
    }

    private void checkUserInFirestore(FirebaseUser user) {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    progressBar.setVisibility(View.GONE);
                    if (document.exists()) {
                        startActivity(new Intent(LoginActivity.this, ListCampsiteActivity.class));
                        finish();
                    } else {
                        // Firestore user not found, redirect to register
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        intent.putExtra("firebase_uid", user.getUid());
                        intent.putExtra("gmail", user.getEmail());
                        intent.putExtra("first_name", user.getDisplayName() != null ? user.getDisplayName().split(" ")[0] : "");
                        intent.putExtra("last_name", user.getDisplayName() != null && user.getDisplayName().split(" ").length > 1 ? user.getDisplayName().split(" ")[1] : "");
                        intent.putExtra("phone_number", user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Cannot connect to server: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void signInWithGoogle() {
        progressBar.setVisibility(View.VISIBLE);
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            progressBar.setVisibility(View.GONE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Đăng nhập Google thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Google Sign-In error: " + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserInFirestore(user);
                        } else {
                            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                        Toast.makeText(this, "Xác thực Firebase thất bại: " + error, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Firebase Auth failed: " + error);
                    }
                });
    }
}