package com.example.grouptaskandroid.data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.grouptaskandroid.model.GroupSummary;
import com.example.grouptaskandroid.util.AuthenticationManager;
import com.example.grouptaskandroid.util.Constants;
import com.example.grouptaskandroid.util.RequestQueueSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyGroupRepository {
    private Context context;
    public static final String TAG = "MyGroupRepository";

    private AuthenticationManager authenticationManager;
    private RequestQueueSingleton requestQueueSingleton;

    private MutableLiveData<List<GroupSummary>> groups = new MutableLiveData<>();

    private MutableLiveData<VolleyError> errorState = new MutableLiveData<>();

    public MyGroupRepository(Context context) {
        this.context = context;

        authenticationManager = new AuthenticationManager(context);
        requestQueueSingleton = RequestQueueSingleton.getInstance(context);
        if (authenticationManager.getIsLoggedIn().getValue()) {
            retrieveGroupData(false);
        }
    }

    public MutableLiveData<List<GroupSummary>> getGroups() {
        return groups;
    }

    public MutableLiveData<VolleyError> getErrorState() { return errorState; }

    private void retrieveGroupData(final boolean isRetry) {

        String tokenUrl = Constants.url + "/users/" + authenticationManager.getUserId() + "/groups";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                tokenUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray myGroups = response.getJSONArray("my_groups");
                            List<GroupSummary> groupList = new ArrayList<>();
                            for (int i = 0; i < myGroups.length(); i++) {
                                JSONObject group = myGroups.getJSONObject(i);
                                groupList.add(
                                        new GroupSummary(
                                                group.getInt("pk"),
                                                group.getString("name")
                                        )
                                );
                            }
                            groups.setValue(groupList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + new String(error.networkResponse.data, StandardCharsets.UTF_8));
                        if (error.networkResponse.statusCode == 401) {
                            authenticationManager.refreshToken(
                                    new AuthenticationManager.RefreshCallback() {
                                        @Override
                                        public void refreshSuccessCallBack() {
                                            if (!isRetry) {
                                                retrieveGroupData(true);
                                            }
                                        }

                                        @Override
                                        public void refreshFailCallBack() {
                                            errorState.setValue(error);
                                        }
                                    }
                            );
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authenticationManager.getCredential();
            }
        };
        requestQueueSingleton.addToRequestQueue(jsonObjectRequest);
    }

}
