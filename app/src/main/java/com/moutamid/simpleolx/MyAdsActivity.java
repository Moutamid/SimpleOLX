package com.moutamid.simpleolx;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

        sellerAdsListView = findViewById(R.id.seller_ads_list);
        sellerAdsAdapter = new AdListAdapter(this, FirebaseAuth.getInstance().getCurrentUser().getUid());
        sellerAdsList = new ArrayList<>();
        sellerAdsListView.setAdapter(sellerAdsAdapter);

        adsRef = FirebaseDatabase.getInstance().getReference("Ads");

        fetchSellerAds();

    }

    private void fetchSellerAds() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adsRef.orderByChild("sellerUid").equalTo(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sellerAdsList.clear();

                for (DataSnapshot adSnapshot : snapshot.getChildren()) {
                    AdModel adModel = adSnapshot.getValue(AdModel.class);
                    sellerAdsList.add(adModel);
                }

                sellerAdsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
}
