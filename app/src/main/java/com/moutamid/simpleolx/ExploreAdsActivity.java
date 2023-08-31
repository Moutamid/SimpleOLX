package com.moutamid.simpleolx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExploreAdsActivity extends AppCompatActivity {

    private static final String TAG = "ExploreAdsActivity";
    private Spinner categorySpinner;
    private ListView adListView;
    private ArrayAdapter<String> categoryAdapter;
    private List<String> imageUrls;
    private AdListAdapter adListAdapter;
    private DatabaseReference adsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_ads);

        categorySpinner = findViewById(R.id.category_spinner);
        adListView = findViewById(R.id.ad_listview);

        adListAdapter = new AdListAdapter(this);
        adListView.setAdapter(adListAdapter);

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        fetchCategories();

        adsRef = Constants.databaseReference().child("Ads");

        adListAdapter.setOnItemClickListener(new AdListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AdModel selectedAd) {
                Intent intent = new Intent(ExploreAdsActivity.this, AdDetailActivity.class);
                intent.putExtra("title", selectedAd.getTitle());
                intent.putExtra("category", selectedAd.getCategory());
                intent.putExtra("description", selectedAd.getDescription());
                intent.putExtra("contact", selectedAd.getContact());

                ArrayList<String> imageUrls = new ArrayList<>(selectedAd.getImages());
                intent.putStringArrayListExtra("images", imageUrls);

                startActivity(intent);
            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categoryAdapter.getItem(position);
                fetchAdsByCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fetchAllAds();
            }
        });
    }

    private void fetchCategories() {
        DatabaseReference categoriesRef = Constants.databaseReference().child("Categories");

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> categories = new ArrayList<>();

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String categoryName = categorySnapshot.getValue(String.class);
                    categories.add(categoryName);
                }

                categoryAdapter.clear();
                categoryAdapter.addAll(categories);
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void fetchAdsByCategory(String category) {
        adsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adListAdapter.clear();

                for (DataSnapshot adSnapshot : snapshot.getChildren()) {
                    AdModel adModel = adSnapshot.getValue(AdModel.class);
                        if (adModel.isApproved() && adModel.getCategory().equals(category)) {
                            adListAdapter.add(adModel);
                        }
                }
                adListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void fetchAllAds() {
        adsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adListAdapter.clear();

                for (DataSnapshot adSnapshot : snapshot.getChildren()) {
                    AdModel adModel = adSnapshot.getValue(AdModel.class);
                        if (adModel.isApproved()) {
                            adListAdapter.add(adModel);
                    }
                }

                adListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
