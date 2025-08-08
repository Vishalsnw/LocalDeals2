
package com.deals.app.utils;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
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

    public FirebaseFirestore getDb() {
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

    public void signOut() {
        auth.signOut();
    }

    public FirebaseCrashlytics getCrashlytics() {
        return crashlytics;
    }

    public void logException(Exception e) {
        Log.e(TAG, "Exception occurred", e);
        crashlytics.recordException(e);
    }

    public void setUserId(String userId) {
        crashlytics.setUserId(userId);
    }

    public void setCustomKey(String key, String value) {
        crashlytics.setCustomKey(key, value);
    }

    public void log(String message) {
        Log.d(TAG, message);
        crashlytics.log(message);
    }

    public com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult> createUserWithEmailAndPassword(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    public com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult> signInWithEmailAndPassword(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    public void logout() {
        auth.signOut();
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }
}
package com.deals.app.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.AuthResult;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private FirebaseManager() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return firebaseAuth;
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }

    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public void logout() {
        firebaseAuth.signOut();
    }

    public interface AuthCallback {
        void onResult(AuthResult authResult, Exception exception);
    }

    public void createUser(String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onResult(task.getResult(), null);
                } else {
                    callback.onResult(null, task.getException());
                }
            });
    }

    public void signInUser(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onResult(task.getResult(), null);
                } else {
                    callback.onResult(null, task.getException());
                }
            });
    }
}
