package com.moutamid.simpleolx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.fxn.stash.Stash;
import com.moutamid.simpleolx.helper.Config;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdDetailActivity extends AppCompatActivity {
    private TextView adDetailTitle, adDetailCategory, adDetailDescription;
    private ViewPager viewPager;
    ArrayList<String> imageUrls;
    ImageView favourite_img, unfavourite_img;

    private Button btnCall, btnMessage, btnWhatsApp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_detail_activity);

//        imageNumberTextView = findViewById(R.id.image_number_textview);
        viewPager = findViewById(R.id.viewPager);
        adDetailTitle = findViewById(R.id.ad_detail_title);
        adDetailCategory = findViewById(R.id.ad_detail_category);
        adDetailDescription = findViewById(R.id.ad_detail_description);
        btnCall = findViewById(R.id.btn_call);
        btnMessage = findViewById(R.id.btn_message);
        btnWhatsApp = findViewById(R.id.btn_whatsapp);
        unfavourite_img = findViewById(R.id.unfavourite);
        favourite_img = findViewById(R.id.favourite);

        Intent intent = getIntent();
        AdModel model = (AdModel) Stash.getObject("Model", AdModel.class);

        imageUrls = getIntent().getStringArrayListExtra("images");

        adDetailTitle.setText(model.title);
        adDetailCategory.setText(model.category);
        adDetailDescription.setText(model.description);

//        if (!imageUrls.isEmpty()) {
//            imageNumberTextView.setText("1 of " + imageUrls.size());
//        } else {
//            imageNumberTextView.setText("0 of 0");
//        }

        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter();
        viewPager.setAdapter(imagePagerAdapter);
        ArrayList<AdModel> resturantModels = Stash.getArrayList(Config.favourite, AdModel.class);
        if (resturantModels != null) {
            for (int i = 0; i < resturantModels.size(); i++) {

                if (model.getAdId().equals(resturantModels.get(i).getAdId())) {
                    unfavourite_img.setVisibility(View.VISIBLE);

                } else {
                    favourite_img.setVisibility(View.VISIBLE);

                }

            }
        }
        favourite_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ArrayList<AdModel> resturantModelArrayList = Stash.getArrayList(Config.favourite, AdModel.class);
                resturantModelArrayList.add(model);
                Stash.put(Config.favourite, resturantModelArrayList);
                unfavourite_img.setVisibility(View.VISIBLE);
                favourite_img.setVisibility(View.GONE);
            }
        });
        unfavourite_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<AdModel> resturantModel = Stash.getArrayList(Config.favourite, AdModel.class);
                for (int i = 0; i < resturantModel.size(); i++) {
                    if (resturantModel.get(i).getAdId().equals(model.getAdId())) {
                        resturantModel.remove(i);
                    }
                }
                Stash.put(Config.favourite, resturantModel);
                unfavourite_img.setVisibility(View.GONE);
                favourite_img.setVisibility(View.VISIBLE);
            }
        });


        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + model.contact));
                startActivity(callIntent);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Not needed for this purpose
            }

            @Override
            public void onPageSelected(int position) {
                // Update the image number text when the page changes
                int imageNumber = position + 1;
//                imageNumberTextView.setText(imageNumber + " of " + imageUrls.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Not needed for this purpose
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageIntent = new Intent(Intent.ACTION_SENDTO);
                messageIntent.setData(Uri.parse("smsto:" + model.contact));
                startActivity(messageIntent);
            }
        });

        btnWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SENDTO);
                whatsappIntent.setData(Uri.parse("smsto:" + model.contact));
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