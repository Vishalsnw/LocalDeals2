
package com.deals.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.deals.app.adapters.BusinessOfferAdapter;
import com.deals.app.models.Business;
import com.deals.app.models.Offer;
import com.deals.app.models.Business;
import com.deals.app.utils.FirebaseManager;
import com.deals.app.adapters.BusinessOfferAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BusinessDashboardActivity extends AppCompatActivity {
    private RecyclerView businessOffersRecyclerView;
    private BusinessOfferAdapter businessOfferAdapter;
    private List<Offer> businessOfferList;
    private FloatingActionButton addOfferFab;
    private FirebaseManager firebaseManager;
    private Business currentBusiness;

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
        businessOfferAdapter = new BusinessOfferAdapter(this, businessOfferList, this::deleteOffer);
        businessOffersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        businessOffersRecyclerView.setAdapter(businessOfferAdapter);
    }

    private void loadBusinessInfo() {
        String userId = firebaseManager.getCurrentUserId();
        
        firebaseManager.getFirestore().collection("businesses")
                .whereEqualTo("ownerId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                            currentBusiness = document.toObject(Business.class);
                            currentBusiness.setBusinessId(document.getId());
                        } else {
                            // Create business profile if doesn't exist
                            showCreateBusinessDialog();
                        }
                    }
                });
    }

    private void loadBusinessOffers() {
        String userId = firebaseManager.getCurrentUserId();
        
        firebaseManager.getFirestore().collection("offers")
                .whereEqualTo("businessId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        businessOfferList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Offer offer = document.toObject(Offer.class);
                            offer.setOfferId(document.getId());
                            businessOfferList.add(offer);
                        }
                        businessOfferAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void showCreateBusinessDialog() {
        // Implementation for creating business profile dialog
        // This would show a dialog with business details form
        Toast.makeText(this, "Please create your business profile first", Toast.LENGTH_LONG).show();
    }

    private void showAddOfferDialog() {
        if (currentBusiness == null) {
            Toast.makeText(this, "Please create your business profile first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create and show offer creation dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_offer, null);
        
        EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        EditText originalPriceEditText = dialogView.findViewById(R.id.originalPriceEditText);
        EditText discountedPriceEditText = dialogView.findViewById(R.id.discountedPriceEditText);
        Button expirationDateButton = dialogView.findViewById(R.id.expirationDateButton);
        
        String[] categories = {"Food", "Shopping", "Entertainment", "Health", "Beauty", "Travel", "Electronics"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        final long[] expirationDate = {0};
        expirationDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        expirationDate[0] = selectedDate.getTimeInMillis();
                        expirationDateButton.setText(String.format("%d/%d/%d", dayOfMonth, month + 1, year));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        builder.setView(dialogView)
                .setTitle("Add New Offer")
                .setPositiveButton("Create", (dialog, which) -> {
                    String title = titleEditText.getText().toString().trim();
                    String description = descriptionEditText.getText().toString().trim();
                    String category = categorySpinner.getSelectedItem().toString();
                    String originalPriceStr = originalPriceEditText.getText().toString().trim();
                    String discountedPriceStr = discountedPriceEditText.getText().toString().trim();

                    if (validateOfferInput(title, description, originalPriceStr, discountedPriceStr, expirationDate[0])) {
                        double originalPrice = Double.parseDouble(originalPriceStr);
                        double discountedPrice = Double.parseDouble(discountedPriceStr);
                        
                        Offer offer = new Offer(
                                currentBusiness.getOwnerId(),
                                currentBusiness.getName(),
                                title,
                                description,
                                category,
                                currentBusiness.getCity(),
                                originalPrice,
                                discountedPrice,
                                "", // imageUrl - can be added later
                                expirationDate[0]
                        );

                        createOffer(offer);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private boolean validateOfferInput(String title, String description, String originalPrice, 
                                     String discountedPrice, long expirationDate) {
        if (title.isEmpty() || description.isEmpty() || originalPrice.isEmpty() || 
            discountedPrice.isEmpty() || expirationDate == 0) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double original = Double.parseDouble(originalPrice);
            double discounted = Double.parseDouble(discountedPrice);
            
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

    private void createOffer(Offer offer) {
        firebaseManager.getFirestore().collection("offers")
                .add(offer)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Offer created successfully!", Toast.LENGTH_SHORT).show();
                    loadBusinessOffers();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error creating offer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteOffer(Offer offer) {
        firebaseManager.getFirestore().collection("offers").document(offer.getOfferId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Offer deleted successfully!", Toast.LENGTH_SHORT).show();
                    loadBusinessOffers();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error deleting offer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            firebaseManager.getAuth().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
}
package com.deals.app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.deals.app.utils.FirebaseManager;

public class BusinessDashboardActivity extends AppCompatActivity {
    private FirebaseManager firebaseManager;

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
