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
    List<Events> eventsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_event_user);

        listView = findViewById(R.id.list_view);
        eventsList = new ArrayList<Events>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(eventsList.get(position).getUrl()), "Event");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
            }
        });

        retrieveFiles();

    }
    private void retrieveFiles() {

        databaseReference = FirebaseDatabase.getInstance().getReference("uploadPDF").child("health");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Events events = ds.getValue(Events.class);
                    eventsList.add(events);

                }

                String[] names = new String[eventsList.size()];

                for(int i = 0; i < names.length; i++) {
                    names[i] = eventsList.get(i).getDate();
                }

                for(int i = 0; i < names.length; i++) {
                    names[i] = eventsList.get(i).getTime();
                }

                for(int i = 0; i < names.length; i++) {
                    names[i] = eventsList.get(i).getDec();
                }

                for(int i = 0; i < names.length; i++) {
                    names[i] = eventsList.get(i).getUrl();
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, names) {

                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                        View view = super.getView(position, convertView, parent);
                        TextView textView = view.findViewById(android.R.id.text1);

                        textView.setTextColor(Color.BLACK);
                        textView.setTextSize(20);
                        return view;

                    }
                };

                listView.setAdapter(arrayAdapter);

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }


}