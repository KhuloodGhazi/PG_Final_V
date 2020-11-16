package com.example.pg.speciality;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.pg.Event_date_time;
import com.example.pg.R;
import com.example.pg.upload.Events;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Iterator;


public class HealthEventActivity extends AppCompatActivity implements View.OnClickListener  {

    ListView listView;
    ImageButton imageButton;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    ArrayAdapter<String> arrayAdapter;

    ArrayList<Events> eventsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_event);

        imageButton = findViewById(R.id.eventaddbutt);
        listView = findViewById(R.id.list_view_event);
        imageButton.setOnClickListener(this);
        eventsList = new ArrayList<>();

        retrieveFiles();

    }

    private void retrieveFiles() {

            databaseReference = FirebaseDatabase.getInstance().getReference("events");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {

                        Events events = ds.getValue(Events.class);
                        eventsList.add(events);

                    }

                    Events adapter = new Events(HealthEventActivity.this, eventsList);
                    listView.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });


    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(HealthEventActivity.this, Event_date_time.class);
        startActivity(intent);

    }








}
