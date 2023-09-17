package com.moutamid.simpleolx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.simpleolx.User.Adapter.CategroyAdapter;
import com.moutamid.simpleolx.User.Adapter.ItemsAdapter;
import com.moutamid.simpleolx.User.AllItemsActivity;
import com.moutamid.simpleolx.helper.Config;
import com.moutamid.simpleolx.helper.Constants;

import java.util.ArrayList;
import java.util.List;

public class ExploreAdsActivity extends AppCompatActivity {

    private static final String TAG = "ExploreAdsActivity";
    //    private Spinner categorySpinner;
    private RecyclerView adListView;

    private ItemsAdapter adListAdapter;
    private DatabaseReference adsRef;
    RecyclerView content_rcv;
    public List<CategoryName> categoryNameList = new ArrayList<>();
    public List<AdModel> adModelArrayList = new ArrayList<>();
    CategroyAdapter categroyAdapter;
    TextView see_store;
    ImageView settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_ads);

        see_store = findViewById(R.id.see_store);
        settings = findViewById(R.id.settings);
        content_rcv = findViewById(R.id.categories);
        content_rcv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        categroyAdapter = new CategroyAdapter(this, categoryNameList);
        content_rcv.setAdapter(categroyAdapter);
        Config.checkApp(ExploreAdsActivity.this);
        if (Config.isNetworkAvailable(ExploreAdsActivity.this)) {
            fetchCategories();

        } else {
            Toast.makeText(ExploreAdsActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
        adListView = findViewById(R.id.ad_listview);
        adListView.setLayoutManager(new GridLayoutManager(this, 2));
        adListAdapter = new ItemsAdapter(this, adModelArrayList);
        adListView.setAdapter(adListAdapter);
        adsRef = Constants.databaseReference().child("Ads");
        fetchAllAds();
        see_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExploreAdsActivity.this, AllItemsActivity.class));
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (com.moutamid.simpleolx.Constants.auth().getCurrentUser() != null) {
                    startActivity(new Intent(ExploreAdsActivity.this, SellerHomeActivity.class));
                } else {
                    Toast.makeText(ExploreAdsActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void fetchCategories() {

        Constants.CategoryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    CategoryName value = categorySnapshot.getValue(CategoryName.class);
                    categoryNameList.add(value);
                }


                categroyAdapter.notifyDataSetChanged();
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
                adModelArrayList.clear();

                for (DataSnapshot adSnapshot : snapshot.getChildren()) {
                    AdModel adModel = adSnapshot.getValue(AdModel.class);
                        if (adModel.isApproved()) {
                            adModelArrayList.add(adModel);
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
