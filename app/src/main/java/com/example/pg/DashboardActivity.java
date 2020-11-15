package com.example.pg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pg.notifications.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {

    //get details of user and post
    private String myDp,myUsername, myName;
    private ImageView pImageIv;
    private TextView uNameTv, uUsernameTV;

    //firebase auth
    FirebaseAuth firebaseAuth;
    String currentUserID;
    ActionBar actionBar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private BottomNavigationView navigationView;
    private NavigationView navigationViewHeader;
    String mUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Actionbar and its title
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");



        //init
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //bottom navigation
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        navigationViewHeader = (NavigationView) findViewById(R.id.header);
        View hView =  navigationViewHeader.getHeaderView(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationViewHeader.setNavigationItemSelectedListener(selectedListenerDrawer);


        pImageIv = (ImageView) hView.findViewById(R.id.imageProfileDashboard);
        uNameTv = (TextView) hView.findViewById(R.id.nameProfile);
        uUsernameTV = (TextView) hView.findViewById(R.id.usernameProfile);
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        //home fragment transaction (default, on star)
        actionBar.setTitle("Home");//change actionbar title
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content, fragment1, "");
        ft1.commit();

        checkUserStatus();
        loadUserInfo();
        // update token
        updateToken(FirebaseInstanceId.getInstance().getToken());

    }
    public  void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    //handle item clicks
                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            //home fragment transaction
                            actionBar.setTitle("Forum");//change actionbar title
                            HomeFragment fragment1 = new HomeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content, fragment1, "");
                            ft1.commit();
                            return true;
                        case R.id.nav_forum:
                            //users fragment transaction
                            actionBar.setTitle("Reference");//change actionbar title
                            ForumFragment fragment3 = new ForumFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content, fragment3, "");
                            ft3.commit();
                            return true;
                        case R.id.nav_event:
                            //users fragment transaction
                            actionBar.setTitle("Event");//change actionbar title
                            EventFragment fragment5 = new EventFragment();
                            FragmentTransaction ft5 = getSupportFragmentManager().beginTransaction();
                            ft5.replace(R.id.content, fragment5, "");
                            ft5.commit();
                            return true;
                        case R.id.nav_Message:
                            //users fragment transaction
                            actionBar.setTitle("Message");//change actionbar title
                            MessageFragment fragment4 = new MessageFragment();
                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content, fragment4, "");
                            ft4.commit();
                            return true;

                    }

                    return false;
                }
            };


    private NavigationView.OnNavigationItemSelectedListener selectedListenerDrawer =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    //handle item clicks
                    switch (menuItem.getItemId()) {
                        case R.id.profile:
                            //profile
                            //get current user
                            String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            Query query  = FirebaseDatabase.getInstance().getReference().child("Specialisations").orderByChild("uid").equalTo(currentUserID);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        startActivity(new Intent(DashboardActivity.this, ProfileSpecialistActivity.class));
                                        finish();
                                    }
                                    else {
                                        startActivity(new Intent(DashboardActivity.this, ProfileUserActivity.class));
                                        finish();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }

                            });
                            return true;

                    }

                    return false;
                }
            };


    private void loadUserInfo() {
        Query myRef = FirebaseDatabase.getInstance().getReference("Users");
        myRef.orderByChild("uid").equalTo(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data
                    myName = "" + ds.child("name").getValue();
                    myUsername = "" + ds.child("username").getValue();
                    myDp = "" + ds.child("image").getValue();

                    //set data
                    uNameTv.setText(myName);
                    uUsernameTV.setText(myUsername);
                    try {
                        Picasso.get().load(myDp).placeholder(R.drawable.ic_person).into(pImageIv);
                    }catch (Exception e){
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query myReff = FirebaseDatabase.getInstance().getReference("Specialisations");
        myReff.orderByChild("uid").equalTo(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data
                    myName = "" + ds.child("name").getValue();
                    myUsername = "" + ds.child("username").getValue();
                    myDp = "" + ds.child("image").getValue();

                    //set data
                    uNameTv.setText(myName);
                    uUsernameTV.setText(myUsername);

                    try {
                        Picasso.get().load(myDp).placeholder(R.drawable.ic_person).into(pImageIv);
                    }catch (Exception e){
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

          mUID = user.getUid();
          //save uid of currently signed in user in shared references
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();
        } else {
            //user not signed in, go to main acitivity
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onStart() {
        //check on start of app
        checkUserStatus();
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mActionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}