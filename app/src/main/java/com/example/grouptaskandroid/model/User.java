package com.example.grouptaskandroid.model;

public class User {
    int pk;
    String username;
    String email;

    public User(int pk, String username, String email) {
        this.pk = pk;
        this.username = username;
        this.email = email;
    }

    public int getPk() {
        return pk;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User{" +
                "pk=" + pk +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
