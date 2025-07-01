package com.lan.campsiteproject.controller.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.lan.campsiteproject.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private EditText firstName, lastName, phoneNumber, email;
    private Button btnUpdate, btnChangePassword;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView avatarImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        phoneNumber = findViewById(R.id.phoneNumber);
        email = findViewById(R.id.email);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Email is always read-only
        email.setText(user.getEmail());
        email.setEnabled(false);
        email.setFocusable(false);

        loadUserProfile();

        btnUpdate.setOnClickListener(v -> updateProfile());
        btnChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, "Change password feature not implemented", Toast.LENGTH_SHORT).show();
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Avatar"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                avatarImageView.setImageBitmap(bitmap);
                // TODO: Upload to Firebase Storage and save URL to Firestore
            } catch (IOException e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadUserProfile() {
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(document -> {
            if (document.exists()) {
                firstName.setText(document.getString("firstName") != null ? document.getString("firstName") : "");
                lastName.setText(document.getString("lastName") != null ? document.getString("lastName") : "");
                phoneNumber.setText(document.getString("phoneNumber") != null ? document.getString("phoneNumber") : "");
            } else {
                firstName.setText("");
                lastName.setText("");
                phoneNumber.setText("");
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            firstName.setText("");
            lastName.setText("");
            phoneNumber.setText("");
        });
    }

    private void updateProfile() {
        String fName = firstName.getText().toString().trim();
        String lName = lastName.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();

        if (TextUtils.isEmpty(fName) || TextUtils.isEmpty(lName)) {
            Toast.makeText(this, "First and last name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", fName);
        updates.put("lastName", lName);
        updates.put("phoneNumber", phone);

        db.collection("users").document(user.getUid())
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}