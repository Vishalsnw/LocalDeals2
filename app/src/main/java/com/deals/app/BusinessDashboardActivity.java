
package com.deals.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.deals.app.adapters.BusinessOfferAdapter;
import com.deals.app.models.Business;
import com.deals.app.models.Offer;
import com.deals.app.utils.FirebaseManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.deals.app.utils.FirebaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
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
        Toast.makeText(this, "Please create your business profile first", Toast.LENGTH_LONG).show();
    }

    private void showAddOfferDialog() {
        if (currentBusiness == null) {
            Toast.makeText(this, "Please create your business profile first", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_offer, null);

        EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        EditText originalPriceEditText = dialogView.findViewById(R.id.originalPriceEditText);
        EditText discountedPriceEditText = dialogView.findViewById(R.id.discountedPriceEditText);
        EditText termsEditText = dialogView.findViewById(R.id.termsEditText);
        Button selectDateButton = dialogView.findViewById(R.id.selectDateButton);
        Button createOfferButton = dialogView.findViewById(R.id.createOfferButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Setup category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        final long[] selectedDate = {0};

        selectDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, dayOfMonth, 23, 59, 59);
                        selectedDate[0] = selected.getTimeInMillis();
                        selectDateButton.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        createOfferButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String category = categorySpinner.getSelectedItem().toString();
            String originalPriceStr = originalPriceEditText.getText().toString().trim();
            String discountedPriceStr = discountedPriceEditText.getText().toString().trim();
            String terms = termsEditText.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty() || originalPriceStr.isEmpty() || 
                discountedPriceStr.isEmpty() || selectedDate[0] == 0) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double originalPrice = Double.parseDouble(originalPriceStr);
            double discountedPrice = Double.parseDouble(discountedPriceStr);
            int discountPercentage = (int) (((originalPrice - discountedPrice) / originalPrice) * 100);

            Offer offer = new Offer(
                    title,
                    description,
                    category,
                    currentBusiness.getCity(),
                    currentBusiness.getOwnerId(),
                    currentBusiness.getBusinessName(),
                    originalPrice,
                    discountedPrice,
                    discountPercentage,
                    selectedDate[0],
                    terms
            );

            createOffer(offer);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
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
        if (item.getItemId() == R.id.action_logout) {
            firebaseManager.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
