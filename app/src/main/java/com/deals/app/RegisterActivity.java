package com.deals.app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.deals.app.models.User;
import com.deals.app.models.Business;
import com.deals.app.utils.FirebaseManager;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private EditText businessNameEditText, businessCityEditText;
    private RadioGroup userTypeRadioGroup;
    private RadioButton customerRadioButton, businessOwnerRadioButton;
    private Button registerButton;
    private View businessInfoLayout;
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
        businessInfoLayout = findViewById(R.id.businessInfoLayout);

        userTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.businessOwnerRadioButton) {
                businessInfoLayout.setVisibility(View.VISIBLE);
            } else {
                businessInfoLayout.setVisibility(View.GONE);
            }
        });
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> registerUser());

        userTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.businessOwnerRadioButton) {
                businessInfoLayout.setVisibility(View.VISIBLE);
            } else {
                businessInfoLayout.setVisibility(View.GONE);
            }
        });
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
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = userTypeRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select user type", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isBusinessOwner = selectedId == R.id.businessOwnerRadioButton;

        if (isBusinessOwner) {
            String businessName = businessNameEditText.getText().toString().trim();
            String businessCity = businessCityEditText.getText().toString().trim();

            if (businessName.isEmpty() || businessCity.isEmpty()) {
                Toast.makeText(this, "Please fill business information", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        registerButton.setEnabled(false);

        firebaseManager.createUser(email, password, (authResult, exception) -> {
            if (exception != null) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                    registerButton.setEnabled(true);
                });
                return;
            }

            if (authResult != null && authResult.getUser() != null) {
                String userId = authResult.getUser().getUid();
                User user = new User(name, email, isBusinessOwner ? "business" : "customer");
                user.setId(userId);
                user.setBusinessOwner(isBusinessOwner);

                saveUserToFirestore(user, isBusinessOwner);
            }
        });
    }

    private void saveUserToFirestore(User user, boolean isBusinessOwner) {
        firebaseManager.getFirestore().collection("users")
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    if (isBusinessOwner) {
                        saveBusiness(user);
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        registerButton.setEnabled(true);
                    });
                });
    }

    private void saveBusiness(User user) {
        String businessName = businessNameEditText.getText().toString().trim();
        String businessCity = businessCityEditText.getText().toString().trim();

        Business business = new Business(businessName, businessCity, user.getId());
        business.setOwnerId(user.getId());

        firebaseManager.getFirestore().collection("businesses")
                .add(business)
                .addOnSuccessListener(documentReference -> {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    });
                })
                .addOnFailureListener(e -> {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Failed to save business data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        registerButton.setEnabled(true);
                    });
                });
    }
}