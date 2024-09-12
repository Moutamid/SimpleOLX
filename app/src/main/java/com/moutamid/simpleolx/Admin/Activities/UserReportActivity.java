package com.moutamid.simpleolx.Admin.Activities;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.icu.lang.UCharacter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.simpleolx.Admin.Adapter.UserReportAdapter;
import com.moutamid.simpleolx.Constants;
import com.moutamid.simpleolx.FeedBack;
import com.moutamid.simpleolx.R;
import com.moutamid.simpleolx.helper.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserReportActivity extends AppCompatActivity {
    RecyclerView content_rcv;
    UserReportAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_report);
        content_rcv = findViewById(R.id.content_rcv);
        content_rcv.setLayoutManager(new LinearLayoutManager(this));
        if (Config.isNetworkAvailable(UserReportActivity.this)) {
            getVideos();
        } else {
            Toast.makeText(UserReportActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }

    }


    private void getVideos() {
        Dialog lodingbar = new Dialog(UserReportActivity.this);
        lodingbar.setContentView(R.layout.loading);
        Objects.requireNonNull(lodingbar.getWindow()).setBackgroundDrawable(new ColorDrawable(UCharacter.JoiningType.TRANSPARENT));
        lodingbar.setCancelable(false);
        lodingbar.show();
        Constants.databaseReference().child("Support").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<FeedBack> adList = new ArrayList<>();

                for (DataSnapshot adSnapshot : dataSnapshot.getChildren()) {
                    FeedBack adModel = adSnapshot.getValue(FeedBack.class);
                    adList.add(adModel);

                }
                videoAdapter = new UserReportAdapter(UserReportActivity.this, adList);
                content_rcv.setAdapter(videoAdapter);

                lodingbar.dismiss();
                videoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserReportActivity.this, "error"+ databaseError.toString(), Toast.LENGTH_SHORT).show();
                lodingbar.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Config.isNetworkAvailable(UserReportActivity.this)) {
            getVideos();
        } else {
            Toast.makeText(UserReportActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    public void back(View view) {
        onBackPressed();
    }
}
