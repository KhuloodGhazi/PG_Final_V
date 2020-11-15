package com.example.pg;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pg.Adapter.AdapterMessage;
import com.example.pg.Model.ModelMessage;
import com.example.pg.Model.ModelMessageList;
import com.example.pg.Model.ModelUser;
import com.example.pg.notifications.Data;
import com.example.pg.notifications.Sender;
import com.example.pg.notifications.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PrivateMessageActivity extends AppCompatActivity {
    //get details of user and post
    List<ModelUser> userList;
    DatabaseReference reference;
    private String HisDp,HisUsername, HisName, hisUid, myUid;
    //volley request queue for notification
    private RequestQueue requestQueue;
    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    //permissions array
    String[] cameraPermissions;
    String[] storagePermissions;
    //image picked will be same in this uri
    Uri image_uri = null;
    private boolean notify = false;
    //firebase auth
    FirebaseAuth firebaseAuth;
    //views from xml
    RecyclerView recyclerView;
    ImageView profileImage;
    TextView profileName, profileUsername;
    EditText messageEditText;
    ImageButton sendBtn, sendPhoto;
    //for checking if user has seen message or not
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;
    List<ModelMessage> messageList;
    AdapterMessage adapterMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_message);


        //init views
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.message_recyclerView);
        profileImage = findViewById(R.id.profileMessageImage);
        profileName = findViewById(R.id.nameProfileMessage);
        profileUsername = findViewById(R.id.usernameProfileMessage);
        messageEditText = findViewById(R.id.messageEdit);
        sendBtn = findViewById(R.id.sendBtnMessage);
        sendPhoto = findViewById(R.id.imageBtnMessage);

        //init permissions arrays;
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //get uid of clicked user to retrieve his info
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("uid");
        checkUserStatus();
        loadUserInfo();

        //click button to import image
        sendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //show image pick dialog
                showImagePickDialog();
            }
        });
        //click button to send message
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                //get text from edit text
                String message = messageEditText.getText().toString().trim();
                //click if text is empty or not
                if(TextUtils.isEmpty(message)){
                    //text empty
                    
                }
                else{
                    //text not empty
                    sendMessage(message);
                    
                }
                //reset edittext after sending message
                messageEditText.setText("");
            }
        });

        loadMessages();
        seenMessages();

    }

    private void showImagePickDialog() {

        String[] options = {"Camera" , "Gallery"};
        //dailog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image from");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }else {
                        pickFromCamera();
                    }
                }
                if(which==1){

                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }else {
                        pickFromGallery();
                    }
                }
            }
        });

        builder.create().show();
    }
    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent , IMAGE_PICK_GALLERY_CODE);

    }

    private void pickFromCamera() {

        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        cv.put(MediaStore.Images.Media.TITLE,"Temp Descr");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI , cv);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT , image_uri);
        startActivityForResult(intent , IMAGE_PICK_CAMERA_CODE);

    }


    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result ;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this , storagePermissions , STORAGE_REQUEST_CODE);

    }

    private boolean checkCameraPermission(){

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this , cameraPermissions , CAMERA_REQUEST_CODE);

    }


    private void seenMessages() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Messages");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModelMessage message = ds.getValue(ModelMessage.class);
                    if(message.getReceiver().equals(myUid)&& message.getSender().equals(hisUid)){
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen", true);
                        ds.getRef().updateChildren(hasSeenHashMap);
                    
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void loadMessages() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setStackFromEnd(true);
        messageList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Messages");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    ModelMessage message = ds.getValue(ModelMessage.class);
                    if(message.getReceiver().equals(myUid)&& message.getSender().equals(hisUid) || message.getReceiver().equals(hisUid) && message.getSender().equals(myUid)) {
                        messageList.add(message);
                    }

                    //pass myUid and postId as parameter of constructor of comment adapter
                    adapterMessage = new AdapterMessage(PrivateMessageActivity.this , messageList);
                    recyclerView.setAdapter(adapterMessage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(final String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages");
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("type", "text");
        hashMap.put("isSeen", false);

        databaseReference.push().setValue(hashMap);
        Query myReff = FirebaseDatabase.getInstance().getReference("Specialisations");
        myReff.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data

                    if (notify) {

                        sentNotification(hisUid, "" + ds.child("name").getValue(), message);

                    } else {
                        notify = false;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query myRef = FirebaseDatabase.getInstance().getReference("Users");
        myRef.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data

                    if (notify) {

                        sentNotification(hisUid, "" + ds.child("name").getValue(), message);

                    } else {
                        notify = false;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //create messagelist node/child in firebase database
        final DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("MessageList")
                .child(myUid)
                .child(hisUid);
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    messageRef.child("id").setValue(hisUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference messageRef2 = FirebaseDatabase.getInstance().getReference("MessageList")
                .child(hisUid)
                .child(myUid);
        messageRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    messageRef.child("id").setValue(myUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sentNotification(final String hisUid, final String name, final String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myUid,  name+": "+message, "New Message", hisUid, R.drawable.ic_person);
                    Sender sender = new Sender(data, token.getToken());
                    //FCM json object request
                    try {
                        JSONObject sendJsonObj = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", sendJsonObj,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                    //response of the request


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                //put params
                                Map <String, String>headers=new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "Key=AAAAOsTgCxM:APA91bFZ9QGjLfx2-yUWy7JFCWAIQXbozG79ikqMt_wa-Vx7j0n5jsloF3lg0F21YUqo7i49b1QwhwhtgE9ZUduuCq3xzIfcjnCO_5J0ueFwUUpqt9aPIDbw4z97wH3HvGaUaCuYB72k");
                                return headers;

                            }
                        };
                        //add this request to queue
                        requestQueue.add(jsonObjectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadUserInfo() {
        Query myRef = FirebaseDatabase.getInstance().getReference("Users");
        myRef.orderByChild("uid").equalTo(hisUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data
                    HisName = "" + ds.child("name").getValue();
                    HisUsername = "" + ds.child("username").getValue();
                    HisDp = "" + ds.child("image").getValue();

                    //set data
                    profileName.setText(HisName);
                    profileUsername.setText(HisUsername);
                    try {
                        Picasso.get().load(HisDp).placeholder(R.drawable.ic_person).into(profileImage);
                    }catch (Exception e){
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query myReff = FirebaseDatabase.getInstance().getReference("Specialisations");
        myReff.orderByChild("uid").equalTo(hisUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data
                    HisName = "" + ds.child("name").getValue();
                    HisUsername = "" + ds.child("username").getValue();
                    HisDp = "" + ds.child("image").getValue();

                    //set data
                    profileName.setText(HisName);
                    profileUsername.setText(HisUsername);

                    try {
                        Picasso.get().load(HisDp).placeholder(R.drawable.ic_person).into(profileImage);
                    }catch (Exception e){
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);

    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }



    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
          //user is singed in stay here
            // set email of logged in user
            myUid = user.getUid();
        } else {
            //user not signed in, go to main acitivity
            startActivity(new Intent(PrivateMessageActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //this method is called when user press allow or deny from permission result dialog
        //here we will handle permission cases(allowed or denied )
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED ;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED ;

                    if (cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }else {
                        Toast.makeText(this, "camera and storage both permission are necessary", Toast.LENGTH_SHORT).show();
                    }

                }else {

                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED ;
                    if (storageAccepted){
                        pickFromGallery();
                    }else {
                        Toast.makeText(this, "storage permission is necessary", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){

            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();

                //use this image uri to com.example.pg.upload to firebase storage
                try {
                    sendImageMessage(image_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE){
                 //image is picked from camera, get uri of image
                try {
                    sendImageMessage(image_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendImageMessage(Uri image_uri) throws IOException {
        notify = true;
        //progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending image...");
        progressDialog.show();
        final String timeStamp = ""+System.currentTimeMillis();
        String fileNameAndPath = "MessageImage/"+"post_"+timeStamp;
        // Messages node will be created that will contain all images sent via message;
        //get bitmap = MediaStore.Images
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        final byte[] data = baos.toByteArray(); //convert image to byte
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
        ref.putBytes(data)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // image com.example.pg.upload
                progressDialog.dismiss();
                //get uri and com.example.pg.upload image
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                String downloadUri = uriTask.getResult().toString();
                if(uriTask.isSuccessful()){
                    // add image uri and other info to database
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    // setup required data
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", myUid);
                    hashMap.put("receiver", hisUid);
                    hashMap.put("message", downloadUri);
                    hashMap.put("timestamp", timeStamp);
                    hashMap.put("type", "image");
                    hashMap.put("isSeen",false);
                    //put this data in firebase
                    reference.child("Messages").push().setValue(hashMap);

                    //send notifaction
                    Query myReff = FirebaseDatabase.getInstance().getReference("Specialisations");
                    myReff.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                //get data

                                if (notify) {

                                    sentNotification(hisUid, "" + ds.child("name").getValue(), "Sent you a photo..");

                                } else {
                                    notify = false;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Query myRef = FirebaseDatabase.getInstance().getReference("Users");
                    myRef.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                //get data

                                if (notify) {

                                    sentNotification(hisUid, "" + ds.child("name").getValue(), "Sent you a photo..");

                                } else {
                                    notify = false;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //create messagelist node/child in firebase database
                    final DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("MessageList")
                            .child(myUid)
                            .child(hisUid);
                    messageRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                messageRef.child("id").setValue(hisUid);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    DatabaseReference messageRef2 = FirebaseDatabase.getInstance().getReference("MessageList")
                            .child(hisUid)
                            .child(myUid);
                    messageRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                messageRef.child("id").setValue(myUid);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}