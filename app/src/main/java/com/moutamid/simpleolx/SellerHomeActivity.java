package com.moutamid.simpleolx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SellerHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);
    }

    public void switchToMyAds(View view) {
        Intent intent = new Intent(this, MyAdsActivity.class);
        startActivity(intent);
    }
    public void switchToAddNew(View view) {
        Intent intent = new Intent(this, AddNewActivity.class);
        startActivity(intent);
    }
    public void switchToMyAccount(View view) {
        Intent intent = new Intent(this, MyAccountActivity.class);
        startActivity(intent);
    }
    public void switchToExploreAds(View view) {
        Intent intent = new Intent(this, ExploreAdsActivity.class);
        startActivity(intent);
    }
}