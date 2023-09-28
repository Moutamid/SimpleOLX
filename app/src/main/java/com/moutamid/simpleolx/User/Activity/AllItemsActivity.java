package com.moutamid.simpleolx.User.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.simpleolx.AdModel;
import com.moutamid.simpleolx.FilterDialogClass;
import com.moutamid.simpleolx.R;
import com.moutamid.simpleolx.User.Adapter.AllItemsAdapter;
import com.moutamid.simpleolx.helper.Constants;

import java.util.ArrayList;
import java.util.List;

public class AllItemsActivity extends AppCompatActivity {


    public static RecyclerView adListView;

    public static AllItemsAdapter adListAdapter;
    private DatabaseReference adsRef;
    public static List<AdModel> adModelArrayList = new ArrayList<>();
    EditText search;
    ImageView mic;
    ImageView filter;
    String lcode = "en-US";
    Query adsRef1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);
        adListView = findViewById(R.id.ad_listview);
        filter = findViewById(R.id.filter);
        adListView.setLayoutManager(new GridLayoutManager(this, 1));
        adListAdapter = new AllItemsAdapter(this, adModelArrayList);
        adListView.setAdapter(adListAdapter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialogClass cdd = new FilterDialogClass(AllItemsActivity.this);
                cdd.show();
            }
        });
        String category = getIntent().getStringExtra("category");
        if (category != null) {
            adsRef = Constants.databaseReference().child("Ads");
            fetchAllAds(category);
        } else {
            adsRef = Constants.databaseReference().child("Ads");
            fetchAllAds();

        }
        search = findViewById(R.id.search);

        mic = findViewById(R.id.mic);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // if result is not empty
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            // get data and append it to editText
                            ArrayList<String> d = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                            search.setText(" " + d.get(0));
                        }
                    }
                });
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lcode);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!");
                activityResultLauncher.launch(intent);
            }
        });


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());


            }
        });


//        fetchAllAds();
    }


    private void fetchAllAds() {

        adsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adModelArrayList.clear();

                for (DataSnapshot adSnapshot : snapshot.getChildren()) {
                    AdModel adModel = adSnapshot.getValue(AdModel.class);
                    if (adModel.isApproved().equals("accepted")) {
                        adModelArrayList.add(adModel);
                    }
                }

                adListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void fetchAllAds(String category) {
        Query query = adsRef.orderByChild("category").equalTo(category);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adModelArrayList.clear();

                for (DataSnapshot adSnapshot : snapshot.getChildren()) {
                    AdModel adModel = adSnapshot.getValue(AdModel.class);
                    if (adModel.isApproved().equals("accepted")) {
                        adModelArrayList.add(adModel);
                    }
                }

                adListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<AdModel> filteredlist = new ArrayList<AdModel>();

        // running a for loop to compare elements.
        for (AdModel item : adModelArrayList) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            } else if (item.getCategory().toLowerCase().contains(text.toLowerCase())) {

                filteredlist.add(item);
            } else if (item.getCompany().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            } else if (item.getHost().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            adListView.setVisibility(View.GONE);
        } else {
            adListView.setVisibility(View.VISIBLE);
            adListAdapter.filterList(filteredlist);
        }
    }

    public static void filterAll(String category, String city, String province, String type) {
        // creating a new array list to filter our data.
        ArrayList<AdModel> filteredlist = new ArrayList<AdModel>();

        for (AdModel item : adModelArrayList) {
            Log.d("data", item.getCategory() + "  sdd   ");
            Log.d("data", item.getCity() + "  sdd   " + city);
            Log.d("data", item.getProvince() + "  sdd   " + province);


//            AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "1");
//        } else
//        if (!category_check.isChecked() && city_check.isChecked() && !province_check.isChecked()) {
//            AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "2");
//        } else if (!category_check.isChecked() && !city_check.isChecked() && province_check.isChecked()) {
//            AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "3");
//        } else if (category_check.isChecked() && city_check.isChecked() && !province_check.isChecked()) {
//            AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "4");
//        } else if (!category_check.isChecked() && city_check.isChecked() && province_check.isChecked()) {
//            AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "5");
//        } else if (category_check.isChecked() && city_check.isChecked() && province_check.isChecked()) {
//            AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "6");
//        } else if (category_check.isChecked() && !city_check.isChecked() && province_check.isChecked()) {
//            AllItemsActivity.filterAll(spinnerCategory.getSelectedItem().toString(), city_str, province_str, "7");

            switch (type) {
                case "1":
                    if (item.getCategory().equals(category)) {
                        if (item.getCity().equals(city)) {
                            filteredlist.add(item);
                        }
                    }
                    break;
                case "2":
                    if (item.getCity().equals(city)) {
                        filteredlist.add(item);
                    }
                    break;
                case "3":
                    if (item.getProvince().equals(province)) {
                        if (item.getCategory().equals(category)) {
                            filteredlist.add(item);
                        }

                    }
                    break;
                case "4":
                    if (item.getProvince().equals(province)) {
                        filteredlist.add(item);
                    }

                    break;
                case "5":
                        filteredlist.add(item);
                    break;
            }
        }
        if (filteredlist.isEmpty()) {
            adListView.setVisibility(View.GONE);
        } else {
            adListView.setVisibility(View.VISIBLE);
            adListAdapter.filterList(filteredlist);
        }
    }

    public void backPress(View view) {
        onBackPressed();
    }
}
