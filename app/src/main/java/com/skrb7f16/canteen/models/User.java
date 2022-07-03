package com.skrb7f16.canteen.models;

public class User {
    String username,userId;

    public User(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
