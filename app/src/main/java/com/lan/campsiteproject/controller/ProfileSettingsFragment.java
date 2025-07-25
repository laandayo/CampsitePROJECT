package com.lan.campsiteproject.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.controller.user.ChangePasswordActivity;

import java.util.HashMap;
import java.util.Map;

public class ProfileSettingsFragment extends Fragment {

    private EditText firstName, lastName, phoneNumber, email;
    private Button btnUpdate, btnChangePassword;
    private ImageView avatarImageView;

    private FirebaseFirestore db;
    private FirebaseUser user;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        avatarImageView.setImageURI(imageUri);
                        uploadAvatarToFirebase(imageUri);
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        email = view.findViewById(R.id.email);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        avatarImageView = view.findViewById(R.id.avatarImageView);

        email.setEnabled(false);
        email.setFocusable(false);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(getContext(), "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return view;
        }
        email.setText(user.getEmail());

        loadUserProfile();

        btnUpdate.setOnClickListener(v -> updateProfile());

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        avatarImageView.setOnClickListener(v -> openImagePicker());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Chọn ảnh đại diện"));
    }

    private void uploadAvatarToFirebase(Uri imageUri) {
        StorageReference ref = FirebaseStorage.getInstance()
                .getReference("avatars/" + user.getUid() + ".jpg");

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    db.collection("users").document(user.getUid())
                            .update("avatarUrl", imageUrl)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(getContext(), "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show()
                            );
                }))
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void loadUserProfile() {
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(document -> {
            if (document.exists()) {
                firstName.setText(document.getString("firstName") != null ? document.getString("firstName") : "");
                lastName.setText(document.getString("lastName") != null ? document.getString("lastName") : "");
                phoneNumber.setText(document.getString("phoneNumber") != null ? document.getString("phoneNumber") : "");

                String avatarUrl = document.getString("avatarUrl");
                if (!TextUtils.isEmpty(avatarUrl)) {
                    Glide.with(this)
                            .load(avatarUrl)
                            .placeholder(R.drawable.avatar)
                            .into(avatarImageView);
                }
            } else {
                firstName.setText("");
                lastName.setText("");
                phoneNumber.setText("");
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Lỗi tải thông tin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Họ và tên không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", fName);
        updates.put("lastName", lName);
        updates.put("phoneNumber", phone);

        db.collection("users").document(user.getUid())
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}