package com.example.grouptaskandroid.exception.network;

import com.android.volley.VolleyError;

public class NoNetworkResponseException extends Exception {
    private VolleyError volleyError;
    private static final String errorMsg = "There is no network detected";

    public NoNetworkResponseException(VolleyError error) {
        this.volleyError = error;
    }

    public VolleyError getVolleyError() {
        return volleyError;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
