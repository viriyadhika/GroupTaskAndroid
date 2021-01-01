package com.example.grouptaskandroid.exception.network;

public class NotFoundException extends Exception {
    private String message;
    public NotFoundException(String message) {
        this.message = message;
    }
}
