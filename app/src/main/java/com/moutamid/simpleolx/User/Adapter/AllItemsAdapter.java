package com.moutamid.simpleolx.User.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.stash.Stash;
import com.moutamid.simpleolx.User.Activity.AdDetailActivity;
import com.moutamid.simpleolx.AdModel;
import com.moutamid.simpleolx.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AllItemsAdapter extends RecyclerView.Adapter<AllItemsAdapter.GalleryPhotosViewHolder> {


    Context ctx;
    List<AdModel> categoryModelList;

    public AllItemsAdapter(Context ctx, List<AdModel> categoryModelList) {
        this.ctx = ctx;
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public GalleryPhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.all_items_product, parent, false);
        return new GalleryPhotosViewHolder(view);
    }

    public void filterList(ArrayList<AdModel> filterlist) {

        categoryModelList = filterlist;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull GalleryPhotosViewHolder holder, final int position) {
        AdModel adModel = categoryModelList.get(position);
        Picasso.get().load(adModel.getImages().get(0)).into(holder.adImage);
        holder.adTitle.setText(adModel.getTitle());
        holder.ad_category.setText(adModel.getDescription());
        holder.ad_company.setText(adModel.getCompany());
        holder.ad_host.setText(adModel.getHost());
        holder.itemView.setOnClickListener(view -> {
            Intent editIntent = new Intent(ctx, AdDetailActivity.class);
            Stash.put("Model", adModel);
            editIntent.putStringArrayListExtra("images", (ArrayList<String>) adModel.getImages());
            ctx.startActivity(editIntent);

        });


    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class GalleryPhotosViewHolder extends RecyclerView.ViewHolder {

        ImageView adImage;
        TextView adTitle, ad_category, ad_host, ad_company;



        public GalleryPhotosViewHolder(@NonNull View convertView) {
            super(convertView);
            adImage = convertView.findViewById(R.id.ad_image);
            adTitle = convertView.findViewById(R.id.ad_title);
            ad_category = convertView.findViewById(R.id.ad_category);
            ad_host = convertView.findViewById(R.id.ad_host);
            ad_company = convertView.findViewById(R.id.ad_company);

        }
    }
}
