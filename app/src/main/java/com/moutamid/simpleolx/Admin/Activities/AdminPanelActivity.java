package com.moutamid.simpleolx.Admin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.moutamid.simpleolx.Constants;
import com.moutamid.simpleolx.HomeActivity;
import com.moutamid.simpleolx.R;

public class AdminPanelActivity extends AppCompatActivity {
    CardView categories_btn, request_btn, logout, users_report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        categories_btn = findViewById(R.id.categories_btn);
        request_btn = findViewById(R.id.request_btn);
        users_report = findViewById(R.id.users_report);
        logout = findViewById(R.id.logout);
        categories_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminPanelActivity.this, AllCategoryActivity.class));
            }
        });
        users_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminPanelActivity.this, UserReportActivity.class));
            }
        });
        request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminPanelActivity.this, AdRequestActivity.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.auth().signOut();
                Intent intent = new Intent(AdminPanelActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}