package com.example.pg.notifications;

public class Sender {
    private Data data;
    private String to;// change later name
    public Sender(Data data, String to){
        this.data = data;
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
