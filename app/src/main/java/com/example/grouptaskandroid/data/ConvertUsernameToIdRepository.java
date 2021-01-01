package com.example.grouptaskandroid.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.grouptaskandroid.data.generics.Repository;
import com.example.grouptaskandroid.exception.network.NoNetworkResponseException;
import com.example.grouptaskandroid.exception.network.NotFoundException;
import com.example.grouptaskandroid.model.User;
import com.example.grouptaskandroid.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ConvertUsernameToIdRepository extends Repository<User> {

    private String username;
    public static final String TAG = "ConvertUsernameToIdRepository";
    private ConvertUsernameToIdRepositoryListener listener;


    public interface ConvertUsernameToIdRepositoryListener {
        void onResultDone(User user);
    }

    public void setListener(ConvertUsernameToIdRepositoryListener listener) {
        this.listener = listener;
    }

    public ConvertUsernameToIdRepository(Context context) {
        super(context);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void refreshData() {
        callAPI(false);
    }

    @Override
    public void callAPI(final boolean isRetry) {
        String url = Constants.url + "/users/" + this.username;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            User user = new User(
                                    response.getInt("pk"),
                                    response.getString("username"),
                                    response.getString("email")
                            );
                            listener.onResultDone(user);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleError(TAG, error, isRetry);
                        if (error.networkResponse != null) {
                            Log.d(TAG, "onErrorResponse: " + new String(error.networkResponse.data, StandardCharsets.UTF_8));
                            if (error.networkResponse.statusCode == Constants.RESPONSE_NOT_FOUND) {
                                errorState.setValue(new NotFoundException("Username does not exist!"));
                            }
                        } else {
                            errorState.setValue(new NoNetworkResponseException(error));
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authenticationManagerSingleton.getCredential();
            }
        };
        requestQueueSingleton.addToRequestQueue(request);
    }
}
