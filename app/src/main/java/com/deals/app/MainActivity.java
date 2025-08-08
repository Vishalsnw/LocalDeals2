
package com.deals.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
    private List<Offer> filteredOfferList;
    private SearchView searchView;
    private Spinner categorySpinner, citySpinner;
    
    private FirebaseManager firebaseManager;

    private String[] categories = {"All", "Food", "Shopping", "Entertainment", "Health", "Beauty", "Travel", "Electronics"};
    private String[] cities = {"All", "Mumbai", "Delhi", "Bangalore", "Chennai", "Kolkata", "Hyderabad", "Pune"};

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
    }

    private void initViews() {
        offersRecyclerView = findViewById(R.id.offersRecyclerView);
        searchView = findViewById(R.id.searchView);
        categorySpinner = findViewById(R.id.categorySpinner);
        citySpinner = findViewById(R.id.citySpinner);

        
    }

    private void setupRecyclerView() {
        offerList = new ArrayList<>();
        filteredOfferList = new ArrayList<>();
        offerAdapter = new OfferAdapter(this, filteredOfferList);
        offersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        offersRecyclerView.setAdapter(offerAdapter);
    }

    private void setupSpinners() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
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

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterOffers();
                return true;
            }
        });
    }

    private void loadOffers() {
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();

        firebaseManager.getFirestore().collection("offers")
                .whereGreaterThan("expirationDate", currentTime)
                .orderBy("expirationDate", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        offerList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Offer offer = document.toObject(Offer.class);
                            offer.setId(document.getId());
                            offerList.add(offer);
                        }
                        filterOffers();
                    } else {
                        Toast.makeText(this, "Error loading offers", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterOffers() {
        String searchQuery = searchView.getQuery().toString().toLowerCase().trim();
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        String selectedCity = citySpinner.getSelectedItem().toString();

        filteredOfferList.clear();

        for (Offer offer : offerList) {
            boolean matchesSearch = searchQuery.isEmpty() ||
                    offer.getTitle().toLowerCase().contains(searchQuery) ||
                    offer.getDescription().toLowerCase().contains(searchQuery) ||
                    offer.getBusinessName().toLowerCase().contains(searchQuery);

            boolean matchesCategory = selectedCategory.equals("All") ||
                    offer.getCategory().equals(selectedCategory);

            boolean matchesCity = selectedCity.equals("All") ||
                    offer.getCity().equals(selectedCity);

            if (matchesSearch && matchesCategory && matchesCity) {
                filteredOfferList.add(offer);
            }
        }

        offerAdapter.notifyDataSetChanged();
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
            startActivity(new Intent(this, BusinessDashboardActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            firebaseManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
