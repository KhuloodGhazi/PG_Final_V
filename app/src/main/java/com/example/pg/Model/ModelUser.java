package com.example.pg.Model;



public class ModelUser {

    String uid, name, image, username, email, cover, bio, BirthDate, location, specialisation;
    public ModelUser() {
    }


    public ModelUser(String uid, String name, String username, String image, String email,String specialisation, String cover, String bio, String BirthDate, String location) {
        this.uid = uid;
        this.name = name;
        this.username = username;
        this.image = image;
        this.email = email;
        this.cover = cover;
        this.email = email;
        this.bio = bio;
        this.BirthDate = BirthDate;
        this.location = location;
        this.specialisation = specialisation;

    }

    public String getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(String specialisation) {
        this.specialisation = specialisation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public String getCover() {
        return cover;
    }

    public String getBio() {
        return bio;
    }

    public String getBirthDate() {
        return BirthDate;
    }

    public String getLocation() {
        return location;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
