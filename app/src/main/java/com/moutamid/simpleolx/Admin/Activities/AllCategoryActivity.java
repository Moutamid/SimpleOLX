package com.moutamid.simpleolx.Admin.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.icu.lang.UCharacter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.simpleolx.CategoryName;
import com.moutamid.simpleolx.R;
import com.moutamid.simpleolx.Admin.Adapter.AllCategroyAdapter;
import com.moutamid.simpleolx.helper.Config;
import com.moutamid.simpleolx.helper.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AllCategoryActivity extends AppCompatActivity {

    RecyclerView content_rcv;
    public List<CategoryName> videoModelList = new ArrayList<>();
    AllCategroyAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_category2);
        content_rcv = findViewById(R.id.content_rcv);
        content_rcv.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new AllCategroyAdapter(this, videoModelList);
        content_rcv.setAdapter(videoAdapter);
        if (Config.isNetworkAvailable(AllCategoryActivity.this)) {
            getVideos();
        } else {
            Toast.makeText(AllCategoryActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }

    }

    public void add_details(View view) {
        Intent intent = new Intent(this, AddCategoryActivity.class);
        intent.putExtra("url", "");
        intent.putExtra("thumbnail", "");
        intent.putExtra("key", "");
        startActivity(intent);
    }

    private void getVideos() {
        Dialog lodingbar = new Dialog(AllCategoryActivity.this);
        lodingbar.setContentView(R.layout.loading);
        Objects.requireNonNull(lodingbar.getWindow()).setBackgroundDrawable(new ColorDrawable(UCharacter.JoiningType.TRANSPARENT));
        lodingbar.setCancelable(false);
        lodingbar.show();
        Constants.CategoryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                videoModelList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    CategoryName videoModel = ds.getValue(CategoryName.class);
                    videoModelList.add(videoModel);
                }
                lodingbar.dismiss();
                videoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                lodingbar.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Config.isNetworkAvailable(AllCategoryActivity.this)) {
            getVideos();
        } else {
            Toast.makeText(AllCategoryActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    public void back(View view) {
        onBackPressed();
    }
}