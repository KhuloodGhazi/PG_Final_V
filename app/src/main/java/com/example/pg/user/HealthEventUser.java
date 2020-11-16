package com.example.pg.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pg.R;
import com.example.pg.speciality.HealthEventActivity;
import com.example.pg.upload.Events;
import com.example.pg.upload.Upload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HealthEventUser extends AppCompatActivity {

    ListView listView;
    DatabaseReference databaseReference;

    ArrayList<Events> eventsList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_event_user);

        listView = findViewById(R.id.list_view);
        eventsList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("events");

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

                Events adapter = new Events(HealthEventUser.this, eventsList);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

}
