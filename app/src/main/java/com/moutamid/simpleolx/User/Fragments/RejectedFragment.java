package com.moutamid.simpleolx.User.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.icu.lang.UCharacter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.simpleolx.AdModel;
import com.moutamid.simpleolx.Admin.Adapter.AdListAdapter;
import com.moutamid.simpleolx.Constants;
import com.moutamid.simpleolx.R;
import com.moutamid.simpleolx.User.Activity.AdDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RejectedFragment extends Fragment {
    private ListView listView;
    private com.moutamid.simpleolx.User.Adapter.AdListAdapter adListAdapter;
    private DatabaseReference adsRef;
    TextView not_add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_activity, container, false);
        listView = view.findViewById(R.id.listViewAds);
        not_add = view.findViewById(R.id.not_add);
        adsRef = Constants.databaseReference().child("Ads");
        adListAdapter = new com.moutamid.simpleolx.User.Adapter.AdListAdapter(getContext());
        listView.setAdapter(adListAdapter);
        Query query = adsRef.orderByChild("approved").equalTo("rejected");
        Dialog lodingbar = new Dialog(getContext());
        lodingbar.setContentView(R.layout.loading);
        Objects.requireNonNull(lodingbar.getWindow()).setBackgroundDrawable(new ColorDrawable(UCharacter.JoiningType.TRANSPARENT));
        lodingbar.setCancelable(false);
        lodingbar.show();


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<AdModel> adList = new ArrayList<>();
                for (DataSnapshot adSnapshot : snapshot.getChildren()) {
                    if (adSnapshot.exists()) {
                        AdModel adModel = adSnapshot.getValue(AdModel.class);
                        Log.d("data","id"+ adModel.getSellerUid() + "  "+FirebaseAuth.getInstance().getCurrentUser().getUid());
                        if (adModel.getSellerUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            adList.add(adModel);
                        }
                    }
                }
                if (adList.size() > 0) {

                    not_add.setVisibility(View.GONE);
                } else {
                    not_add.setVisibility(View.VISIBLE);

                }
                adListAdapter.clear();
                adListAdapter.addAll(adList);
                lodingbar.dismiss();
                adListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                lodingbar.dismiss();
            }
        });


        return view;
    }
}