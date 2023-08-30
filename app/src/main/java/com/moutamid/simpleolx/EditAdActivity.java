package com.moutamid.simpleolx;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EditAdActivity extends AppCompatActivity {

    private ViewPager existingImagesViewPager;
    private int currentPosition = 0;
    private ImagePagerAdapter existingImagesAdapter;
    private List<String> existingImageUrls = new ArrayList<>();
    private EditText editTitle, editDescription, editContact;
    private Spinner editCategorySpinner;
    private Button submitButton;
    private ArrayAdapter<String> categoryAdapter;

    private static final int REQUEST_CODE_IMAGES = 2;

    private AdModel adModel;

    private DatabaseReference adsRef;
    Button btnAddImages, btnDeleteImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ad);

        btnAddImages = findViewById(R.id.btnAddImages);
        btnDeleteImage = findViewById(R.id.btnDeleteImage);

        adsRef = FirebaseDatabase.getInstance().getReference().child("Ads");

        editTitle = findViewById(R.id.edit_title);
        editCategorySpinner = findViewById(R.id.edit_spinnerCategory);
        editDescription = findViewById(R.id.edit_description);
        editContact = findViewById(R.id.edit_contact);
        submitButton = findViewById(R.id.edit_submit_button);

        adModel = getIntent().getParcelableExtra("adModel");

        if (adModel == null) {
            finish();
            return;
        }

        existingImagesViewPager = findViewById(R.id.existingImagesViewPager);
        existingImageUrls = adModel.getImages();
        existingImagesAdapter = new ImagePagerAdapter(existingImageUrls);
        existingImagesViewPager.setAdapter(existingImagesAdapter);

        editTitle.setText(adModel.getTitle());
        editDescription.setText(adModel.getDescription());
        editContact.setText(adModel.getContact());

        // Fetch available categories and set up the spinner
        fetchCategories();

        btnAddImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
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
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                updateDeleteButtonVisibility();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAd();
            }
        });
    }

    private class ImagePagerAdapter extends PagerAdapter {
        private List<String> imageUrls;
        private LayoutInflater inflater;

        public ImagePagerAdapter(List<String> imageUrls) {
            this.imageUrls = imageUrls;
            inflater = LayoutInflater.from(EditAdActivity.this);
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View itemView = inflater.inflate(R.layout.activity_edit_ad, container, false);
            ImageView imageView = itemView.findViewById(R.id.existingImagesViewPager);
            String imageUrl = imageUrls.get(position);
            Picasso.get().load(imageUrl).into(imageView);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private void updateDeleteButtonVisibility() {
        if (existingImageUrls.isEmpty()) {
            btnDeleteImage.setVisibility(View.GONE);
        } else {
            btnDeleteImage.setVisibility(View.VISIBLE);
        }
    }

    private void deleteCurrentImage() {
        if (!existingImageUrls.isEmpty()) {
            existingImageUrls.remove(currentPosition);
            existingImagesAdapter.notifyDataSetChanged();
            updateDeleteButtonVisibility();

            if (currentPosition >= existingImageUrls.size()) {
                currentPosition = existingImageUrls.size() - 1;
            }
            existingImagesViewPager.setCurrentItem(currentPosition);
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

    private void updateAd() {
        String newTitle = editTitle.getText().toString().trim();
        String newCategory = editCategorySpinner.getSelectedItem().toString();
        String newDescription = editDescription.getText().toString().trim();
        String newContact = editContact.getText().toString().trim();

        if (TextUtils.isEmpty(newTitle) || TextUtils.isEmpty(newDescription) || TextUtils.isEmpty(newContact)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        AdModel updatedAd = new AdModel(adModel.getAdId(),newTitle, newCategory, newDescription, newContact, adModel.getSellerUid(), adModel.getImages(), false);
        updatedAd.setSellerUid(adModel.getSellerUid());

        // TODO: Update images if needed

        updateAdInDatabase(updatedAd);
    }

    private void updateAdInDatabase(AdModel updatedAd) {
        String adId = adModel.getAdId();

        if (adId != null) {
            adsRef.child(adId).setValue(updatedAd, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
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
