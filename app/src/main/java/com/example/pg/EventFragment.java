package com.example.pg;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.pg.speciality.HealthEventActivity;
import com.example.pg.user.HealthEventUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class EventFragment extends Fragment {

    public String user_type = "";

    public void EventFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_event, container, false);



        LinearLayout health = view.findViewById(R.id.health_section);
        LinearLayout family = view.findViewById(R.id.family_section);
        LinearLayout growth = view.findViewById(R.id.growth_section);
        LinearLayout relation = view.findViewById(R.id.relationship_section);
        LinearLayout finance = view.findViewById(R.id.finance_section);
        LinearLayout religious = view.findViewById(R.id.religion_section);


        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();


        // Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Users").child("/" + user);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_type = dataSnapshot.child("usertype").getValue().toString();
                Log.d("TAG", "onDataChange: " + user_type);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });

        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_type.equals("spec")) {
                    startActivity(new Intent(getActivity(), HealthEventActivity.class));
                }
                if (user_type.equals("user")) {
                    startActivity(new Intent(getActivity(), HealthEventUser.class));
                }
            }
        });


        return view;
    }
}

