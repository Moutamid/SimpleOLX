package com.moutamid.simpleolx;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//if(Constants.auth().getCurrentUser()!=null)
//{
//    startActivity(new Intent(SplashActivity.this, ExploreAdsActivity.class));
//
//}
//else
//{
//    startActivity(new Intent(this, HomeActivity.class));
//}
        if (Stash.getBoolean(Constants.IS_LOGGED_IN, false)) {

            if (Stash.getBoolean(Constants.IS_ADMIN, false)){
                startActivity(new Intent(SplashActivity.this, AdminActivity.class));
            }else {
                startActivity(new Intent(SplashActivity.this, SellerHomeActivity.class));
            }

        } else startActivity(new Intent(this, HomeActivity.class));

    }
}