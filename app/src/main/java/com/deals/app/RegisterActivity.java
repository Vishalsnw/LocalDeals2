
package com.deals.app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.deals.app.models.User;
import com.deals.app.utils.FirebaseManager;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameEditText, emailEditText, passwordEditText;
    private Spinner citySpinner;
    private RadioGroup roleRadioGroup;
    private Button registerButton;
    private TextView loginLink;
    private ProgressBar progressBar;
    private FirebaseManager firebaseManager;

    private String[] indianCities = {
        "Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai", "Kolkata", 
        "Pune", "Ahmedabad", "Jaipur", "Lucknow", "Kanpur", "Nagpur",
        "Indore", "Thane", "Bhopal", "Visakhapatnam", "Patna", "Vadodara",
        "Ghaziabad", "Ludhiana", "Agra", "Nashik", "Faridabad", "Meerut"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseManager = FirebaseManager.getInstance();
        
        initViews();
        setupSpinner();
        setupClickListeners();
    }

    private void initViews() {
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        citySpinner = findViewById(R.id.citySpinner);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, indianCities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);
    }

    private void setupClickListeners() {
        registerButton.setOnClickListener(v -> registerUser());
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String city = citySpinner.getSelectedItem().toString();
        
        int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRoleButton = findViewById(selectedRoleId);
        String role = selectedRoleButton.getText().toString().toLowerCase().replace(" ", "_");

        if (validateInput(name, email, password)) {
            progressBar.setVisibility(View.VISIBLE);
            registerButton.setEnabled(false);

            firebaseManager.getAuth().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = firebaseManager.getCurrentUserId();
                            User user = new User(userId, email, name, city, role);
                            
                            saveUserToFirestore(user);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            registerButton.setEnabled(true);
                            Toast.makeText(RegisterActivity.this, 
                                "Registration failed: " + task.getException().getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean validateInput(String name, String email, String password) {
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return false;
        }

        if (roleRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveUserToFirestore(User user) {
        firebaseManager.getFirestore().collection("users").document(user.getUid())
                .set(user)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    registerButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        
                        Intent intent;
                        if ("business_owner".equals(user.getRole())) {
                            intent = new Intent(RegisterActivity.this, BusinessDashboardActivity.class);
                        } else {
                            intent = new Intent(RegisterActivity.this, MainActivity.class);
                        }
                        
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error saving user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
