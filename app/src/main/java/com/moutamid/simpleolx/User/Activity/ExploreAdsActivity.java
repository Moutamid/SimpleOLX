package com.moutamid.simpleolx.User.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.simpleolx.AdModel;
import com.moutamid.simpleolx.CategoryName;
import com.moutamid.simpleolx.LoginActivity;
import com.moutamid.simpleolx.R;
import com.moutamid.simpleolx.SellerHomeActivity;
import com.moutamid.simpleolx.User.Adapter.CategroyAdapter;
import com.moutamid.simpleolx.User.Adapter.ItemsAdapter;
import com.moutamid.simpleolx.helper.Config;
import com.moutamid.simpleolx.helper.Constants;

import java.util.ArrayList;
import java.util.List;

public class ExploreAdsActivity extends AppCompatActivity {
    EditText search;
    ImageView mic;
    String lcode = "en-US";
    private static final String TAG = "ExploreAdsActivity";
    //    private Spinner categorySpinner;
    private RecyclerView adListView;

    private ItemsAdapter adListAdapter;
    private DatabaseReference adsRef;
    RecyclerView content_rcv;
    public List<CategoryName> categoryNameList = new ArrayList<>();
    public List<AdModel> adModelArrayList = new ArrayList<>();
    CategroyAdapter categroyAdapter;
    TextView see_store, see_category;
    ImageView settings, favourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_ads);

        favourite = findViewById(R.id.favourite);
        see_category = findViewById(R.id.see_category);
        see_store = findViewById(R.id.see_store);
        settings = findViewById(R.id.settings);
        content_rcv = findViewById(R.id.categories);
        content_rcv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        categroyAdapter = new CategroyAdapter(this, categoryNameList);
        content_rcv.setAdapter(categroyAdapter);
        Config.checkApp(ExploreAdsActivity.this);
        search = findViewById(R.id.search);
        mic = findViewById(R.id.mic);
        Config.showProgressDialog(ExploreAdsActivity.this);
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

        if (Config.isNetworkAvailable(ExploreAdsActivity.this)) {
            fetchCategories();

        } else {
            Toast.makeText(ExploreAdsActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
        adListView = findViewById(R.id.ad_listview);
        adListView.setLayoutManager(new GridLayoutManager(this, 2));
        adListAdapter = new ItemsAdapter(this, adModelArrayList);
        adListView.setAdapter(adListAdapter);
        adsRef = Constants.databaseReference().child("Ads");
        see_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExploreAdsActivity.this, AllItemsActivity.class));
            }
        });
        see_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExploreAdsActivity.this, AllCategoryActivity.class));
            }
        });
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExploreAdsActivity.this, FavouriteActivity.class));
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (com.moutamid.simpleolx.Constants.auth().getCurrentUser() != null) {
                    startActivity(new Intent(ExploreAdsActivity.this, SellerHomeActivity.class));
                } else {
                    startActivity(new Intent(ExploreAdsActivity.this, LoginActivity.class));
                }
            }

        });
    }

    private void fetchCategories() {
        Stash.clear(Config.category);
        Constants.CategoryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    CategoryName value = categorySnapshot.getValue(CategoryName.class);
                    categoryNameList.add(value);

                }
                Stash.put(Config.category, categoryNameList);
                Config.dismissProgressDialog();
                categroyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
                Config.dismissProgressDialog();

                adListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void filter(String text) {
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
            }else if (item.getHost().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            adListView.setVisibility(View.GONE);
//            no_text.setVisibility(View.VISIBLE);
        } else {
//            no_text.setVisibility(View.GONE);
            adListView.setVisibility(View.VISIBLE);

            // at last we are passing that filtered
            // list to our adapter class.
            adListAdapter.filterList(filteredlist);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAllAds();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
