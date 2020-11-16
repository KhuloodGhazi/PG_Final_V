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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.pg.speciality.HealthEventActivity;
import com.example.pg.upload.Events;

import com.example.pg.speciality.HealthActivity;
import com.example.pg.upload.Events;
import com.example.pg.upload.Upload;
import com.example.pg.user.HealthEventUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Event_date_time extends AppCompatActivity {

    EditText date_in;
    EditText time_in;
    EditText dec_in;

    Button sumButton;

    private String userDate;
    private String userTime;
    private int eventCounter;

    DatabaseReference databaseReference;
    final HashMap<String , Object> hashMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_date_time);

        date_in = findViewById(R.id.date);
        time_in = findViewById(R.id.time);
        dec_in = findViewById(R.id.Dec);
        sumButton = findViewById(R.id.buttsum);

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

        sumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToFirebase();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("events").push();


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

                userTime = simpleDateFormat.format(calendar.getTime());

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

                userDate = simpleDateFormat.format(calendar.getTime());

            }
        };

        new DatePickerDialog(Event_date_time.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void gatherData() {
        final String userInput = dec_in.getText().toString();

        hashMap.put("time" , userTime);
        hashMap.put("date" , userDate);
        hashMap.put("dec" , userInput);

    }
    public void uploadToFirebase() {

        gatherData();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                databaseReference.setValue(hashMap);
                Toast.makeText(Event_date_time.this, "Event has been created successfully", Toast.LENGTH_LONG).show();

//                if (ForumFragment.user_type.equals("spec")) {
                startActivity(new Intent(getApplicationContext(), HealthEventActivity.class));
//                    Log.d("TAG", "event_date_time spec : " + ForumFragment.user_type);
//                }
//                if (ForumFragment.user_type.equals("user")) {
//                    startActivity(new Intent(getApplicationContext(), HealthEventUser.class));
//                    Log.d("TAG", "event_date_time user:  " + ForumFragment.user_type);
//
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}

