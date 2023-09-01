package com.moutamid.simpleolx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

public class SplashScreenActivity extends Activity {

    private ProgressBar loadingProgressBar;
    private int progressStatus = 0;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        handler = new Handler();

        final int totalProgress = 100;

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < totalProgress) {
                    progressStatus++;
                    handler.post(new Runnable() {
                        public void run() {
                            loadingProgressBar.setProgress(progressStatus);
                        }
                    });
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (isAdmin) {
                    startActivity(new Intent(SplashScreenActivity.this, AdminActivity.class));
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, SellerHomeActivity.class));
                }

                finish();
            }
        }).start();
    }
}
