package com.moutamid.simpleolx;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moutamid.simpleolx.User.Activity.AdDetailActivity;
import com.moutamid.simpleolx.helper.Config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddNewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText editTitle, editDescription, editContact, editHost, editCompany, editcategory, edit_from_date, edit_to_date, editTime;
    private Spinner spinnerCategory;
    private static final int REQUEST_CODE_IMAGES = 1;
    private ViewPager viewPager;
    private ImagePagerAdapter imagePagerAdapter;
    private List<String> selectedImages = new ArrayList<>();
    private Button btnAddImages, btnPreview, btnSubmit;
    private ImageView uploadBtn;
    Calendar myCalendar = Calendar.getInstance();
    Spinner provincespinner, cityspinner;
    String province_str, city_str;
    String[] city = {"Toronto", "Montréal",
            "Vancouver", " Ottawa", " Edmonton", "Calgary", "Quebéc ", "Winnipeg ", "Hamilton", "London ",
            "Kitchener ", " St Catharines-Niagara", "Windsor\tOntario ", "Halifax ", "Victoria ", " Oshawa",
            "Saskatoon ", "Regina ", "St John's", "Sudbury", "Chicoutimi", "Sydney ", "Sherbrooke", "Kingston ",
            " Trois-Rivières", "Kelowna ", "Abbotsford ", "Saint John", "Thunder Bay", "Barrie"};

    String[] province = {"Quebec", "British Columbia", "Ontario", "Alberta", "Manitoba", "Nova Scotia", "Saskatchewan", "Newfoundland", "New Brunswick"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_activity);
        provincespinner = findViewById(R.id.provincespinner);
        cityspinner = findViewById(R.id.cityspinner);
        viewPager = findViewById(R.id.viewPagerImages);
        uploadBtn = findViewById(R.id.uploadImageView);
        imagePagerAdapter = new ImagePagerAdapter(this, selectedImages);
        viewPager.setAdapter(imagePagerAdapter);
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

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "MM/dd/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            edit_from_date.setText(sdf.format(myCalendar.getTime()));
        };

        provincespinner.setOnItemSelectedListener(this);

        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, province);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provincespinner.setAdapter(ad);
        ArrayAdapter ad1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, city);
        ad1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityspinner.setAdapter(ad1);
        cityspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                city_str = city[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });

        DatePickerDialog.OnDateSetListener date1 = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "MM/dd/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            edit_to_date.setText(sdf.format(myCalendar.getTime()));
        };
        edit_from_date.setOnClickListener(v ->

        {
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddNewActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        edit_to_date.setOnClickListener(v ->

        {
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddNewActivity.this, date1, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(contact) || TextUtils.isEmpty(host) || TextUtils.isEmpty(comapny) || TextUtils.isEmpty(category_new) || TextUtils.isEmpty(from_date) || TextUtils.isEmpty(to_date) || TextUtils.isEmpty(time) || TextUtils.isEmpty(province_str) || TextUtils.isEmpty(province_str)) {
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
                    Config.showProgressDialog(AddNewActivity.this);

                    Uri imageUri = Uri.parse(imageUrl);
                    StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("images/" + imageUri.getLastPathSegment());

                    imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            updatedImageUrls.add(uri.toString());
                            uploadedImages[0]++;

                            if (uploadedImages[0] == totalImages) {
                                AdModel newAd = new AdModel(adKey, title, category, description, contact, currentUserUid, updatedImageUrls, "pending", host, comapny, category_new, from_date, to_date, time, city_str, province_str);
                                newAd.setSellerUid(currentUserUid);

                                newAdRef.setValue(newAd);


                                Config.dismissProgressDialog();
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
                mTimePicker = new TimePickerDialog(AddNewActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                    selectedImages.add(imageUri.toString());
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                selectedImages.add(imageUri.toString());
            }
            imagePagerAdapter.notifyDataSetChanged();
        }
    }

    public void backPress(View view) {
        onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        province_str = province[i];

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}