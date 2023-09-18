package com.moutamid.simpleolx.Admin.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.simpleolx.AdModel;
import com.moutamid.simpleolx.Constants;
import com.moutamid.simpleolx.R;
import com.moutamid.simpleolx.User.Activity.AdDetailActivity;
import com.moutamid.simpleolx.User.Adapter.AdListAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private ListView listView;
    private ListView listViewCategories;
    private AdListAdapter adListAdapter;
    private DatabaseReference adsRef;
    private Button btnAddCategory;
    private ArrayAdapter<String> categoryAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        listView = findViewById(R.id.listViewAds);
        adsRef = Constants.databaseReference().child("Ads");

        listViewCategories = findViewById(R.id.listViewCategories);
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listViewCategories.setAdapter(categoryAdapter);

        btnAddCategory = findViewById(R.id.btnAddCategory);
        adListAdapter = new AdListAdapter(this);
        listView.setAdapter(adListAdapter);

        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCategoryDialog();
            }
        });

        // Fetch categories from Firebase and populate the category adapter
//        DatabaseReference categoriesRef = Constants.databaseReference().child("categories");
//        categoriesRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<String> categories = new ArrayList<>();
//                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
//                    String categoryName = categorySnapshot.getValue(String.class);
//                    categories.add(categoryName);
//                }
//                categoryAdapter.clear();
//                categoryAdapter.addAll(categories);
//                categoryAdapter.notifyDataSetChanged();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle error
//            }
//        });

        listViewCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String categoryName = categoryAdapter.getItem(position);
                if (categoryName != null) {
                    showRemoveCategoryDialog(categoryName);
                }
            }
        });

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

    private void showAddCategoryDialog() {
       startActivity(new Intent(this, AddCategoryActivity.class));
    }


    private void showRemoveCategoryDialog(String categoryName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Remove Category");
        dialogBuilder.setMessage("Are you sure you want to remove this category: " + categoryName + "?");

        dialogBuilder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeCategoryFromDatabase(categoryName);
            }
        });

        dialogBuilder.setNegativeButton("Cancel", null);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void removeCategoryFromDatabase(String categoryName) {
        DatabaseReference categoriesRef = Constants.databaseReference().child("Categories");
        categoriesRef.orderByValue().equalTo(categoryName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    categorySnapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error == null) {
                                // Handle success
                            } else {
                                // Handle error
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
