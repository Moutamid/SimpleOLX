package com.moutamid.simpleolx;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdListAdapter extends ArrayAdapter<AdModel> {

    public AdListAdapter(Context context) {
        super(context, 0, new ArrayList<>());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ad_list_adapter, parent, false);
        }

        AdModel adModel = getItem(position);

        ImageView adImage = convertView.findViewById(R.id.ad_image);
        TextView adTitle = convertView.findViewById(R.id.ad_title);
        TextView adCategory = convertView.findViewById(R.id.ad_category);
        Button callButton = convertView.findViewById(R.id.ad_call_button);
        Button messageButton = convertView.findViewById(R.id.ad_message_button);
        Button whatsappButton = convertView.findViewById(R.id.ad_whatsapp_button);

        adTitle.setText(adModel.getTitle());
        adCategory.setText(adModel.getCategory());

        Picasso.get().load(adModel.getImages().get(0)).into(adImage);

        // Set onClickListener for call button
        callButton.setOnClickListener(v -> {
            String phoneNumber = adModel.getContact(); // Set the correct contact number
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            getContext().startActivity(intent);
        });

        // Set onClickListener for message button
        messageButton.setOnClickListener(v -> {
            String phoneNumber = adModel.getContact(); // Set the correct contact number
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
            getContext().startActivity(intent);
        });

        whatsappButton.setOnClickListener(v -> {
            String phoneNumber = adModel.getContact(); // Set the correct contact number
            String message = "Hello, I'm interested in your ad."; // Customize the message
            String url = "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + message;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            getContext().startActivity(intent);
        });

        return convertView;
    }
}
