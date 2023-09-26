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

import com.bumptech.glide.Glide;
import com.moutamid.simpleolx.CategoryName;
import com.moutamid.simpleolx.R;
import com.moutamid.simpleolx.User.Activity.AllItemsActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllCategroyAdapter extends RecyclerView.Adapter<AllCategroyAdapter.GalleryPhotosViewHolder> {


    Context ctx;
    List<CategoryName> categoryModelList;

    public AllCategroyAdapter(Context ctx, List<CategoryName> categoryModelList) {
        this.ctx = ctx;
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public GalleryPhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.all_categories, parent, false);
        return new GalleryPhotosViewHolder(view);
    }

    public void filterList(ArrayList<CategoryName> filterlist) {
        categoryModelList = filterlist;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryPhotosViewHolder holder, final int position) {
        CategoryName categoryNameModel = categoryModelList.get(position);
        Glide.with(ctx).load(categoryNameModel.thumbnail).into(holder.image);
        holder.category_name.setText(categoryNameModel.url);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(ctx, AllItemsActivity.class);
            intent.putExtra("category", categoryNameModel.url);
            ctx.startActivity(intent);
        });
        holder.remove.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class GalleryPhotosViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        ImageView remove;
        TextView category_name;

        public GalleryPhotosViewHolder(@NonNull View itemView) {
            super(itemView);
            remove = itemView.findViewById(R.id.remove);
            image = itemView.findViewById(R.id.ad_image);
            category_name = itemView.findViewById(R.id.ad_title);

        }
    }
}
