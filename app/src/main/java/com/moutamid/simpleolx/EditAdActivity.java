package com.moutamid.simpleolx;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.icu.lang.UCharacter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EditAdActivity extends AppCompatActivity {

    private EditText editTitle, editDescription, editContact, editHost, editCompany, editcategory, edit_from_date, edit_to_date, editTime;
    private Spinner spinnerCategory;
    private static final int REQUEST_CODE_IMAGES = 1;
    private ViewPager viewPager;
    private ImagePagerAdapter imagePagerAdapter;
    private List<String> selectedImages = new ArrayList<>();
    private List<String> new_images = new ArrayList<>();
    private List<String> previous_images = new ArrayList<>();
    private Button btnAddImages, btnPreview, btnSubmit;
    private ImageView uploadBtn;
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ad);
        viewPager = findViewById(R.id.viewPagerImages);
        uploadBtn = findViewById(R.id.uploadImageView);
        DatabaseReference categoriesRef = Constants.databaseReference().child("categories");
        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editContact = findViewById(R.id.editContact);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnAddImages = findViewById(R.id.btnAddImages);
        btnPreview = findViewById(R.id.btnPreview);
        btnSubmit = findViewById(R.id.btnSubmit);
        editHost = findViewById(R.id.editHost);
        editCompany = findViewById(R.id.editCompany);
        editcategory = findViewById(R.id.editcategory);
        edit_from_date = findViewById(R.id.edit_from_date);
        edit_to_date = findViewById(R.id.edit_to_date);
        editTime = findViewById(R.id.editTime);
        AdModel model = (AdModel) Stash.getObject("Model", AdModel.class);
        new_images = getIntent().getStringArrayListExtra("images");
        selectedImages = getIntent().getStringArrayListExtra("images");
        imagePagerAdapter = new ImagePagerAdapter(this, new_images);
        viewPager.setAdapter(imagePagerAdapter);
        editTitle.setText(model.title);
        editDescription.setText(model.description);
        editContact.setText(model.contact);
        editHost.setText(model.getHost());
        editCompany.setText(model.getCompany());
        editcategory.setText(model.getNew_category());
        edit_from_date.setText(model.getFrom_date());
        edit_to_date.setText(model.getTo_date());
        editTime.setText(model.getTime());

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        categoryAdapter.add("Select Category");
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "MM/dd/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            edit_from_date.setText(sdf.format(myCalendar.getTime()));
        };
        DatePickerDialog.OnDateSetListener date1 = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "MM/dd/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            edit_to_date.setText(sdf.format(myCalendar.getTime()));
        };
        edit_from_date.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditAdActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        edit_to_date.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditAdActivity.this, date1, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditAdActivity.this, "pre" + previous_images.size(), Toast.LENGTH_SHORT).show();

                String title = editTitle.getText().toString().trim();
                String description = editDescription.getText().toString().trim();
                String contact = editContact.getText().toString().trim();
                String category = spinnerCategory.getSelectedItem().toString();
                String host = editHost.getText().toString().trim();
                String comapny = editCompany.getText().toString().trim();
                String category_new = editcategory.getText().toString().trim();
                String from_date = edit_from_date.getText().toString().trim();
                String to_date = edit_to_date.getText().toString().trim();
                String time = editTime.getText().toString().trim();
                String currentUserUid = Constants.auth().getCurrentUser().getUid();

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(contact) || TextUtils.isEmpty(host) || TextUtils.isEmpty(comapny) || TextUtils.isEmpty(category_new) || TextUtils.isEmpty(from_date) || TextUtils.isEmpty(to_date) || TextUtils.isEmpty(time)) {
                    Toast.makeText(EditAdActivity.this, "Fill All The Fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Dialog lodingbar = new Dialog(EditAdActivity.this);
                lodingbar.setContentView(R.layout.loading);
                Objects.requireNonNull(lodingbar.getWindow()).setBackgroundDrawable(new ColorDrawable(UCharacter.JoiningType.TRANSPARENT));
                lodingbar.setCancelable(false);
                lodingbar.show();

                DatabaseReference adsRef = Constants.databaseReference().child("Ads");
                DatabaseReference newAdRef = adsRef.child(model.adId);
                if (previous_images.size() != 0) {
                    List<String> updatedImageUrls = new ArrayList<>();
                    int totalImages = previous_images.size();
                    final int[] uploadedImages = {0};

                    for (String imageUrl : previous_images) {
                        Uri imageUri = Uri.parse(imageUrl);
                        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("images/" + imageUri.getLastPathSegment());
                        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                updatedImageUrls.add(uri.toString());
                                uploadedImages[0]++;
                                if (uploadedImages[0] == totalImages) {
                                    for (String imageUrl1 : new_images) {
                                        updatedImageUrls.add(imageUrl1);
                                    }
                                    AdModel newAd = new AdModel(model.adId, title, model.category, description, contact, currentUserUid, updatedImageUrls, false, host, comapny, category_new, from_date, to_date, time, model.city, model.province);
                                    newAd.setSellerUid(currentUserUid);
                                    newAdRef.setValue(newAd);
                                    lodingbar.dismiss();
                                    finish();
                                    Toast.makeText(EditAdActivity.this, "Ad submitted for approval", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    }
                } else {
                    Toast.makeText(EditAdActivity.this, "abc" + previous_images.size(), Toast.LENGTH_SHORT).show();

                    AdModel newAd = new AdModel(model.adId, title, model.category, description, contact, currentUserUid, new_images, false, host, comapny, category_new, from_date, to_date, time, model.city, model.province);
                    newAd.setSellerUid(currentUserUid);
                    newAdRef.setValue(newAd);
                    lodingbar.dismiss();
                    finish();
                    Toast.makeText(EditAdActivity.this, "Ad submitted for approval", Toast.LENGTH_SHORT).show();

                }
            }
        });

        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> categories = new ArrayList<>();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    CategoryName categoryName = categorySnapshot.getValue(CategoryName.class);
                    categories.add(categoryName.url);
                }
                categoryAdapter.addAll(categories);
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditAdActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
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

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, REQUEST_CODE_IMAGES);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Not needed for this purpose
            }

            @Override
            public void onPageSelected(int position) {
                // Update the image number text when the page changes
                int imageNumber = position + 1;
//                imageNumberTextView.setText(imageNumber + " of " + imageUrls.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Not needed for this purpose
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGES && resultCode == RESULT_OK && data != null) {
            viewPager.setVisibility(View.VISIBLE);
            uploadBtn.setVisibility(View.GONE);
            btnAddImages.setVisibility(View.VISIBLE);
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();

                    previous_images.add(imageUri.toString());
                    selectedImages.add(imageUri.toString());
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                previous_images.add(imageUri.toString());
                selectedImages.add(imageUri.toString());
            }
            imagePagerAdapter.notifyDataSetChanged();
        }
    }

    public void backPress(View view) {
        onBackPressed();
    }
}