package com.moutamid.simpleolx;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
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

public class EditAdActivity extends AppCompatActivity {

    private ViewPager existingImagesViewPager;
    private int currentPosition = 0;
    private ImagePagerAdapter existingImagesAdapter;
    private List<String> existingImageUrls = new ArrayList<>();
    private EditText editTitle, editDescription, editContact;
    private int selectedImagePosition = -1;

    private Spinner categorySpinner;
    private Button submitButton, btnAddImages, btnDeleteImage;

    private static final int REQUEST_CODE_IMAGES = 2;

    private AdModel adModel;
    private DatabaseReference adsRef;

    private ArrayAdapter<String> categoryAdapter;
    private String selectedCategory;
    private List<String> categoriesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ad);

        initializeViews();
        setupListeners();
        initializeAdModel();

        categoriesList = new ArrayList<>();
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        existingImagesAdapter = new ImagePagerAdapter(this, existingImageUrls);
        existingImagesViewPager.setAdapter(existingImagesAdapter);
    }

    private void initializeViews() {
        existingImagesViewPager = findViewById(R.id.existingImagesViewPager);
        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.edit_description);
        editContact = findViewById(R.id.edit_contact);
        categorySpinner = findViewById(R.id.categorySpinner);
        btnAddImages = findViewById(R.id.btnAddImages);
        btnDeleteImage = findViewById(R.id.btnDeleteImage);
        submitButton = findViewById(R.id.edit_submit_button);
    }

    private void setupListeners() {
        btnAddImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        existingImagesViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageToDelete(currentPosition);
            }
        });

        btnDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCurrentImage();
            }
        });

        existingImagesViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                updateDeleteButtonVisibility();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Handle category selection
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedCategory = categoriesList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAd();
            }
        });
    }

    private void initializeAdModel() {
        adsRef = Constants.databaseReference().child("Ads");

        adModel = new AdModel();
        adModel.setTitle(getIntent().getStringExtra("title"));
        adModel.setCategory(getIntent().getStringExtra("category"));
        adModel.setDescription(getIntent().getStringExtra("description"));
        adModel.setContact(getIntent().getStringExtra("contact"));
        adModel.setImages(getIntent().getStringArrayListExtra("images"));


        categoriesList = new ArrayList<>();
        DatabaseReference categoriesRef = Constants.databaseReference().child("Categories");
        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing categories list
                categoriesList.clear();

                // Iterate through the dataSnapshot to get category names
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String categoryName = categorySnapshot.getValue(String.class);
                    if (categoryName != null) {
                        categoriesList.add(categoryName);
                    }
                }

                categoryAdapter.notifyDataSetChanged();
                setCategorySelection(adModel.getCategory());

                existingImageUrls.clear();
                existingImageUrls.addAll(adModel.getImages());
                existingImagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the database fetch
                Toast.makeText(EditAdActivity.this, "Failed to fetch categories: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        if (adModel == null) {
            Toast.makeText(this, "No Ads Found", Toast.LENGTH_LONG).show();
            return;
        }

        editTitle.setText(adModel.getTitle());
        editDescription.setText(adModel.getDescription());
        editContact.setText(adModel.getContact());
        setCategorySelection(adModel.getCategory());
    }

    private void setCategorySelection(String category) {
        if (!TextUtils.isEmpty(category)) {
            int position = categoriesList.indexOf(category);
            if (position != -1) {
                categorySpinner.setSelection(position);
            }
        }
    }

    private void selectImageToDelete(final int position) {
        if (!existingImageUrls.isEmpty() && position >= 0 && position < existingImageUrls.size()) {
            // Show a confirmation dialog before deleting the image
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User confirmed to delete the image
                            existingImageUrls.remove(position);
                            existingImagesAdapter.notifyDataSetChanged();

                            if (existingImageUrls.isEmpty()) {
                                currentPosition = 0; // Reset position if there are no images left
                            } else if (currentPosition >= existingImageUrls.size()) {
                                currentPosition = existingImageUrls.size() - 1;
                            }

                            existingImagesViewPager.setCurrentItem(currentPosition);
                            updateDeleteButtonVisibility();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the delete operation
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    private void updateDeleteButtonVisibility() {
        btnDeleteImage.setVisibility(existingImageUrls.isEmpty() ? View.GONE : View.VISIBLE);
        submitButton.setEnabled(!existingImageUrls.isEmpty() && !TextUtils.isEmpty(editTitle.getText())
                && !TextUtils.isEmpty(editDescription.getText()) && !TextUtils.isEmpty(editContact.getText()));
    }

    private void deleteCurrentImage() {
        if (!existingImageUrls.isEmpty() && currentPosition >= 0 && currentPosition < existingImageUrls.size()) {
            // Show a confirmation dialog before deleting the image
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User confirmed to delete the image
                            existingImageUrls.remove(currentPosition);
                            existingImagesAdapter.notifyDataSetChanged();

                            if (existingImageUrls.isEmpty()) {
                                currentPosition = 0; // Reset position if there are no images left
                            } else if (currentPosition >= existingImageUrls.size()) {
                                currentPosition = existingImageUrls.size() - 1;
                            }

                            existingImagesViewPager.setCurrentItem(currentPosition);
                            updateDeleteButtonVisibility();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the delete operation
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, REQUEST_CODE_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGES && resultCode == RESULT_OK && data != null) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    existingImageUrls.add(imageUri.toString());
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                existingImageUrls.add(imageUri.toString());
            }

            // Notify the adapter about the changes
            existingImagesAdapter.notifyDataSetChanged();
            updateDeleteButtonVisibility();
        }
    }

    private void updateAd() {
        String newTitle = editTitle.getText().toString().trim();
        String newDescription = editDescription.getText().toString().trim();
        String newContact = editContact.getText().toString().trim();
        String newCategory = categorySpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(newTitle) || TextUtils.isEmpty(newDescription) || TextUtils.isEmpty(newContact) || TextUtils.isEmpty(newCategory)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> updatedImageUrls = new ArrayList<>();

        // Upload images and update URLs
        for (String imageUrl : existingImageUrls) {
            Uri imageUri = Uri.parse(imageUrl);

            // Check if the image URL is valid and has a content scheme
            if (imageUri.getScheme() != null && imageUri.getScheme().startsWith("content")) {
                StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("images/" + imageUri.getLastPathSegment());

                // Upload the image and update the URLs
                imageRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    updatedImageUrls.add(uri.toString());

                                    // If all images have been uploaded, update the ad in the database
                                    if (updatedImageUrls.size() == existingImageUrls.size()) {
                                        AdModel updatedAd = new AdModel(
                                                adModel.getAdId(),
                                                newTitle,
                                                newCategory,
                                                newDescription,
                                                newContact,
                                                adModel.getSellerUid(),
                                                updatedImageUrls,
                                                false
                                        );

                                        updateAdInDatabase(updatedAd);
                                    }
                                }));
            } else {
                updatedImageUrls.add(imageUrl);
            }
        }
    }

    private void updateAdInDatabase(AdModel updatedAd) {
        String adId = adModel.getAdId();

        if (adId != null) {
            adsRef.child(adId).setValue(updatedAd, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@NonNull DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error == null) {
                        Toast.makeText(EditAdActivity.this, "Ad updated successfully will be live after approval", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditAdActivity.this, "Error updating ad", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
