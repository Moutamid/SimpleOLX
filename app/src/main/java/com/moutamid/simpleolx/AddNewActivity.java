package com.moutamid.simpleolx;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AddNewActivity extends AppCompatActivity {

    private EditText editTitle, editDescription, editContact;
    private Spinner spinnerCategory;
    private static final int REQUEST_CODE_IMAGES = 1;
    private ViewPager viewPager;
    private ImagePagerAdapter imagePagerAdapter;
    private List<String> selectedImages = new ArrayList<>();
    private Button btnAddImages, btnPreview, btnSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_activity);

        viewPager = findViewById(R.id.viewPagerImages);
        imagePagerAdapter = new ImagePagerAdapter(this, selectedImages);
        viewPager.setAdapter(imagePagerAdapter);

        DatabaseReference categoriesRef = Constants.databaseReference().child("Categories");

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editContact = findViewById(R.id.editContact);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnAddImages = findViewById(R.id.btnAddImages);
        btnPreview = findViewById(R.id.btnPreview);
        btnSubmit = findViewById(R.id.btnSubmit);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        categoryAdapter.add("Select Category");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString().trim();
                String description = editDescription.getText().toString().trim();
                String contact = editContact.getText().toString().trim();
                String category = spinnerCategory.getSelectedItem().toString();
                String currentUserUid = Constants.auth().getCurrentUser().getUid();

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(contact)) {
                    Toast.makeText(AddNewActivity.this, "Fill All The Fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedImages.isEmpty()) {
                    Toast.makeText(AddNewActivity.this, "Select at least one image", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference adsRef = Constants.databaseReference().child("Ads");
                DatabaseReference newAdRef = adsRef.push();
                String adKey = newAdRef.getKey();

                List<String> updatedImageUrls = new ArrayList<>();
                int totalImages = selectedImages.size();
                final int[] uploadedImages = {0};

                for (String imageUrl : selectedImages) {
                    Uri imageUri = Uri.parse(imageUrl);
                    StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("images/" + imageUri.getLastPathSegment());

                    imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            updatedImageUrls.add(uri.toString());
                            uploadedImages[0]++;

                            if (uploadedImages[0] == totalImages) {
                                AdModel newAd = new AdModel(adKey, title, category, description, contact, currentUserUid, updatedImageUrls, false);
                                newAd.setSellerUid(currentUserUid);

                                newAdRef.setValue(newAd);

                                editTitle.setText("");
                                editDescription.setText("");
                                editContact.setText("");
                                selectedImages.clear();

                                Toast.makeText(AddNewActivity.this, "Ad submitted for approval", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                }
            }
        });

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
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
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
                imagePagerAdapter.notifyDataSetChanged();
            }
        }
    }