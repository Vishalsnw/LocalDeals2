
package com.deals.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.deals.app.adapters.BusinessOfferAdapter;
import com.deals.app.models.Business;
import com.deals.app.models.Offer;
import com.deals.app.utils.FirebaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessDashboardActivity extends AppCompatActivity {
    private RecyclerView businessOffersRecyclerView;
    private BusinessOfferAdapter businessOfferAdapter;
    private List<Offer> businessOfferList;
    private FloatingActionButton addOfferFab;
    private FirebaseManager firebaseManager;
    private Business currentBusiness;

    private String[] categories = {"Food", "Shopping", "Entertainment", "Health", "Beauty", "Travel", "Electronics"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_dashboard);

        firebaseManager = FirebaseManager.getInstance();

        if (!firebaseManager.isUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        loadBusinessInfo();
        loadBusinessOffers();
    }

    private void initViews() {
        businessOffersRecyclerView = findViewById(R.id.businessOffersRecyclerView);
        addOfferFab = findViewById(R.id.addOfferFab);

        addOfferFab.setOnClickListener(v -> showAddOfferDialog());
    }

    private void setupRecyclerView() {
        businessOfferList = new ArrayList<>();
        businessOfferAdapter = new BusinessOfferAdapter(this, businessOfferList);
        businessOffersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        businessOffersRecyclerView.setAdapter(businessOfferAdapter);
    }

    private void loadBusinessInfo() {
        String userId = firebaseManager.getCurrentUserId();
        firebaseManager.getFirestore().collection("businesses")
                .whereEqualTo("ownerId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        currentBusiness = document.toObject(Business.class);
                        if (currentBusiness != null) {
                            currentBusiness.setId(document.getId());
                        }
                    } else {
                        // Create a default business if none exists
                        createDefaultBusiness();
                    }
                });
    }

    private void createDefaultBusiness() {
        String userId = firebaseManager.getCurrentUserId();
        Map<String, Object> business = new HashMap<>();
        business.put("ownerId", userId);
        business.put("name", "My Business");
        business.put("category", "General");
        business.put("city", "Mumbai");
        business.put("address", "");
        business.put("phone", "");
        business.put("email", "");

        firebaseManager.getFirestore().collection("businesses")
                .add(business)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loadBusinessInfo();
                    }
                });
    }

    private void loadBusinessOffers() {
        if (currentBusiness == null) return;
        
        firebaseManager.getFirestore().collection("offers")
                .whereEqualTo("businessId", currentBusiness.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        businessOfferList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Offer offer = document.toObject(Offer.class);
                            offer.setId(document.getId());
                            businessOfferList.add(offer);
                        }
                        businessOfferAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading offers", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddOfferDialog() {
        if (currentBusiness == null) {
            Toast.makeText(this, "Please wait for business info to load", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_offer, null);
        
        EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        EditText originalPriceEditText = dialogView.findViewById(R.id.originalPriceEditText);
        EditText discountedPriceEditText = dialogView.findViewById(R.id.discountedPriceEditText);
        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Offer")
                .setView(dialogView)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String title = titleEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String originalPriceStr = originalPriceEditText.getText().toString().trim();
                String discountedPriceStr = discountedPriceEditText.getText().toString().trim();
                String category = categorySpinner.getSelectedItem().toString();

                if (validateOfferInput(title, description, originalPriceStr, discountedPriceStr)) {
                    addOffer(title, description, Double.parseDouble(originalPriceStr), 
                            Double.parseDouble(discountedPriceStr), category);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private boolean validateOfferInput(String title, String description, String originalPrice, String discountedPrice) {
        if (title.isEmpty()) {
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (description.isEmpty()) {
            Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            double original = Double.parseDouble(originalPrice);
            double discounted = Double.parseDouble(discountedPrice);
            if (original <= 0 || discounted <= 0) {
                Toast.makeText(this, "Prices must be greater than 0", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (discounted >= original) {
                Toast.makeText(this, "Discounted price must be less than original price", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid prices", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void addOffer(String title, String description, double originalPrice, double discountedPrice, String category) {
        Map<String, Object> offer = new HashMap<>();
        offer.put("title", title);
        offer.put("description", description);
        offer.put("originalPrice", originalPrice);
        offer.put("discountedPrice", discountedPrice);
        offer.put("category", category);
        offer.put("businessId", currentBusiness.getId());
        offer.put("businessName", currentBusiness.getName());
        offer.put("city", currentBusiness.getCity());
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30); // Default 30 days validity
        offer.put("expiryDate", calendar.getTime());
        offer.put("createdAt", Calendar.getInstance().getTime());

        firebaseManager.getFirestore().collection("offers")
                .add(offer)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Offer added successfully", Toast.LENGTH_SHORT).show();
                        loadBusinessOffers();
                    } else {
                        Toast.makeText(this, "Error adding offer", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            firebaseManager.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
