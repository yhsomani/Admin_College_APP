package com.example.admincollegeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class UploadImageActivity extends AppCompatActivity {

    Spinner imageCategory;
    Bitmap bitmap;
    private final int REQ = 1;
    MaterialCardView selectImage;
    MaterialButton uploadImageBtn;
    ImageView galleryImageView;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    String downloadUrl = "";
    String category;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        selectImage = findViewById(R.id.addGalleryImageView);
        imageCategory = findViewById(R.id.imageCategory);
        uploadImageBtn = findViewById(R.id.uploadImageButton);
        galleryImageView = findViewById(R.id.galleryImageView);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("gallery");
        storageReference = FirebaseStorage.getInstance().getReference().child("gallery");

        progressDialog = new ProgressDialog(this);

        String[] items = {"Select Category", "Academics", "Activities", "Administration", "Facilities", "Student Resources", "Health and Safety", "Community", "General", "Other Events"};
        imageCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
        imageCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = imageCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap == null) {
                    Toast.makeText(UploadImageActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
                } else if (category.equals("Select Category")) {
                    Toast.makeText(UploadImageActivity.this, "Select Image Category", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }
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
                    Toast.makeText(UploadImageActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadData() {
        DatabaseReference categoryReference = databaseReference.child(category);
        String uniqueKey = categoryReference.push().getKey();
        if (uniqueKey != null) {
            categoryReference.child(uniqueKey).setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadImageActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    clearComponents(); // Clear components after successful upload
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadImageActivity.this, "Failed to Upload Image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void clearComponents() {
        // Clear the ImageView
        galleryImageView.setImageDrawable(null);

        // Reset the spinner selection
        imageCategory.setSelection(0);

        // Reset the bitmap
        bitmap = null;
    }

    private void openGallery() {
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
                galleryImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
