package com.example.admincollegeapp.notice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.admincollegeapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadNoticeActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private ImageView noticeImageView;
    private EditText noticeTitle;
    private MaterialButton uploadNoticeBTN;
    private final int REQ = 1;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String downloadUrl = "";
    private MaterialCardView addImageCardView;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Notice");
        storageReference = FirebaseStorage.getInstance().getReference().child("Notice");

        progressDialog = new ProgressDialog(this);

        addImageCardView = findViewById(R.id.addImage);
        noticeImageView = findViewById(R.id.noticeImageView);
        noticeTitle = findViewById(R.id.noticeTitleTextView);
        uploadNoticeBTN = findViewById(R.id.uploadNoticeButton);

        uploadNoticeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noticeTitle.getText().toString().isEmpty()) {
                    noticeTitle.setError("Required Field");
                    noticeTitle.requestFocus();
                } else if (bitmap == null) {
                    uploadData();
                } else {
                    uploadImage();
                }
            }
        });

        addImageCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void uploadImage() {
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();
        final StorageReference filePath = storageReference.child(System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = filePath.putBytes(data);
        uploadTask.addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString();
                            uploadData();
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(UploadNoticeActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadData() {
        String title = noticeTitle.getText().toString();

        // Get Date
        String date = new SimpleDateFormat("dd-MM-yy", java.util.Locale.getDefault()).format(Calendar.getInstance().getTime());

        // Get Time
        String time = new SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(Calendar.getInstance().getTime());

        String uniqueKey = databaseReference.push().getKey();
        if (uniqueKey != null) {
            NoticeData noticeData = new NoticeData(title, downloadUrl, date, time, uniqueKey);
            databaseReference.child(uniqueKey).setValue(noticeData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(UploadNoticeActivity.this, "Notice Uploaded", Toast.LENGTH_SHORT).show();
                            clearComponents(); // Clear components after successful upload
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UploadNoticeActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearComponents() {
        // Clear the EditText
        noticeTitle.setText("");

        // Clear the ImageView
        noticeImageView.setImageDrawable(null);

        // Reset the bitmap
        bitmap = null;
    }

    void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                noticeImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
