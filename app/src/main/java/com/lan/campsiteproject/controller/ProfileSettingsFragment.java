package com.lan.campsiteproject.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.lan.campsiteproject.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileSettingsFragment extends Fragment {
    private EditText firstName, lastName, phoneNumber, email;
    private Button btnUpdate, btnChangePassword;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView avatarImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        email = view.findViewById(R.id.email);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        avatarImageView = view.findViewById(R.id.avatarImageView);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }

        email.setText(user.getEmail());
        email.setEnabled(false);
        email.setFocusable(false);

        loadUserProfile();

        btnUpdate.setOnClickListener(v -> updateProfile());
        btnChangePassword.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Change password feature not implemented", Toast.LENGTH_SHORT).show();
        });

        avatarImageView.setOnClickListener(v -> openImagePicker());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Avatar"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                avatarImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "First and last name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", fName);
        updates.put("lastName", lName);
        updates.put("phoneNumber", phone);

        db.collection("users").document(user.getUid())
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}