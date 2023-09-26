package com.moutamid.simpleolx.Admin.Adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.moutamid.simpleolx.CategoryName;
import com.moutamid.simpleolx.R;
import com.moutamid.simpleolx.User.Activity.AllItemsActivity;
import com.moutamid.simpleolx.helper.Config;
import com.moutamid.simpleolx.helper.Constants;

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
//        holder.itemView.setOnClickListener(view -> {
//            Intent intent = new Intent(ctx, AllItemsActivity.class);
//            intent.putExtra("category", categoryNameModel.url);
//            ctx.startActivity(intent);
//        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                dialog.setTitle("Delete Category");
                dialog.setMessage("Are you sure to delete this category");
                dialog.setPositiveButton("Yes", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    Config.showProgressDialog(ctx);
                    Constants.CategoryReference.child(categoryNameModel.key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Config.dismissProgressDialog();
                        if (task.isSuccessful()){
//                            categoryNameModel.remove(position);
                            notifyDataSetChanged();
                        }
                        }
                    }).addOnFailureListener(e -> {
                        Config.dismissProgressDialog();
                        Toast.makeText(ctx, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    });
                }).setNegativeButton("No", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
                dialog.show();

            }
        });
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
