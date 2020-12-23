package com.example.grouptaskandroid.model;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return pk == user.pk &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pk, username, email);
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
