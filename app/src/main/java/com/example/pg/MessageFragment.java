package com.example.pg;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pg.Adapter.AdapterMessageList;
import com.example.pg.Model.ModelMessage;
import com.example.pg.Model.ModelMessageList;
import com.example.pg.Model.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List <ModelMessageList>modelMessageLists;
    List<ModelUser> userList;
    FirebaseUser currentUser;
    AdapterMessageList adapterMessageList;

    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.recyclerViewMessageList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        modelMessageLists= new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MessageList").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelMessageLists.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelMessageList modelMessageList = ds.getValue(ModelMessageList.class);
                    modelMessageLists.add(modelMessageList);
                }
                loadMessage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void loadMessage() {
        userList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelUser user = ds.getValue(ModelUser.class);
                    for(ModelMessageList messageList: modelMessageLists) {
                        if(user.getUid() != null && user.getUid().equals(messageList.getId()) ){

                            userList.add(user);
                            break;


                        }
                    }
                    // adapter
                    adapterMessageList = new AdapterMessageList(getContext(),userList);
                    //set adapter
                    recyclerView.setAdapter(adapterMessageList);
                    //set last message
                    for(int i=0; i<userList.size(); i++){
                        lastMessage(userList.get(i).getUid());
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Specialisations");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelUser user = ds.getValue(ModelUser.class);
                    for(ModelMessageList messageList: modelMessageLists) {
                        if(user.getUid() != null && user.getUid().equals(messageList.getId()) ){

                            userList.add(user);
                            break;


                        }
                    }
                    // adapter
                    adapterMessageList = new AdapterMessageList(getContext(),userList);
                    //set adapter
                    recyclerView.setAdapter(adapterMessageList);
                    //set last message
                    for(int i=0; i<userList.size(); i++){
                        lastMessage(userList.get(i).getUid());
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void lastMessage(final String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelMessage message = ds.getValue(ModelMessage.class);
                    if(message == null){
                        continue;
                    }
                    String sender = message.getSender();
                    String receiver = message.getReceiver();
                    if(sender == null || receiver == null){
                        continue;
                    }
                    if(message.getReceiver().equals(currentUser.getUid()) && message.getSender().equals(userId) ||
                            message.getReceiver().equals(userId) && message.getSender().equals(currentUser.getUid())){
                        //instead of displying url in message show "sent photo
                        if(message.getType().equals("image")){
                            theLastMessage = "Sent a photo";
                        }
                        else {
                            theLastMessage = message.getMessage();
                        }

                    }
                }
                adapterMessageList.setLastMessage(userId, theLastMessage);
                adapterMessageList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}