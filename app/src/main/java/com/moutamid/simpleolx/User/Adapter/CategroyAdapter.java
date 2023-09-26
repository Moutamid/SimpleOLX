package com.moutamid.simpleolx.User.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moutamid.simpleolx.CategoryName;
import com.moutamid.simpleolx.R;
import com.moutamid.simpleolx.User.Activity.AllItemsActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategroyAdapter extends RecyclerView.Adapter<CategroyAdapter.GalleryPhotosViewHolder> {


    Context ctx;
    List<CategoryName> categoryModelList;

    public CategroyAdapter(Context ctx, List<CategoryName> categoryModelList) {
        this.ctx = ctx;
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public GalleryPhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.category_layout, parent, false);
        return new GalleryPhotosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryPhotosViewHolder holder, final int position) {
        CategoryName categoryNameModel = categoryModelList.get(position);
        Glide.with(ctx).load(categoryNameModel.thumbnail).into(holder.image);
        holder.category_name.setText(categoryNameModel.url);
        holder.image.setOnClickListener(view -> {
            Intent intent = new Intent(ctx, AllItemsActivity.class);
            intent.putExtra("category", categoryNameModel.url);
            ctx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class GalleryPhotosViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView category_name;

        public GalleryPhotosViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            category_name = itemView.findViewById(R.id.category_name);

        }
    }
}
