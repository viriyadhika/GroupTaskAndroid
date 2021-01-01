package com.example.grouptaskandroid.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.grouptaskandroid.model.Group;
import com.example.grouptaskandroid.model.User;
import com.example.grouptaskandroid.util.AuthenticationManagerSingleton;
import com.example.grouptaskandroid.util.Constants;
import com.example.grouptaskandroid.util.RequestQueueSingleton;

import org.json.JSONObject;

import java.util.Map;

public class AddRemoveMemberRepository {

    public static final String TAG = "AddRemoveMemberRepository";
    private AuthenticationManagerSingleton authenticationManager;
    private RequestQueueSingleton requestQueueSingleton;
    private Listener listener;

    public AddRemoveMemberRepository(Context context) {
        authenticationManager = AuthenticationManagerSingleton.getInstance(context);
        requestQueueSingleton = RequestQueueSingleton.getInstance(context);
    }

    public void addMember(Group group, User user) {
        callAPI(false, group, user);
    }

    public interface Listener {
        void onAddMemberDone();
        void onRemoveMemberDone();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void callAPI(boolean isRetry, Group group, User user) {
        String url = Constants.url + "/groups/" + group.getPk() + "/users/" + user.getPk();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: ");
                        listener.onAddMemberDone();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authenticationManager.getCredential();
            }
        };
        requestQueueSingleton.addToRequestQueue(request);
    }

    public void removeMember(Group group, User user) {
        callAPIRemoveMember(false, group, user);
    }

    public void callAPIRemoveMember(boolean isRetry, Group group, User user) {
        String url = Constants.url + "/groups/" + group.getPk() + "/users/" + user.getPk();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: ");
                        listener.onRemoveMemberDone();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error);
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authenticationManager.getCredential();
            }
        };
        requestQueueSingleton.addToRequestQueue(request);
    }

}