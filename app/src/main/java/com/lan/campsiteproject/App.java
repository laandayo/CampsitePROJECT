package com.lan.campsiteproject;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class App extends Application {
    public static FirebaseFirestore db;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            FirebaseApp.initializeApp(this);
            db = FirebaseFirestore.getInstance();
            // Test Firestore connectivity
            db.collection("test").document("test").set(new HashMap<>())
                    .addOnSuccessListener(aVoid -> android.util.Log.d("App", "Firestore test write successful"))
                    .addOnFailureListener(e -> android.util.Log.e("App", "Firestore test failed: " + e.getMessage()));
        } catch (Exception e) {
            android.util.Log.e("App", "Firebase initialization failed: " + e.getMessage());
        }
    }
}