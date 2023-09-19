package com.moutamid.simpleolx.Admin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.simpleolx.AdModel;
import com.moutamid.simpleolx.Constants;
import com.moutamid.simpleolx.R;
import com.moutamid.simpleolx.User.Activity.AdDetailActivity;
import com.moutamid.simpleolx.Admin.Adapter.AdListAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private ListView listView;
    private AdListAdapter adListAdapter;
    private DatabaseReference adsRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        listView = findViewById(R.id.listViewAds);
        adsRef = Constants.databaseReference().child("Ads");
        adListAdapter = new AdListAdapter(this);
        listView.setAdapter(adListAdapter);



        adListAdapter.setOnItemClickListener(new AdListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AdModel selectedAd) {
                Intent intent = new Intent(AdminActivity.this, AdDetailActivity.class);
                intent.putExtra("title", selectedAd.getTitle());
                intent.putExtra("category", selectedAd.getCategory());
                intent.putExtra("description", selectedAd.getDescription());
                intent.putExtra("contact", selectedAd.getContact());

                ArrayList<String> imageUrls = new ArrayList<>(selectedAd.getImages());
                intent.putStringArrayListExtra("images", imageUrls);

                startActivity(intent);
            }
        });

        adsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<AdModel> adList = new ArrayList<>();
                for (DataSnapshot adSnapshot : snapshot.getChildren()) {
                    AdModel adModel = adSnapshot.getValue(AdModel.class);
                    adList.add(adModel);
                }
                adListAdapter.clear();
                adListAdapter.addAll(adList);
                adListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

   }
