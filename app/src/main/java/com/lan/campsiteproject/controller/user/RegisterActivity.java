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
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.FirebaseException;
import java.util.concurrent.TimeUnit;
import android.graphics.Color;
import android.widget.RadioGroup;
import android.widget.RadioButton;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private FirebaseFirestore db;
    private String verificationId;
    private boolean isPhoneVerified = false;

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

        Button sendOtpButton = findViewById(R.id.sendOtpButton);
        Button verifyOtpButton = findViewById(R.id.verifyOtpButton);
        EditText otpField = findViewById(R.id.otpField);

        RadioGroup genderRadioGroup = findViewById(R.id.genderRadioGroup);
        RadioButton radioMale = findViewById(R.id.radioMale);
        RadioButton radioFemale = findViewById(R.id.radioFemale);
        RadioButton radioOther = findViewById(R.id.radioOther);

        sendOtpButton.setOnClickListener(v -> {
            String phoneInput = phoneNumberField.getText().toString().trim();
            String phone = phoneInput.startsWith("0") ? "+84" + phoneInput.substring(1) : phoneInput;


            if (phoneInput.isEmpty()) {
                Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            final String finalPhone = phone;
            if (phone.startsWith("0")) {
                phone = "+84" + phone.substring(1);
            }

            // Check if phone exists in Firestore
            db.collection("users")
                    .whereEqualTo("phoneNumber", finalPhone)
                    .get()
                    .addOnSuccessListener(query -> {
                        if (!query.isEmpty()) {
                            Toast.makeText(this, "Phone number already used", Toast.LENGTH_SHORT).show();
                        } else {
                            // Send OTP
                            PhoneAuthOptions options =
                                    PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                                            .setPhoneNumber(finalPhone)
                                            .setTimeout(60L, TimeUnit.SECONDS)
                                            .setActivity(this)
                                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                                @Override
                                                public void onVerificationCompleted(PhoneAuthCredential credential) {
                                                    isPhoneVerified = true;
                                                    disableOtpButtons(sendOtpButton, verifyOtpButton);
                                                    Toast.makeText(RegisterActivity.this, "Phone verified automatically", Toast.LENGTH_SHORT).show();
                                                }
                                                @Override
                                                public void onVerificationFailed(FirebaseException e) {
                                                    Toast.makeText(RegisterActivity.this, "OTP failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                                @Override
                                                public void onCodeSent(String id, PhoneAuthProvider.ForceResendingToken token) {
                                                    verificationId = id;
                                                    Toast.makeText(RegisterActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .build();
                            PhoneAuthProvider.verifyPhoneNumber(options);
                        }
                    });
        });

        verifyOtpButton.setOnClickListener(v -> {
            String code = otpField.getText().toString().trim();
            if (verificationId == null || code.isEmpty()) {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            isPhoneVerified = true;
                            disableOtpButtons(sendOtpButton, verifyOtpButton);
                            Toast.makeText(this, "Phone verified", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "OTP incorrect", Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        registerButton.setOnClickListener(v -> {
            String password = passwordField.getText().toString().trim();
            String passwordAgain = passwordAgainField.getText().toString().trim();
            String phone = phoneNumberField.getText().toString().trim();
            String gender;String packageName = getPackageName();
            String profileImageUri = "android.resource://" + packageName + "/drawable/profile";
            String phoneInput = phoneNumberField.getText().toString().trim();
            String finalPhone = phoneInput.startsWith("0") ? "+84" + phoneInput.substring(1) : phoneInput;

            if (radioMale.isChecked()) {
                gender = "male";
            } else if (radioFemale.isChecked()) {
                gender = "female";
            } else {
                gender = "other";
            }

            if (!isValidPassword(password)) {
                Toast.makeText(this, "Password must be at least 8 characters, include upper, lower, digit, and special character", Toast.LENGTH_LONG).show();
                return;
            }



            if (phone.isEmpty()) {
                Toast.makeText(this, "Please enter the phone number!", Toast.LENGTH_SHORT).show();
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
            if (!isPhoneVerified) {
                Toast.makeText(this, "Please verify your phone number with OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            // Build userData before using it
            Map<String, Object> userData = new HashMap<>();
            userData.put("email", email);
            userData.put("firstName", firstNameField.getText().toString().trim());
            userData.put("lastName", lastNameField.getText().toString().trim());
            userData.put("phoneNumber", finalPhone);
            userData.put("isAdmin", false);
            userData.put("isOwner", false);
            userData.put("profileImageUrl", profileImageUri);
            userData.put("gender", gender);

            // Only create Firebase Auth user if firebaseUid is null
            if (firebaseUid != null) {
                userData.put("firebaseUid", firebaseUid);
                db.collection("users").document(firebaseUid)
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
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
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
                                            // Delete the Auth user if Firestore registration fails
                                            task.getResult().getUser().delete();
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
            if (firebaseUid != null) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && user.getUid().equals(firebaseUid)) {
                    user.delete();
                }
            }
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void disableOtpButtons(Button sendOtpButton, Button verifyOtpButton) {
        sendOtpButton.setEnabled(false);
        verifyOtpButton.setEnabled(false);
        sendOtpButton.setBackgroundColor(Color.GRAY);
        verifyOtpButton.setBackgroundColor(Color.GRAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Only delete if registration was not completed (user still exists and came from Google sign-in)
        String firebaseUid = getIntent().getStringExtra("firebase_uid");
        if (firebaseUid != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.getUid().equals(firebaseUid)) {
                user.delete();
            }
        }
    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        // At least 8 chars, 1 upper, 1 lower, 1 digit, 1 special char
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(pattern);
    }

}