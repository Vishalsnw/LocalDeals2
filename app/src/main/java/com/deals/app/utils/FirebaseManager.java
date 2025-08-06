
package com.deals.app.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseCrashlytics crashlytics;

    private FirebaseManager() {
        try {
            auth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();
            storage = FirebaseStorage.getInstance();
            crashlytics = FirebaseCrashlytics.getInstance();
        } catch (Exception e) {
            crashlytics.recordException(e);
            throw new RuntimeException("Firebase initialization failed: " + e.getMessage(), e);
        }
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public StorageReference getStorageReference() {
        return storage.getReference();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public FirebaseCrashlytics getCrashlytics() {
        return crashlytics;
    }

    public void logException(Exception e) {
        crashlytics.recordException(e);
    }

    public void setUserId(String userId) {
        crashlytics.setUserId(userId);
    }

    public void setCustomKey(String key, String value) {
        crashlytics.setCustomKey(key, value);
    }

    public void log(String message) {
        crashlytics.log(message);
    }
}
package com.deals.app.utils;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseCrashlytics crashlytics;

    public FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        crashlytics = FirebaseCrashlytics.getInstance();
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void signOut() {
        mAuth.signOut();
    }

    public void log(String message) {
        Log.d(TAG, message);
        crashlytics.log(message);
    }

    public void logException(Exception exception) {
        Log.e(TAG, "Exception occurred", exception);
        crashlytics.recordException(exception);
    }

    public void setUserId(String userId) {
        crashlytics.setUserId(userId);
    }

    public void setCustomKey(String key, String value) {
        crashlytics.setCustomKey(key, value);
    }
}
