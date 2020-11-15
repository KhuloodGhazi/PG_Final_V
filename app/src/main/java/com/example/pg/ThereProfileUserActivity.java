package com.example.pg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pg.Adapter.AdapterPost;
import com.example.pg.Model.ModelPost;
import com.example.pg.Model.ModelUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThereProfileUserActivity extends AppCompatActivity {
    ActionBar actionBar;
    FirebaseAuth firebaseAuth;
    RecyclerView postsRecyclerView;
    List<ModelPost> postList;
    AdapterPost adapterPost;
    String uid;

    //views from xml

    ImageView avatarTv, coverTv, location, date, message;
    TextView nameTv, usernameTv, bioTv, locationTv, dateBrithdayTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile_user);
        //get uid of clicked user to retrieve his posts
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");


        //Actionbar and its title
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        postsRecyclerView = findViewById(R.id.recyclerViewP_posts);
        firebaseAuth = FirebaseAuth.getInstance();

        //init views
        avatarTv = findViewById(R.id.avatarIvP);
        coverTv = findViewById(R.id.coverIvP);
        nameTv = findViewById(R.id.nameTvP);
        usernameTv = findViewById(R.id.usernameTvP);
        bioTv = findViewById(R.id.bioTvP);
        locationTv = findViewById(R.id.locationTvP);
        dateBrithdayTv = findViewById(R.id.dateBirthdayTvP);
        location = findViewById(R.id.location);
        date = findViewById(R.id.date);
        message = findViewById(R.id.messageBtnUser);

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required data get
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data
                    String name = "" + ds.child("name").getValue();
                    String username = "" + ds.child("username").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();
                    String dateBrithday = "" + ds.child("BirthDate").getValue();
                    String locationIm = "" + ds.child("location").getValue();
                    String bio = "" + ds.child("bio").getValue();

                    // set data
                    nameTv.setText(name);
                    usernameTv.setText(username);

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

        postList = new ArrayList<>();
        checkUsrStatus();
        loadHisPosts();
        message.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                //user not signed in, go to main acitivity
              //  startActivity(new Intent(ThereProfileUserActivity.this, PrivateMessageActivity.class));
                //finish();
                Intent intent = new Intent(ThereProfileUserActivity.this, PrivateMessageActivity.class);
                intent.putExtra("uid", uid); //we will get the detail of post from this post id
                startActivity(intent);
                finish();
            }
        });
    }



    private void loadHisPosts() {
        //linear layout for recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ThereProfileUserActivity.this);
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
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    //add to list
                    postList.add(modelPost);
                    //adapter
                    adapterPost = new AdapterPost(ThereProfileUserActivity.this, postList);
                    // set this adapter to recyclerview
                    postsRecyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ThereProfileUserActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT);

            }
        });
    }

    private void checkUsrStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            //set email of logged in user
        } else {
            Intent intent = new Intent(ThereProfileUserActivity.this, LoginActivity.class);
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