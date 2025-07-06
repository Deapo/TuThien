package com.example.tuibikho.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey
    @NonNull
    private String uid;
    private String email;
    private String userName;
    private String imgAvatar;

    public UserEntity(){}

    public UserEntity(@NonNull String uid, String email, String userName) {
        this.uid = uid;
        this.email = email;
        this.userName = userName;
    }

    @NonNull
    public String getUid() {
        return uid;
    }
    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getImgAvatar() {
        return imgAvatar;
    }
    public void setImgAvatar(String imgAvatar) {
        this.imgAvatar = imgAvatar;
    }
}
