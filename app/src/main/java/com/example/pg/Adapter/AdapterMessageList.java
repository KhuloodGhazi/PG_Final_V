package com.example.pg.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pg.Model.ModelMessageList;
import com.example.pg.Model.ModelUser;
import com.example.pg.PrivateMessageActivity;
import com.example.pg.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterMessageList extends RecyclerView.Adapter<AdapterMessageList.MyHolder> {
    Context context;
    List<ModelUser> userList; //get user info
    private HashMap<String, String> lastMessageMap;


    public AdapterMessageList(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflate layout
            View view = LayoutInflater.from(context).inflate(R.layout.message_list_users, parent, false);
            return new MyHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
        // get data
        final String hisUid = userList.get(i).getUid();

        String name = userList.get(i).getName();
        String username = userList.get(i).getUsername();
        String image = userList.get(i).getImage();

        String lastMessage = lastMessageMap.get(hisUid);
        // set data
        holder.nameMessagelistUser.setText(name);
        holder.usernameMessagelistUser.setText(username);
        if(lastMessage == null || lastMessage.equals("default")){
            holder.lastmessageMessagelistUser.setVisibility(View.GONE);
        }
        else{
            holder.lastmessageMessagelistUser.setVisibility(View.VISIBLE);
            holder.lastmessageMessagelistUser.setText(lastMessage );
        }

        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_person_black).into(holder.profileImageMessagelistUser);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.ic_person_black).into(holder.profileImageMessagelistUser);
        }
        // handle click of user in message list
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start message activity with that user
                Intent intent = new Intent(context, PrivateMessageActivity.class);
                intent.putExtra("uid", hisUid);
                context.startActivity(intent);
            }
        });
    }


        public void setLastMessage (String userId, String lastMessage){
            lastMessageMap.put(userId, lastMessage);
        }

        @Override
        public int getItemCount () {
            return userList.size(); // size of the list
        }

        class MyHolder extends RecyclerView.ViewHolder {
            //Declare user views
            ImageView profileImageMessagelistUser;
            TextView nameMessagelistUser, usernameMessagelistUser, lastmessageMessagelistUser;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                //Initialize Specialisation views
                profileImageMessagelistUser = itemView.findViewById(R.id.profileImageMessagelistUser);
                nameMessagelistUser = itemView.findViewById(R.id.nameMessagelistUser);
                usernameMessagelistUser = itemView.findViewById(R.id.usernameMessagelistUser);
                lastmessageMessagelistUser = itemView.findViewById(R.id.lastmessageMessagelistUser);
            }
        }

    }

