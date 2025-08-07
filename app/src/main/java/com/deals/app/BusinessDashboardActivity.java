
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

public class BusinessDashboardActivity extends AppCompatActivity implements BusinessOfferAdapter.OnOfferDeleteListener {
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
        businessOfferAdapter = new BusinessOfferAdapter(this, businessOfferList, this);
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
                        createDefaultBusiness();
                    }
                });
    }

    private void createDefaultBusiness() {
        String userId = firebaseManager.getCurrentUserId();
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("name", "My Business");
        businessData.put("ownerId", userId);
        businessData.put("city", "Default City");
        businessData.put("description", "Default business description");

        firebaseManager.getFirestore().collection("businesses")
                .add(businessData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentBusiness = new Business();
                        currentBusiness.setId(task.getResult().getId());
                        currentBusiness.setName("My Business");
                        currentBusiness.setOwnerId(userId);
                        currentBusiness.setCity("Default City");
                        currentBusiness.setDescription("Default business description");
                        loadBusinessOffers();
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

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Offer")
                .setView(dialogView)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(v -> {
                String title = titleEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String originalPriceStr = originalPriceEditText.getText().toString().trim();
                String discountedPriceStr = discountedPriceEditText.getText().toString().trim();
                String category = categorySpinner.getSelectedItem().toString();

                if (title.isEmpty() || description.isEmpty() || originalPriceStr.isEmpty() || discountedPriceStr.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double originalPrice = Double.parseDouble(originalPriceStr);
                    double discountedPrice = Double.parseDouble(discountedPriceStr);

                    if (discountedPrice >= originalPrice) {
                        Toast.makeText(this, "Discounted price must be less than original price", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    addOffer(title, description, originalPrice, discountedPrice, category);
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter valid prices", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
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
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        offer.put("expirationDate", calendar.getTimeInMillis());
        offer.put("createdAt", Calendar.getInstance().getTimeInMillis());

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
    public void onDelete(Offer offer) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Offer")
                .setMessage("Are you sure you want to delete this offer?")
                .setPositiveButton("Delete", (dialog, which) -> deleteOffer(offer))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteOffer(Offer offer) {
        firebaseManager.getFirestore().collection("offers")
                .document(offer.getId())
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Offer deleted successfully", Toast.LENGTH_SHORT).show();
                        loadBusinessOffers();
                    } else {
                        Toast.makeText(this, "Error deleting offer", Toast.LENGTH_SHORT).show();
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
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
