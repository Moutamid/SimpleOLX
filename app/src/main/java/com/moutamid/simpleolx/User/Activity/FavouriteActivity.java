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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.stash.Stash;
import com.moutamid.simpleolx.AdModel;
import com.moutamid.simpleolx.R;
import com.moutamid.simpleolx.User.Adapter.AllItemsAdapter;
import com.moutamid.simpleolx.helper.Config;

import java.util.ArrayList;

public class FavouriteActivity extends AppCompatActivity {


    private RecyclerView adListView;

    private AllItemsAdapter adListAdapter;
    EditText search;
    ImageView mic;
    String lcode = "en-US";
    ArrayList<AdModel> adModelArrayList;
    TextView not_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        not_add = findViewById(R.id.not_add);
        adListView = findViewById(R.id.ad_listview);
        adListView.setLayoutManager(new GridLayoutManager(this, 1));
        adModelArrayList = Stash.getArrayList(Config.favourite, AdModel.class);
        adListAdapter = new AllItemsAdapter(this, adModelArrayList);
        if(adModelArrayList.size()>0)
        {
            not_add.setVisibility(View.GONE);
            adListView.setVisibility(View.VISIBLE);
        }
        else
        {

            not_add.setVisibility(View.VISIBLE);
            adListView.setVisibility(View.GONE);
        }
        adListView.setAdapter(adListAdapter);
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
            } else if (item.getHost().toLowerCase().contains(text.toLowerCase())) {
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
    public void backPress(View view) {
        onBackPressed();
    }
}
