package com.moutamid.simpleolx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdDetailActivity extends AppCompatActivity {
    private TextView adDetailTitle, adDetailCategory, adDetailDescription;
    private ViewPager viewPager;
    ArrayList<String> imageUrls;

    private Button btnCall, btnMessage, btnWhatsApp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_detail_activity);

        viewPager = findViewById(R.id.viewPager);
        adDetailTitle = findViewById(R.id.ad_detail_title);
        adDetailCategory = findViewById(R.id.ad_detail_category);
        adDetailDescription = findViewById(R.id.ad_detail_description);
        btnCall = findViewById(R.id.btn_call);
        btnMessage = findViewById(R.id.btn_message);
        btnWhatsApp = findViewById(R.id.btn_whatsapp);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String category = intent.getStringExtra("category");
        String description = intent.getStringExtra("description");
        String contact = intent.getStringExtra("contact");
        imageUrls = getIntent().getStringArrayListExtra("images");

        adDetailTitle.setText(title);
        adDetailCategory.setText(category);
        adDetailDescription.setText(description);

        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter();
        viewPager.setAdapter(imagePagerAdapter);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + contact));
                startActivity(callIntent);
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageIntent = new Intent(Intent.ACTION_SENDTO);
                messageIntent.setData(Uri.parse("smsto:" + contact));
                startActivity(messageIntent);
            }
        });

        btnWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SENDTO);
                whatsappIntent.setData(Uri.parse("smsto:" + contact));
                whatsappIntent.setPackage("com.whatsapp");
                startActivity(whatsappIntent);
            }
        });
    }

    private class ImagePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(AdDetailActivity.this);
            Picasso.get().load(imageUrls.get(position)).into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}