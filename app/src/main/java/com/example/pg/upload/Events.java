package com.example.pg.upload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pg.R;

import java.util.ArrayList;

public class Events extends BaseAdapter{

    Context mContext;
    public String date;
    public String time;
    public String dec;

    Events event;
    ArrayList<Events> arr = new ArrayList<>();
//    ArrayList<String> arr = new ArrayList<>();


    public Events() {

    }
    public Events(Context mContext, ArrayList<Events> arr) {
        this.mContext = mContext;
        this.arr = arr;
    }
//    public Events(Context mContext, ArrayList<String> arr) {
//        this.mContext = mContext;
//        this.arr = arr;
//    }

//    public Events(Context mContext, ArrayList<Events> arr) {
//        this.mContext = mContext;
//        this.arr = arr;
//    }

    public Events(String date, String time, String dec) {
            this.date = date;
            this.time = time;
            this.dec = dec;
        }

    @Override
    public int getCount() {
        return arr.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Events event = arr.get(i);
        view = LayoutInflater.from(mContext).inflate(R.layout.rowitemevent,null);

        TextView time = view.findViewById(R.id.id_time);
        TextView date = view.findViewById(R.id.id_date);
        TextView dec = view.findViewById(R.id.id_dec);

        time.setText(event.getTime());
        date.setText(event.getDate());
        dec.setText(event.getDec());


        return view;

    }

    public String getDate () {
            return date;
        }

        public void setDate (String date){
            this.date = date;
        }

        public String getTime () {
            return time;
        }

        public void setTime (String time){
            this.time = time;
        }

        public String getDec () {
            return dec;
        }

        public void setDec (String dec){
            this.dec = dec;
        }

    }

