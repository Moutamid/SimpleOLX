package com.moutamid.simpleolx.Admin.Adapter;


import android.app.AlertDialog;
import android.content.Context;
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

import com.moutamid.simpleolx.FeedBack;
import com.moutamid.simpleolx.R;
import com.moutamid.simpleolx.helper.Config;
import com.moutamid.simpleolx.helper.Constants;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserReportAdapter extends RecyclerView.Adapter<UserReportAdapter.GalleryPhotosViewHolder> {


    Context ctx;
    List<FeedBack> categoryModelList;

    public UserReportAdapter(Context ctx, List<FeedBack> categoryModelList) {
        this.ctx = ctx;
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public GalleryPhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.all_user, parent, false);
        return new GalleryPhotosViewHolder(view);
    }
    public void filterList(ArrayList<FeedBack> filterlist) {

        categoryModelList = filterlist;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull GalleryPhotosViewHolder holder, final int position) {
        FeedBack FeedBackModel = categoryModelList.get(position);
         holder.email.setText(FeedBackModel.getEmail());
         holder.issue.setText(FeedBackModel.getMessage());
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class GalleryPhotosViewHolder extends RecyclerView.ViewHolder {

         TextView issue, email;

        public GalleryPhotosViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.email);
            issue = itemView.findViewById(R.id.issue);

        }
    }
}
