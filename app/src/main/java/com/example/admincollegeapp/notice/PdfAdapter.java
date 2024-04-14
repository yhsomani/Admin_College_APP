package com.example.admincollegeapp.notice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admincollegeapp.R;

import java.util.ArrayList;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.PdfDataViewHolder> {
    private Context context;
    private ArrayList<PdfData> pdfDataList;

    public PdfAdapter(Context context, ArrayList<PdfData> pdfDataList) {
        this.context = context;
        this.pdfDataList = pdfDataList;
    }

    @NonNull
    @Override
    public PdfDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_item_delete_layout, parent, false);
        return new PdfDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfDataViewHolder holder, int position) {
        PdfData currentItem = pdfDataList.get(position);

        holder.pdfTitleTextView.setText(currentItem.getPdfTitle());
    }

    @Override
    public int getItemCount() {
        return pdfDataList.size();
    }

    public static class PdfDataViewHolder extends RecyclerView.ViewHolder {
        private TextView pdfTitleTextView;

        public PdfDataViewHolder(@NonNull View itemView) {
            super(itemView);
            pdfTitleTextView = itemView.findViewById(R.id.pdfTitleTextView);
        }
    }
}
