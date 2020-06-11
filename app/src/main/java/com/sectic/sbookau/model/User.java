package com.sectic.sbookau.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bioz on 6/22/2017.
 */

public class User {
    @SerializedName("token")
    public String token;
    @SerializedName("id")
    public String id;
    @SerializedName("username")
    public String username;
    @SerializedName("password")
    public String password;
    @SerializedName("displayName")
    public String displayName;
    @SerializedName("email")
    public String email;
    @SerializedName("userRight")
    public String userRight;
    @SerializedName("avatarUrl")
    public String avatarUrl;
    @SerializedName("idToken")
    public String idToken;

    public User(){
    }

    public User(String id, String username, String displayName, String email, String userRight, String avatarUrl, String token){
        this.token = token;
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.userRight = userRight;
        this.avatarUrl = avatarUrl;
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
    public User(String idToken){
        this.idToken = idToken;
    }
}
