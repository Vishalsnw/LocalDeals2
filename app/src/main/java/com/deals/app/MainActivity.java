package com.deals.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import com.deals.app.adapters.OfferAdapter;
import com.deals.app.models.Offer;
import com.deals.app.utils.FirebaseManager;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView offersRecyclerView;
    private OfferAdapter offerAdapter;
    private List<Offer> offerList;
    private SearchView searchView;
    private Spinner categorySpinner, citySpinner;
    private FirebaseManager firebaseManager;

    private String[] categories = {"All", "Food", "Shopping", "Entertainment", "Health", "Beauty", "Travel", "Electronics"};
    private String[] cities = {"All", "Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai", "Kolkata", "Pune"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseManager = FirebaseManager.getInstance();

        if (!firebaseManager.isUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupSpinners();
        loadOffers();
        addTestCrashButton();
    }

    private void addTestCrashButton() {
        // Only add test crash button in debug builds for testing
        if (BuildConfig.DEBUG) {
            Button crashButton = new Button(this);
            crashButton.setText("Test Crash");
            crashButton.setOnClickListener(v -> {
                throw new RuntimeException("Test Crash"); // Force a crash
            });

            addContentView(crashButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ));
        }
    }

    private void initViews() {
        offersRecyclerView = findViewById(R.id.offersRecyclerView);
        searchView = findViewById(R.id.searchView);
        categorySpinner = findViewById(R.id.categorySpinner);
        citySpinner = findViewById(R.id.citySpinner);
    }

    private void setupRecyclerView() {
        offerList = new ArrayList<>();
        offerAdapter = new OfferAdapter(this, offerList);
        offersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        offersRecyclerView.setAdapter(offerAdapter);
    }

    private void setupSpinners() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                filterOffers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                filterOffers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadOffers() {
        firebaseManager.getFirestore().collection("offers")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        offerList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Offer offer = document.toObject(Offer.class);
                            offer.setOfferId(document.getId());
                            offerList.add(offer);
                        }
                        offerAdapter.notifyDataSetChanged();
                    } else {
                        firebaseManager.logException(task.getException());
                    }
                });
    }

    private void filterOffers() {
        String searchQuery = searchView.getQuery().toString().toLowerCase();
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        String selectedCity = citySpinner.getSelectedItem().toString();

        Query query = firebaseManager.getFirestore().collection("offers")
                .whereEqualTo("isActive", true)
                .whereGreaterThan("expirationDate", System.currentTimeMillis());

        if (!"All".equals(selectedCategory)) {
            query = query.whereEqualTo("category", selectedCategory);
        }

        if (!"All".equals(selectedCity)) {
            query = query.whereEqualTo("city", selectedCity);
        }

        query.orderBy("expirationDate")
             .orderBy("createdAt", Query.Direction.DESCENDING)
             .get()
             .addOnCompleteListener(task -> {
                 if (task.isSuccessful()) {
                     offerList.clear();
                     for (QueryDocumentSnapshot document : task.getResult()) {
                         Offer offer = document.toObject(Offer.class);
                         offer.setOfferId(document.getId());

                         if (searchQuery.isEmpty() ||
                             offer.getTitle().toLowerCase().contains(searchQuery) ||
                             offer.getDescription().toLowerCase().contains(searchQuery) ||
                             offer.getBusinessName().toLowerCase().contains(searchQuery)) {
                             offerList.add(offer);
                         }
                     }
                     offerAdapter.notifyDataSetChanged();
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
            firebaseManager.getAuth().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}