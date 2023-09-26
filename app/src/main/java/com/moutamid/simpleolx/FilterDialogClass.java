package com.moutamid.simpleolx;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fxn.stash.Stash;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.simpleolx.User.Activity.AllItemsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilterDialogClass extends Dialog {
    private Spinner spinnerCategory;
    Spinner provincespinner, cityspinner;
    String province_str, city_str;
    TextView buttonSubmit, buttonCancel;
    CheckBox category_check;
    RadioButton city_btn, province_btn;
    String[] city = {"Toronto", "Montréal",
            "Vancouver", " Ottawa", " Edmonton", "Calgary", "Quebéc ", "Winnipeg ", "Hamilton", "London ",
            "Kitchener ", " St Catharines-Niagara", "Windsor\tOntario ", "Halifax ", "Victoria ", " Oshawa",
            "Saskatoon ", "Regina ", "St John's", "Sudbury", "Chicoutimi", "Sydney ", "Sherbrooke", "Kingston ",
            " Trois-Rivières", "Kelowna ", "Abbotsford ", "Saint John", "Thunder Bay", "Barrie"};

    String[] province = {"Quebec", "British Columbia", "Ontario", "Alberta", "Manitoba", "Nova Scotia", "Saskatchewan", "Newfoundland", "New Brunswick"};


    public Activity c;
    public Dialog d;


    public FilterDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.filter_lauout);
        provincespinner = findViewById(R.id.provincespinner);
        buttonCancel = findViewById(R.id.buttonCancel);
        cityspinner = findViewById(R.id.cityspinner);
        city_btn = findViewById(R.id.category_btn);
        province_btn = findViewById(R.id.province_btn);
        DatabaseReference categoriesRef = Constants.databaseReference().child("categories");
        spinnerCategory = findViewById(R.id.spinnerCategory);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        category_check = findViewById(R.id.category_check);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(c, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        ArrayAdapter ad = new ArrayAdapter(c, android.R.layout.simple_spinner_item, province);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provincespinner.setAdapter(ad);
        ArrayAdapter ad1 = new ArrayAdapter(c, android.R.layout.simple_spinner_item, city);
        ad1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityspinner.setAdapter(ad1);
        province_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    provincespinner.setVisibility(View.VISIBLE);
                    cityspinner.setVisibility(View.INVISIBLE);
                }
            }
        });
        city_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    cityspinner.setVisibility(View.VISIBLE);
                    provincespinner.setVisibility(View.INVISIBLE);
                }
            }
        });


        if (city_btn.isChecked()) {
            cityspinner.setVisibility(View.VISIBLE);
            provincespinner.setVisibility(View.INVISIBLE);
        }
        if (province_btn.isChecked()) {

            provincespinner.setVisibility(View.VISIBLE);
            cityspinner.setVisibility(View.INVISIBLE);
        }
        category_check.setChecked(Stash.getBoolean("category_check", false));
        province_btn.setChecked(Stash.getBoolean("province_check", false));
        city_btn.setChecked(Stash.getBoolean("city_check", true));


        spinnerCategory.setSelection(Stash.getInt("category_id"));
        cityspinner.setSelection(Stash.getInt("city_id"));
        provincespinner.setSelection(Stash.getInt("province_id"));

        cityspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Stash.put("city_id", i);
                city_str = city[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });
        provincespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Stash.put("province_id", i);
                province_str = province[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Stash.put("category_id", i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "5");
                dismiss();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Stash.put("category_check", category_check.isChecked());
                Stash.put("city_check", city_btn.isChecked());
                Stash.put("province_check", province_btn.isChecked());
                if (city_btn.isChecked()) {
                    if (category_check.isChecked()) {
                        AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "1");
                    } else {
                        AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "1");
                    }
                }
                if (province_btn.isChecked()) {
                    if (category_check.isChecked()) {
                        AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "3");
                    } else {
                        AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "4");
                    }
                }
                dismiss();
            }
        });
//                if (category_check.isChecked() && !city_check.isChecked() && !province_check.isChecked()) {
//
//                } else if (!category_check.isChecked() && city_check.isChecked() && !province_check.isChecked()) {
//                    AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "2");
//                } else if (!category_check.isChecked() && !city_check.isChecked() && province_check.isChecked()) {
//                    AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "3");
//                } else if (category_check.isChecked() && city_check.isChecked() && !province_check.isChecked()) {
//                    AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "4");
//                } else if (!category_check.isChecked() && city_check.isChecked() && province_check.isChecked()) {
//                    AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "5");
//                } else if (category_check.isChecked() && city_check.isChecked() && province_check.isChecked()) {
//                    AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "6");
//                } else if (category_check.isChecked() && !city_check.isChecked() && province_check.isChecked()) {
//                    AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "7");
//                } else if (!category_check.isChecked() && !city_check.isChecked() && !province_check.isChecked()) {
//                    AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "8");
//                }
//
////                Toast.makeText(c, "test " + city_str + "  " + province_str + "  " + spinnerCategory.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
//                dismiss();
//            }
//        });

    }

    private void addDataToFirestore(String message, String email) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("email", email);


        Constants.databaseReference().child("Support").push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(c, "Feedback is successfully submitted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(c, "Something went wrong" + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}