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

public class AdListAdapter extends ArrayAdapter<AdModel> {
    private OnItemClickListener clickListener;
    private String currentUserUid;

    public AdListAdapter(Context context, String currentUserUid)
    {
        super(context,0, new ArrayList<>());
        this.currentUserUid = currentUserUid;
    }

    public interface OnItemClickListener {
        void onItemClick(AdModel selectedAd);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public AdListAdapter(Context context) {
        super(context, 0, new ArrayList<>());
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AdModel adModel = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ad_list_adapter, parent, false);
            convertView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(adModel);
                }
            });
        }

        Button editBtn = convertView.findViewById(R.id.editBtn);

        if (currentUserUid != null && adModel != null && adModel.getSellerUid() != null && currentUserUid.equals(adModel.getSellerUid())) {
            editBtn.setVisibility(View.VISIBLE);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editIntent = new Intent(getContext(), EditAdActivity.class);
                    editIntent.putExtra("title", adModel.getTitle());
                    editIntent.putExtra("category", adModel.getCategory());
                    editIntent.putExtra("description", adModel.getDescription());
                    editIntent.putExtra("contact", adModel.getContact());
                    editIntent.putExtra("SellerUid", adModel.getSellerUid());
                    editIntent.putStringArrayListExtra("images", (ArrayList<String>) adModel.getImages());
                    editIntent.putExtra("approved", adModel.isApproved());
                    getContext().startActivity(editIntent);
                }
            });
        } else {
            editBtn.setVisibility(View.GONE);
        }

        ImageView adImage = convertView.findViewById(R.id.ad_image);
        TextView adTitle = convertView.findViewById(R.id.ad_title);
        TextView adCategory = convertView.findViewById(R.id.ad_category);
        Button callButton = convertView.findViewById(R.id.ad_call_button);
        Button messageButton = convertView.findViewById(R.id.ad_message_button);
        Button whatsappButton = convertView.findViewById(R.id.ad_whatsapp_button);

        adTitle.setText(adModel.getTitle());
        adCategory.setText(adModel.getCategory());

        Picasso.get().load(adModel.getImages().get(0)).into(adImage);

        callButton.setOnClickListener(v -> {
            String phoneNumber = adModel.getContact();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            getContext().startActivity(intent);
        });

        // Set onClickListener for message button
        messageButton.setOnClickListener(v -> {
            String phoneNumber = adModel.getContact();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
            getContext().startActivity(intent);
        });

        whatsappButton.setOnClickListener(v -> {
            String phoneNumber = adModel.getContact();
            String message = "Hello, I'm interested in your ad.";
            String url = "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + message;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            getContext().startActivity(intent);
        });

        return convertView;
    }
}
