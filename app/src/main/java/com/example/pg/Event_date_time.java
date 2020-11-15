package com.example.pg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.pg.upload.Events;

import com.example.pg.speciality.HealthActivity;
import com.example.pg.upload.Events;
import com.example.pg.upload.Upload;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Event_date_time extends AppCompatActivity {

    int date, time, Dec;


    EditText date_in;
    EditText time_in;
    EditText dec_in;
    Button sumButton;

    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_date_time);

        date_in = findViewById(R.id.date);
        time_in = findViewById(R.id.time);
        dec_in = findViewById(R.id.Dec);
        sumButton = findViewById(R.id.buttsum);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("AddEvent").child("health");

        date_in.setInputType(InputType.TYPE_NULL);
        time_in.setInputType(InputType.TYPE_NULL);

        date_in.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDateDialog(date_in);
            }
        });

        time_in.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimeDialog(time_in);
            }
        });


        sumButton.setEnabled(false);


    }

    private void showTimeDialog(final EditText time_in) {
        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                time_in.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };

        new TimePickerDialog(Event_date_time.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
    }

    private void showDateDialog(final EditText date_in) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
                date_in.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };

        new DatePickerDialog(Event_date_time.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        sumButton.setEnabled(true);
        date_in.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/") + 1));
        time_in.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/") + 1));
        dec_in.setText(data.getDataString().substring(data.getDataString().lastIndexOf("/") + 1));
//            editText.setText(data.getDataString());
        sumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToFirebase(data.getData());
            }
        });


    }




    public void uploadToFirebase(Uri data) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Event is adding...");
        progressDialog.show();

        StorageReference ref = storageReference.child("event/").child("health/").child("Events" + System.currentTimeMillis() + ".add");
        ref.putFile(data)

                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri uri = uriTask.getResult();

                        Events events = new Events(date_in.getText().toString(), uri.toString(), time_in.getText().toString(), uri.toString(), dec_in.getText().toString(), uri.toString());
                        databaseReference.child(databaseReference.push().getKey()).setValue(events);
                        Toast.makeText(Event_date_time.this, "File upload", Toast.LENGTH_LONG).show();
                        date_in.setText("");
                        time_in.setText("");
                        dec_in.setText("");
                        progressDialog.dismiss();

                    }
                });


    }
}

