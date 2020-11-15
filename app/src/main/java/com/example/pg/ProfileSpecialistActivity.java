package com.example.pg;


import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pg.Adapter.AdapterPost;
import com.example.pg.Model.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileSpecialistActivity extends AppCompatActivity {
    ActionBar actionBar;
    // firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //views from xml

    ImageView avatarTv, coverTv, location, date;
    TextView nameTv, usernameTv, bioTv, locationTv, dateBrithdayTv, specialisationTv;
    FloatingActionButton fab;
    RecyclerView postsRecyclerView;
    List<ModelPost> postList;
    AdapterPost adapterPost;
    String uid;

    public ProfileSpecialistActivity(){
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_specialist);

        //Actionbar and its title
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");



        // init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Specialisations");

        //init views
        avatarTv = findViewById(R.id.avatarIv);
        coverTv = findViewById(R.id.coverIv);
        nameTv = findViewById(R.id.nameTv);
        usernameTv = findViewById(R.id.usernameTv);
        bioTv = findViewById(R.id.bioTv);
        specialisationTv = findViewById(R.id.specialisationTv);
        locationTv = findViewById(R.id.locationTv);
        dateBrithdayTv = findViewById(R.id.dateBirthdayTv);
        fab = findViewById(R.id.fab);
        location = findViewById(R.id.location);
        date = findViewById(R.id.date);
        postsRecyclerView = findViewById(R.id.recyclerViewS_posts);

        /* We have to get info of currently signed in user. We can get it using user's email or uid
        I'm gonna retrieve user detail using email*/
        /* By using orderByChild query we will show the detail from a node
        whose key named email has value equal to currently signed in email.
        It will search all nodes, where the key matches it will get its detail*/

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required data get
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data
                    String name = "" + ds.child("name").getValue();
                    String username = "" + ds.child("username").getValue();
                    String specialisation = "" + ds.child("specialisation").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();
                    String dateBrithday = "" + ds.child("BirthDate").getValue();
                    String locationIm = "" + ds.child("location").getValue();
                    String bio = "" + ds.child("bio").getValue();

                    // set data
                    nameTv.setText(name);
                    usernameTv.setText(username);
                    specialisationTv.setText(specialisation);

                    try{
                        //if image is received then set
                        Picasso.get().load(image).placeholder(R.drawable.ic_person).into(avatarTv);
                    }
                    catch (Exception e){
                        // if there is anay exception while getting image then set default
                        avatarTv.setImageResource(R.drawable.ic_person);
                    }
                    try{
                        //if image is received then set
                        Picasso.get().load(cover).into(coverTv);
                    }
                    catch (Exception e){
                        // if there is anay exception while getting image then set default

                    }
                    dateBrithdayTv.setText(dateBrithday);
                    locationTv.setText(locationIm);
                    bioTv.setText(bio);
                    if(!locationIm.isEmpty()){
                        location.setVisibility(View.VISIBLE);
                    }
                    else{
                        location.setVisibility(View.INVISIBLE);
                    }
                    if(!dateBrithday.isEmpty()){
                        date.setVisibility(View.VISIBLE);
                    }
                    else{
                        date.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //fab button click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent edit profile activity
                //open edit profile activity
                Intent intent = new Intent(ProfileSpecialistActivity.this,EditProfileSpecialistActivity.class);
                startActivity(intent);
                finish();

            }
        });
        postList = new ArrayList<>();
        checkUsrStatus();
        loadMyPost();
        if(!user.isEmailVerified()){

            // send verification link

            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ProfileSpecialistActivity.this, "Verify Your Email, Link Has Been Sent !",
                            Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }

    }

    private void loadMyPost() {
        //linear layout for recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfileSpecialistActivity.this);
        //show newest post first, for this load from last
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        //set this layout to recyclerview
        postsRecyclerView.setLayoutManager(linearLayoutManager);
        //init posts list
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //query to load posts
        /*
        whenever user publishes a post the uid of this user is also saved as info of post
        so we're retrieving posts having uid equals to uid of current user
        */
        Query query = ref.orderByChild("uid").equalTo(uid);
        //get all data from this ref
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            postList.clear();
            for (DataSnapshot ds: snapshot.getChildren()){
                ModelPost modelPost = ds.getValue(ModelPost.class);
                //add to list
                postList.add(modelPost);
                //adapter
                adapterPost = new AdapterPost(ProfileSpecialistActivity.this, postList);
                // set this adapter to recyclerview
                postsRecyclerView.setAdapter(adapterPost);
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileSpecialistActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT);

            }
        });
    }

    private void checkUsrStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            //user is signed in stay here
            //set email of logged in user
            uid = user.getUid();
        }else{
            Intent intent = new Intent(ProfileSpecialistActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}