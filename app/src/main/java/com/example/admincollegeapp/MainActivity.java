package com.example.admincollegeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.admincollegeapp.faculty.FacultyActivity;
import com.example.admincollegeapp.notice.DeleteNoticeActivity;
import com.example.admincollegeapp.notice.UploadNoticeActivity;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    MaterialCardView uploadNotice, addGalleryImage, uploadEbooks, addFaculty, deleteNotice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadNotice = findViewById(R.id.addNotice);
        uploadNotice.setOnClickListener(this);

        addGalleryImage = findViewById(R.id.addGalleryImage);
        addGalleryImage.setOnClickListener(this);

        uploadEbooks = findViewById(R.id.addEbook);
        uploadEbooks.setOnClickListener(this);

        addFaculty = findViewById(R.id.addFaculty);
        addFaculty.setOnClickListener(this);

        deleteNotice = findViewById(R.id.deleteNotice);
        deleteNotice.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addNotice) {
            startActivity(new Intent(MainActivity.this, UploadNoticeActivity.class));
        }
        if (view.getId() == R.id.addEbook) {
            startActivity(new Intent(MainActivity.this, UploadPdfActivity.class));
        }
        if (view.getId() == R.id.addFaculty) {
            startActivity(new Intent(MainActivity.this, FacultyActivity.class));
        }
        if (view.getId() == R.id.addGalleryImage) {
            startActivity(new Intent(MainActivity.this, UploadImageActivity.class));
        }
        if (view.getId() == R.id.deleteNotice) {
            startActivity(new Intent(MainActivity.this, DeleteNoticeActivity.class));
        }
    }

}
