package com.moutamid.simpleolx.Admin.Adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fxn.stash.Stash;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.simpleolx.AdModel;
import com.moutamid.simpleolx.Constants;
import com.moutamid.simpleolx.EditAdActivity;
import com.moutamid.simpleolx.R;
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
        Log.d("AdListAdapter", "getView called for position: " + position);
        AdModel adModel = getItem(position);
        String adId = adModel.getAdId();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ad_list_adapter, parent, false);
            if (adModel == null) {
            }
            convertView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(adModel);
                }
            });
        }

        Button editBtn = convertView.findViewById(R.id.editBtn);
        if (currentUserUid != null && adModel != null && adModel.getSellerUid() != null && currentUserUid.equals(adModel.getSellerUid())) {
            editBtn.setVisibility(View.VISIBLE);
        } else {
            editBtn.setVisibility(View.GONE);
        }

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(getContext(), EditAdActivity.class);
                Stash.put("EditAd", adModel);
                editIntent.putExtra("title", adModel.getTitle());
                editIntent.putExtra("category", adModel.getCategory());
                editIntent.putExtra("description", adModel.getDescription());
                editIntent.putExtra("contact", adModel.getContact());
                editIntent.putExtra("host", adModel.getHost());
                editIntent.putExtra("company", adModel.getCompany());
                editIntent.putExtra("category", adModel.getNew_category());
                editIntent.putExtra("from_date", adModel.getFrom_date());
                editIntent.putExtra("to_date", adModel.getTo_date());
                editIntent.putExtra("time", adModel.getTime());
                editIntent.putStringArrayListExtra("images", (ArrayList<String>) adModel.getImages());
                getContext().startActivity(editIntent);
            }
        });

        Button approveBtn = convertView.findViewById(R.id.approveBtn);
        Button disapproveBtn = convertView.findViewById(R.id.disapproveBtn);

        DatabaseReference adminRef = Constants.databaseReference().child("Admins");
        FirebaseUser currentUser = Constants.auth().getCurrentUser();
        if (currentUser != null) {
            String currentUserEmail = Constants.auth().getCurrentUser().getEmail();
            adminRef.orderByChild("email").equalTo(currentUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isAdmin = dataSnapshot.exists();

                    if (isAdmin) {
                        approveBtn.setVisibility(View.VISIBLE);
                        disapproveBtn.setVisibility(View.VISIBLE);
                    } else {
                        approveBtn.setVisibility(View.GONE);
                        disapproveBtn.setVisibility(View.GONE);
                    }
                }

                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error if needed
                }
            });
        }

        DatabaseReference adsRef = Constants.databaseReference().child("Ads");
        adsRef.child(adId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean isApproved = dataSnapshot.child("approved").getValue(Boolean.class);

                    if (isApproved) {
                        approveBtn.setVisibility(View.GONE);
                    } else {
                        approveBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if needed
            }
        });

        approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adsRef.child(adId).child("approved").setValue(true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Ad approved!", Toast.LENGTH_SHORT).show();
                                approveBtn.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error approving ad: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        disapproveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adsRef.child(adId).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Ad Disapproved and removed!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error Disapproving ad: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

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
