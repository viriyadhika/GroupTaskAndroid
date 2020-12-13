package com.example.grouptaskandroid.exception;

import com.android.volley.VolleyError;

public class AuthenticationFailedException extends Exception {
    VolleyError volleyError;

    public AuthenticationFailedException(VolleyError volleyError) {
        this.volleyError = volleyError;
    }

    public VolleyError getVolleyError() {
        return volleyError;
    }
}
