package com.example.pg.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pg.Model.ModelMessage;
import com.example.pg.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterMessage extends  RecyclerView.Adapter<AdapterMessage.MyHolder>{
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_Right = 1;
    Context context;
    List<ModelMessage> messageList;
    FirebaseUser firebaseUser;
    //String imageUri;
    public AdapterMessage(Context context, List<ModelMessage> messageList){
        this.context = context;
        this.messageList = messageList;
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layouts: raw_message_left.xml for receiver, raw_message_right.xml for sender
        if(viewType == MSG_TYPE_Right) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_message_right, parent, false);
            return new MyHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_message_left, parent, false);
            return new MyHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
      //get data
        String message = messageList.get(position).getMessage();
        String timeStamp = messageList.get(position).getTimestamp();
        String type = messageList.get(position).getType();
        //convert time stamp to dd/mm//yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        // set data
        holder.messageIv.setText(message);
        holder.timeIv.setText(df.format("dd/MM/yy, hh:mm AA", calendar).toString());

        if(type.equals("text")){
            // text messages
            holder.messageIv.setVisibility(View.VISIBLE);
            holder.imageMessageTv.setVisibility(View.GONE);
            holder.messageIv.setText(message);
        }
        else{
            //image message
            holder.messageIv.setVisibility(View.GONE);
            holder.imageMessageTv.setVisibility(View.VISIBLE);
            Picasso.get().load(message).placeholder(R.drawable.ic_image_black).into(holder.imageMessageTv);

        }
        //click to show delete dialog
        holder.messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this message?");
                //delete button
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessage(position);
                    }
                });
                //cancel delete button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss dialog
                        dialog.dismiss();
                    }
                });
                //create and show dialog
                builder.create().show();
            }
        });

        //set seen/delivered status of message
        if(position == messageList.size()-1){
            if(messageList.get(position).isSeen()){
                holder.isSeenIv.setText("Seen");

            }else {
                holder.isSeenIv.setText("delivered");
            }
        }else {
            holder.isSeenIv.setVisibility(View.GONE);
        }
    }

    private void deleteMessage(int position) {
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // get timestamp of clicked message
        // compare the timestamp of clicked message with all messages in private message
        //where both values matches delete the message
        String msgTimeStamp = messageList.get(position).getTimestamp();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Messages");
        Query query = ref.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    // remove only sender message
                    if(ds.child("sender").getValue().equals(myUid)){

                        //remove the message from private message
                        ds.getRef().removeValue();
                        Toast.makeText(context,"Message deleted..", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context,"You can delete only your message..", Toast.LENGTH_SHORT).show();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // get currently signed in user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(messageList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_Right;
        }
        else{
            return MSG_TYPE_LEFT;
        }

    }

    //view holder class
    static class  MyHolder extends RecyclerView.ViewHolder{
        //views
        ImageView imageMessageTv;
        TextView messageIv, timeIv, isSeenIv;
        LinearLayout messageLayout; //for click listener to show delete

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            //init views
            messageIv = itemView.findViewById(R.id.messageIv);
            imageMessageTv = itemView.findViewById(R.id.imageMessageTv);
            timeIv = itemView.findViewById(R.id.timeIv);
            isSeenIv = itemView.findViewById(R.id.isSeentIv);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }
}
