
package com.deals.app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.deals.app.utils.FirebaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerLink;
    private ProgressBar progressBar;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseManager = FirebaseManager.getInstance();

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> loginUser());
        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        firebaseManager.getFirebaseAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        firebaseManager.setUserId(firebaseManager.getCurrentUserId());
                        firebaseManager.log("User logged in successfully");
                        
                        // Check user role and redirect accordingly
                        checkUserRoleAndRedirect();
                    } else {
                        Exception exception = task.getException();
                        Toast.makeText(LoginActivity.this, "Login failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        firebaseManager.logException(exception);
                        firebaseManager.log("Login failed for email: " + email);
                    }
                });
    }

    private void checkUserRoleAndRedirect() {
        String userId = firebaseManager.getCurrentUserId();
        firebaseManager.getFirestore().collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String role = document.getString("role");
                            if ("business".equals(role)) {
                                startActivity(new Intent(LoginActivity.this, BusinessDashboardActivity.class));
                            } else {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                            finish();
                        } else {
                            // User document doesn't exist, redirect to main activity
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    } else {
                        // Error getting user document, redirect to main activity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }
}
