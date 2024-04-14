package com.example.admincollegeapp.notice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admincollegeapp.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryDataViewHolder> {
    private Context context;
    private ArrayList<GalleryData> galleryDataList;
    private GalleryItemClickListener galleryItemClickListener;

    public GalleryAdapter(Context context, ArrayList<GalleryData> galleryDataList, GalleryItemClickListener galleryItemClickListener) {
        this.context = context;
        this.galleryDataList = galleryDataList;
        this.galleryItemClickListener = galleryItemClickListener;
    }

    @NonNull
    @Override
    public GalleryDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallery_item_delete_layout, parent, false);
        return new GalleryDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryDataViewHolder holder, int position) {
        GalleryData currentItem = galleryDataList.get(position);

        holder.categoryTextView.setText(currentItem.getCategory());
        Picasso.get().load(currentItem.getImageUrl()).into(holder.galleryImageView);

        // Set click listener for delete button
        holder.deleteButton.setOnClickListener(v -> {
            if (galleryItemClickListener != null) {
                galleryItemClickListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryDataList.size();
    }

    public static class GalleryDataViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryTextView;
        private ImageView galleryImageView;
        private MaterialButton deleteButton;

        public GalleryDataViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            galleryImageView = itemView.findViewById(R.id.galleryImageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface GalleryItemClickListener {
        void onDeleteClick(int position);
    }
}
