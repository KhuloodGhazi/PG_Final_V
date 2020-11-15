package com.example.pg.upload;

public class Events {

    public String date;
    public String time;
    public String dec;
    public String url;



    public Events(String toString, String s, String date, String time, String dec, String url) {
            this.date = date;
            this.time = time;
            this.dec = dec;
            this.url = url;
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

        public String getUrl () {
            return url;
        }

        public void setUrl (String url){
            this.url = url;
        }

    }

