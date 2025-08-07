
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
import android.widget.Toast;

import com.deals.app.adapters.OfferAdapter;
import com.deals.app.models.Offer;
import com.deals.app.utils.FirebaseManager;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
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
        setupSearchView();
        loadOffers();
        addTestCrashButton();
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
        // Setup category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                filterOffers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Setup city spinner
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                filterOffers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterOffers();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterOffers();
                return false;
            }
        });
    }

    private void filterOffers() {
        String searchText = searchView.getQuery().toString().toLowerCase();
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        String selectedCity = citySpinner.getSelectedItem().toString();

        List<Offer> filteredList = new ArrayList<>();
        for (Offer offer : offerList) {
            boolean matchesSearch = searchText.isEmpty() || 
                offer.getTitle().toLowerCase().contains(searchText) ||
                offer.getDescription().toLowerCase().contains(searchText);
            
            boolean matchesCategory = selectedCategory.equals("All") || 
                offer.getCategory().equals(selectedCategory);
            
            boolean matchesCity = selectedCity.equals("All") || 
                offer.getCity().equals(selectedCity);

            if (matchesSearch && matchesCategory && matchesCity) {
                filteredList.add(offer);
            }
        }
        offerAdapter.updateOffers(filteredList);
    }

    private void loadOffers() {
        firebaseManager.getFirestore().collection("offers")
            .whereGreaterThan("expiryDate", Calendar.getInstance().getTime())
            .orderBy("expiryDate", Query.Direction.ASCENDING)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    offerList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Offer offer = document.toObject(Offer.class);
                        offer.setId(document.getId());
                        offerList.add(offer);
                    }
                    offerAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "Error loading offers: " + task.getException().getMessage(), 
                        Toast.LENGTH_SHORT).show();
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

    private void addTestCrashButton() {
        try {
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
        } catch (Exception e) {
            // Ignore any issues with adding the crash button
        }
    }
}
