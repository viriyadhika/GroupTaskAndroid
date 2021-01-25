package com.example.grouptaskandroid.exception.network;

import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class BadRequestException extends Exception {
    private VolleyError volleyError;
    private String errorMsg;

    public static final String TAG = "BadRequestException";

    public BadRequestException(VolleyError error) {
        this.volleyError = error;
        if (error.networkResponse.data != null) {
            String buffer = new String(error.networkResponse.data, StandardCharsets.UTF_8);
            try {
                JSONObject jsonObject = new JSONObject(buffer);
                errorMsg = jsonObject.getString("detail");
            }catch (JSONException err){
                Log.d(TAG + " Error", err.toString());
            }
        }
    }

    public VolleyError getVolleyError() {
        return volleyError;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
