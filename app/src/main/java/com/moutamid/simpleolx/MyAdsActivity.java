package com.moutamid.simpleolx;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyAdsActivity extends AppCompatActivity {

    private ListView sellerAdsListView;
    private List<AdModel> sellerAdsList;
    private AdListAdapter sellerAdsAdapter;
    private DatabaseReference adsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_ads_activity);

        // Initialize views and adapter
        initializeViewsAndAdapter();

        // Fetch and display seller ads
        fetchAndDisplaySellerAds();
    }

    private void initializeViewsAndAdapter() {
        // Find ListView and set up adapter
        sellerAdsListView = findViewById(R.id.seller_ads_list);
        sellerAdsAdapter = new AdListAdapter(this, Constants.auth().getCurrentUser().getUid());
        sellerAdsListView.setAdapter(sellerAdsAdapter);

        // Initialize the sellerAdsList
        sellerAdsList = new ArrayList<>();
    }

    private void fetchAndDisplaySellerAds() {
        // Get a reference to the "Ads" section in the database
        adsRef = Constants.databaseReference().child("Ads");

        // Get the current user's UID
        String currentUserUid = Constants.auth().getCurrentUser().getUid();

        // Query the ads belonging to the current user
        adsRef.orderByChild("sellerUid").equalTo(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the existing list before adding new ads
                sellerAdsList.clear();

                // Iterate through the retrieved ads and add them to the list
                for (DataSnapshot adSnapshot : snapshot.getChildren()) {
                    AdModel adModel = adSnapshot.getValue(AdModel.class);
                    sellerAdsList.add(adModel);
                }

                // Update the adapter's data and notify it to refresh the UI
                sellerAdsAdapter.clear();
                sellerAdsAdapter.addAll(sellerAdsList);
                sellerAdsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors or exceptions here
            }
        });
    }
}
