package com.example.admincollegeapp.notice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {
    private Context context;
    private ArrayList<NoticeData> noticeDataList;
    private NoticeClickListener noticeClickListener;

    public NoticeAdapter(Context context, ArrayList<NoticeData> noticeDataList, NoticeClickListener noticeClickListener) {
        this.context = context;
        this.noticeDataList = noticeDataList;
        this.noticeClickListener = noticeClickListener;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notice_item_delete_layout, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        NoticeData currentItem = noticeDataList.get(position);

        holder.noticeTitle.setText(currentItem.getTitle());

        // Load image using Picasso if URL is not empty
        if (currentItem.getImage() != null && !currentItem.getImage().isEmpty()) {
            Picasso.get().load(currentItem.getImage()).into(holder.noticeImage);
        } else {
            // Handle empty or null image URL
            // For example, you can set a placeholder image or hide the ImageView
            holder.noticeImage.setImageResource(R.drawable.download);
        }

        holder.deleteNoticeButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to delete this notice?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (noticeClickListener != null) {
                        noticeClickListener.onDeleteClick(position);
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return noticeDataList.size();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        private final MaterialButton deleteNoticeButton;
        private final TextView noticeTitle;
        private final ImageView noticeImage;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteNoticeButton = itemView.findViewById(R.id.deleteNoticeButton);
            noticeTitle = itemView.findViewById(R.id.deleteNoticeTitle);
            noticeImage = itemView.findViewById(R.id.deleteNoticeImage);
        }
    }

    public interface NoticeClickListener {
        void onDeleteClick(int position);
    }
}
