package com.example.pg.speciality;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pg.R;
import com.example.pg.upload.Events;
import com.example.pg.upload.Upload;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class HealthActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1234;
    Button upload;

    EditText editText;
    EditText mTitle;

    String title_user_input;

    StorageReference storageReference;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        upload = findViewById(R.id.btnupload);
        editText = findViewById(R.id.et_txt);
        mTitle = findViewById(R.id.upload_title);

        editText.setOnClickListener(this);
        upload.setOnClickListener(this);


        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploadPDF").child("health");


        upload.setEnabled(false);

    }

    private void showFiles() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            upload.setEnabled(true);
            editText.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/") + 1));
//            editText.setText(data.getDataString());
            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadToFirebase(data.getData());
                }
            });


        }
    }

    public void uploadToFirebase(Uri data) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("File is loading...");
        progressDialog.show();

        StorageReference ref = storageReference.child("pdf/").child("health/").child("upload" + System.currentTimeMillis() + ".pdf");
        ref.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!((Task) uriTask).isComplete());
                        Uri uri = uriTask.getResult();

                        Upload upload = new Upload(editText.getText().toString(), uri.toString());
                        databaseReference.child(databaseReference.push().getKey()).setValue(upload);

                        uploadTitle();

                        Toast.makeText(HealthActivity.this, "File has been uploaded", Toast.LENGTH_LONG).show();

                        editText.setText("");
                        mTitle.setText("");
                        progressDialog.dismiss();

                    }

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressDialog.setMessage("File uploading " + (int) progress + "%");
            }
        });
    }

    public void uploadTitle() {
        title_user_input = mTitle.getText().toString();

        databaseReference = FirebaseDatabase.getInstance().getReference("title");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.setValue(title_user_input);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == editText) {
            showFiles();

        }

    }
}



