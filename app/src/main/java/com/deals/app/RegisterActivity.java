
package com.deals.app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.deals.app.models.User;
import com.deals.app.utils.FirebaseManager;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private EditText businessNameEditText, businessCityEditText;
    private RadioGroup userTypeRadioGroup;
    private RadioButton customerRadioButton, businessOwnerRadioButton;
    private Button registerButton;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseManager = FirebaseManager.getInstance();

        initViews();
        setupListeners();
    }

    private void initViews() {
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        businessNameEditText = findViewById(R.id.businessNameEditText);
        businessCityEditText = findViewById(R.id.businessCityEditText);
        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup);
        customerRadioButton = findViewById(R.id.customerRadioButton);
        businessOwnerRadioButton = findViewById(R.id.businessOwnerRadioButton);
        registerButton = findViewById(R.id.registerButton);
    }

    private void setupListeners() {
        userTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.businessOwnerRadioButton) {
                businessNameEditText.setVisibility(android.view.View.VISIBLE);
                businessCityEditText.setVisibility(android.view.View.VISIBLE);
            } else {
                businessNameEditText.setVisibility(android.view.View.GONE);
                businessCityEditText.setVisibility(android.view.View.GONE);
            }
        });

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        String userType = businessOwnerRadioButton.isChecked() ? "business" : "customer";
        
        firebaseManager.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    saveUserData(name, email, userType);
                } else {
                    Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void saveUserData(String name, String email, String userType) {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) return;

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("role", userType);
        userData.put("dateCreated", System.currentTimeMillis());

        firebaseManager.getFirestore().collection("users").document(userId)
            .set(userData)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if ("business".equals(userType)) {
                        createBusinessProfile(name);
                    } else {
                        redirectToMainActivity();
                    }
                } else {
                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void createBusinessProfile(String ownerName) {
        String businessName = businessNameEditText.getText().toString().trim();
        String businessCity = businessCityEditText.getText().toString().trim();
        
        if (businessName.isEmpty()) businessName = ownerName + "'s Business";
        if (businessCity.isEmpty()) businessCity = "Not specified";

        Map<String, Object> businessData = new HashMap<>();
        businessData.put("name", businessName);
        businessData.put("ownerId", firebaseManager.getCurrentUserId());
        businessData.put("city", businessCity);
        businessData.put("description", "New business");
        businessData.put("dateCreated", System.currentTimeMillis());

        firebaseManager.getFirestore().collection("businesses")
            .add(businessData)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(this, BusinessDashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Failed to create business profile", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void redirectToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void setupListeners() {
        userTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.businessOwnerRadioButton) {
                businessNameEditText.setVisibility(android.view.View.VISIBLE);
                businessCityEditText.setVisibility(android.view.View.VISIBLE);
            } else {
                businessNameEditText.setVisibility(android.view.View.GONE);
                businessCityEditText.setVisibility(android.view.View.GONE);
            }
        });

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isBusinessOwner = businessOwnerRadioButton.isChecked();
        String businessName = "";
        String businessCity = "";

        if (isBusinessOwner) {
            businessName = businessNameEditText.getText().toString().trim();
            businessCity = businessCityEditText.getText().toString().trim();

            if (businessName.isEmpty() || businessCity.isEmpty()) {
                Toast.makeText(this, "Please fill business details", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        registerButton.setEnabled(false);

        String finalBusinessName = businessName;
        String finalBusinessCity = businessCity;

        firebaseManager.createUser(email, password, (authResult, exception) -> {
            registerButton.setEnabled(true);

            if (exception != null) {
                Toast.makeText(RegisterActivity.this, "Registration failed: " + exception.getMessage(), 
                    Toast.LENGTH_SHORT).show();
                return;
            }

            if (authResult != null && authResult.getUser() != null) {
                String userId = authResult.getUser().getUid();
                
                User user = new User();
                user.setId(userId);
                user.setName(name);
                user.setEmail(email);
                user.setBusinessOwner(isBusinessOwner);

                Map<String, Object> userData = new HashMap<>();
                userData.put("name", name);
                userData.put("email", email);
                userData.put("isBusinessOwner", isBusinessOwner);

                firebaseManager.getFirestore().collection("users")
                        .document(userId)
                        .set(userData)
                        .addOnCompleteListener(userTask -> {
                            if (userTask.isSuccessful()) {
                                if (isBusinessOwner) {
                                    createBusinessProfile(userId, finalBusinessName, finalBusinessCity);
                                } else {
                                    navigateToMainActivity();
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Error creating user profile", 
                                    Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void createBusinessProfile(String userId, String businessName, String businessCity) {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("name", businessName);
        businessData.put("city", businessCity);
        businessData.put("ownerId", userId);
        businessData.put("description", "");

        firebaseManager.getFirestore().collection("businesses")
                .add(businessData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        navigateToMainActivity();
                    } else {
                        Toast.makeText(this, "Error creating business profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToMainActivity() {
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
