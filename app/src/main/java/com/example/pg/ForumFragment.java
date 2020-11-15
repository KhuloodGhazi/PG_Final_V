package com.example.pg;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.pg.speciality.FamilyActivity;
import com.example.pg.speciality.FinanceActivity;
import com.example.pg.speciality.GrowthActivity;
import com.example.pg.speciality.HealthActivity;
import com.example.pg.speciality.RelationActivity;
import com.example.pg.user.FamilyUser;
import com.example.pg.user.FinanceUser;
import com.example.pg.user.GrowthUser;
import com.example.pg.user.HealthUser;
import com.example.pg.user.RelationUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ForumFragment extends Fragment {

    public String user_type = "";

    public ForumFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_forum, container, false);

        LinearLayout health = view.findViewById(R.id.health_section);
        LinearLayout family = view.findViewById(R.id.family_section);
        LinearLayout growth = view.findViewById(R.id.growth_section);
        LinearLayout relation = view.findViewById(R.id.relationship_section);
        LinearLayout finance = view.findViewById(R.id.finance_section);
        LinearLayout religion = view.findViewById(R.id.religion_section);


        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Users").child("/" + user);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                if(user_type.equals("spec")) {
                    startActivity(new Intent(getActivity(), HealthActivity.class));
                }
                if(user_type.equals("user")) {
                    startActivity(new Intent(getActivity(), HealthUser.class));
                }
            }
        });

        family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_type.equals("spec")) {
                    startActivity(new Intent(getActivity(), FamilyActivity.class));
                }
                if(user_type.equals("user")) {
                    startActivity(new Intent(getActivity(), FamilyUser.class));
                }
            }
        });

        growth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_type.equals("spec")) {
                    startActivity(new Intent(getActivity(), GrowthActivity.class));
                }
                if(user_type.equals("user")) {
                    startActivity(new Intent(getActivity(), GrowthUser.class));
                }
            }
        });

        relation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_type.equals("spec")) {
                    startActivity(new Intent(getActivity(), RelationActivity.class));
                }
                if(user_type.equals("user")) {
                    startActivity(new Intent(getActivity(), RelationUser.class));
                }
            }
        });

        finance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_type.equals("spec")) {
                    startActivity(new Intent(getActivity(), FinanceActivity.class));
                }
                if(user_type.equals("user")) {
                    startActivity(new Intent(getActivity(), FinanceUser.class));
                }
            }
        });

        religion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_type.equals("spec")) {
                    startActivity(new Intent(getActivity(), RelationActivity.class));
                }
                if(user_type.equals("user")) {
                    startActivity(new Intent(getActivity(), RelationUser.class));
                }
            }
        });

        return view;
    }

}