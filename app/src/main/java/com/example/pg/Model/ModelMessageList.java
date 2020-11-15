package com.example.pg.Model;

public class ModelMessageList {
    String id; // to get sender and receiver uid

    public ModelMessageList(String id) {
        this.id = id;
    }
    public ModelMessageList() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
