package com.example.admincollegeapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.pspdfkit.PSPDFKit;
import com.pspdfkit.configuration.activity.PdfActivityConfiguration;
import com.pspdfkit.ui.PdfActivity;

import java.io.File;
import java.util.HashMap;

public class UploadPdfActivity extends AppCompatActivity {

    private Uri pdfData;
    private EditText pdfTitle;
    private TextView pdfTextView;
    private MaterialButton uploadPdfBTN, previewPdf;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    private static final int REQ = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        PSPDFKit.initialize(this, "YOUR_LICENSE_KEY_GOES_HERE");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        pdfTitle = findViewById(R.id.pdfTitleTextView);
        pdfTextView = findViewById(R.id.pdfTextView);
        uploadPdfBTN = findViewById(R.id.uploadPdfButton);
        previewPdf = findViewById(R.id.pdfPreview);
        progressDialog = new ProgressDialog(this);

        MaterialCardView addPdfCardView = findViewById(R.id.addPdf);
        addPdfCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    requestPermission();
                } else {
                    openPdfPicker();
                }
            }
        });

        previewPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pdfData != null) {
                    displayPDF(pdfData);
                } else {
                    Toast.makeText(UploadPdfActivity.this, "Select Pdf", Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadPdfBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = pdfTitle.getText().toString().trim();
                if (title.isEmpty()) {
                    pdfTitle.setError("Required");
                    pdfTitle.requestFocus();
                } else if (pdfData == null) {
                    Toast.makeText(UploadPdfActivity.this, "Please Select PDF", Toast.LENGTH_SHORT).show();
                } else {
                    uploadPdf(title);
                }
            }
        });
    }

    private void uploadPdf(String title) {
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Uploading PDF...");
        progressDialog.show();

        String pdfName = title + "-" + System.currentTimeMillis() + ".pdf";
        StorageReference reference = storageReference.child("pdf").child(pdfName);

        reference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        uploadData(title, uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(UploadPdfActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadPdfActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData(String title, String downloadUrl) {
        String uniqueKey = databaseReference.child("pdf").push().getKey();
        HashMap<String, Object> data = new HashMap<>();
        data.put("pdfTitle", title);
        data.put("pdfUrl", downloadUrl);

        databaseReference.child("pdf").child(uniqueKey).setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        Toast.makeText(UploadPdfActivity.this, "Pdf Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        clearComponents(); // Clear components after successful upload
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(UploadPdfActivity.this, "Failed to upload pdf", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openPdfPicker();
        } else {
            Toast.makeText(this, "Permission Denied! Please allow the permission to read PDF files.", Toast.LENGTH_SHORT).show();
        }
    }

    void openPdfPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select Pdf File"), REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ && resultCode == RESULT_OK && data != null) {
            pdfData = data.getData();
            if (pdfData != null) {
                pdfTextView.setText(getPdfName(pdfData));
            }
        }
    }

    private String getPdfName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = new File(uri.getPath()).getName();
        }
        return result;
    }

    private void displayPDF(Uri pdfData) {
        final Uri uri = Uri.parse("file:///android_asset/my-document.pdf");
        PdfActivityConfiguration config = new PdfActivityConfiguration.Builder(this).build();
        PdfActivity.showDocument(this, pdfData, config);
    }

    private void clearComponents() {
        pdfTitle.setText("");
        pdfTextView.setText("");
        pdfData = null;
    }
}
