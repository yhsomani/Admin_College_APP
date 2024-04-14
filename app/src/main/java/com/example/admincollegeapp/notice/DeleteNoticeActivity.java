package com.example.admincollegeapp.notice;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admincollegeapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeleteNoticeActivity extends AppCompatActivity implements NoticeAdapter.NoticeClickListener {

    private RecyclerView deleteNoticeRecycler;
    private ProgressBar progressBar;
    private ArrayList<NoticeData> noticeDataList;
    private DatabaseReference noticeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_notice);
        deleteNoticeRecycler = findViewById(R.id.deleteNoticeRecycler);
        progressBar = findViewById(R.id.progressBar);

        noticeRef = FirebaseDatabase.getInstance().getReference("Notice");

        noticeDataList = new ArrayList<>();

        deleteNoticeRecycler.setLayoutManager(new LinearLayoutManager(this));
        deleteNoticeRecycler.setHasFixedSize(true);

        loadNoticeData();
    }

    private void loadNoticeData() {
        progressBar.setVisibility(View.VISIBLE); // Show the progress bar

        noticeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noticeDataList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NoticeData noticeData = snapshot.getValue(NoticeData.class);
                    noticeDataList.add(noticeData);
                }
                NoticeAdapter noticeAdapter = new NoticeAdapter(DeleteNoticeActivity.this, noticeDataList, DeleteNoticeActivity.this);
                deleteNoticeRecycler.setAdapter(noticeAdapter);

                progressBar.setVisibility(View.GONE); // Hide the progress bar when data is loaded
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                progressBar.setVisibility(View.GONE); // Hide the progress bar if loading is canceled due to an error
            }
        });
    }

    @Override
    public void onDeleteClick(int position) {
        // Delete the notice from the database based on its position in the RecyclerView
        // You need to implement the logic to delete the notice from the Firebase Realtime Database here
        // You can use noticeDataList.get(position) to get the notice data to be deleted
        DatabaseReference noticeToDeleteRef = noticeRef.child(noticeDataList.get(position).getKey());
        noticeToDeleteRef.removeValue();
    }
}
