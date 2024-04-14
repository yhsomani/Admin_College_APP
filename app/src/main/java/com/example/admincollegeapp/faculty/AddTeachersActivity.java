package com.example.admincollegeapp.faculty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddTeachersActivity extends AppCompatActivity {

    private CircleImageView teacherImage;
    private TextInputEditText teacherName, teacherEmail, teacherPost;
    private Spinner teacherCategory;
    private MaterialButton addTeacherButton;
    private Bitmap bitmap = null;
    private String downloadUrl = "";
    private static final int REQ = 1;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private DatabaseReference databaseReference, dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teachers);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("teacher");
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize views and Firebase references
        teacherImage = findViewById(R.id.addTeacherImage);
        teacherName = findViewById(R.id.addTeacherName);
        teacherEmail = findViewById(R.id.addTeacherEmail);
        teacherPost = findViewById(R.id.addTeacherPost);
        teacherCategory = findViewById(R.id.addTeacherCategory);
        addTeacherButton = findViewById(R.id.addTeacherButton);

        progressDialog = new ProgressDialog(this);

        // Set up spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.teacher_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherCategory.setAdapter(adapter);

        // Set click listeners
        teacherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addTeacherButton.setOnClickListener(v -> checkValidation());

        teacherCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle category selection
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void checkValidation() {
        String name = teacherName.getText().toString().trim();
        String email = teacherEmail.getText().toString().trim();
        String post = teacherPost.getText().toString().trim();
        String category = teacherCategory.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || post.isEmpty() || category.equals("Select Category")) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (bitmap == null) {
            insertData(name, email, post, category);
        } else {
            insertImage(name, email, post, category);
        }
    }

    private void insertImage(final String name, final String email, final String post, final String category) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalimg = baos.toByteArray();
        final StorageReference filePath = storageReference.child("Teachers").child(name + ".jpg");
        UploadTask uploadTask = filePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString();
                            insertData(name, email, post, category);
                        }
                    });
                } else {
                    Toast.makeText(AddTeachersActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                teacherImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertData(String name, String email, String post, String category) {
        dbRef = databaseReference.child(category);
        String uniqueKey = dbRef.push().getKey();

        if (uniqueKey != null) {
            TeacherData teacherData = new TeacherData(name, email, post, downloadUrl, category, uniqueKey);

            dbRef.child(uniqueKey).setValue(teacherData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(AddTeachersActivity.this, "Teacher Added", Toast.LENGTH_SHORT).show();

                            // Clear input components
                            teacherName.setText("");
                            teacherEmail.setText("");
                            teacherPost.setText("");
                            teacherCategory.setSelection(0); // Reset category to "Select Category"
                            teacherImage.setImageResource(R.drawable.man_user_icon); // Set a default image
                            bitmap = null; // Reset the bitmap
                            downloadUrl = ""; // Reset the download URL
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddTeachersActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}