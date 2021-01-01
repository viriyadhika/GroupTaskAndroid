package com.example.grouptaskandroid.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.grouptaskandroid.data.generics.PostRepository;
import com.example.grouptaskandroid.model.Group;
import com.example.grouptaskandroid.model.User;
import com.example.grouptaskandroid.util.AuthenticationManagerSingleton;
import com.example.grouptaskandroid.util.Constants;
import com.example.grouptaskandroid.util.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class AddMemberRepository {

    public static final String TAG = "AddMemberRepository";
    private AuthenticationManagerSingleton authenticationManager;
    private RequestQueueSingleton requestQueueSingleton;
    private AddMemberRepositoryListener listener;

    public AddMemberRepository(Context context) {
        authenticationManager = AuthenticationManagerSingleton.getInstance(context);
        requestQueueSingleton = RequestQueueSingleton.getInstance(context);
    }

    public void addMember(Group group, User user) {
        callAPI(false, group, user);
    }

    public interface AddMemberRepositoryListener {
        void onResultDone();
    }

    public void setListener(AddMemberRepositoryListener listener) {
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
                        listener.onResultDone();
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
}
