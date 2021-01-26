package com.example.grouptaskandroid.exception.network;

import androidx.annotation.Nullable;

public class NotFoundException extends Exception {
    private String message;
    public NotFoundException(String message) {
        this.message = message;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }
}
