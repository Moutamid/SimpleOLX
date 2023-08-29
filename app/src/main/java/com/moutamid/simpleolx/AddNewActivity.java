package com.moutamid.simpleolx;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AddNewActivity extends AppCompatActivity {

    private EditText editTitle, editDescription, editContact;
    private Spinner spinnerCategory;
    private static final int REQUEST_CODE_IMAGES = 1;
    private List<String> selectedImages = new ArrayList<>();
    private Button btnAddImages, btnPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_activity);

        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("Categories");

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editContact = findViewById(R.id.editContact);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnAddImages = findViewById(R.id.btnAddImages);
        btnPreview = findViewById(R.id.btnPreview);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> categories = new ArrayList<>();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String categoryName = categorySnapshot.getValue(String.class);
                    categories.add(categoryName);
                }
                categoryAdapter.addAll(categories);
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });


        btnAddImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open image picker and handle selected images
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, REQUEST_CODE_IMAGES);
            }
        });

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString();
                String description = editDescription.getText().toString();
                String contact = editContact.getText().toString();
                String category = spinnerCategory.getSelectedItem().toString();

                Intent intent = new Intent(AddNewActivity.this, AdDetailActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("contact", contact);
                intent.putExtra("category", category);
                intent.putStringArrayListExtra("images", (ArrayList<String>) selectedImages);

                startActivity(intent);
            }
        });
    }

        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE_IMAGES && resultCode == RESULT_OK && data != null) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        selectedImages.add(imageUri.toString());
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    selectedImages.add(imageUri.toString());
                }
            }
        }
    }