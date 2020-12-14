package com.example.grouptaskandroid.data;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.grouptaskandroid.model.Group;
import com.example.grouptaskandroid.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyGroupRepository extends GenericRepository<List<Group>>{

    public static final String TAG = "MyGroupRepository";

    public MyGroupRepository(Context context) {
        super(context);
        refreshData();
    }

    @Override
    public void retrieveData(final boolean isRetry) {
        String tokenUrl = Constants.url + "/users/" + authenticationManagerSingleton.getUserId() + "/groups";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                tokenUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray myGroups = response.getJSONArray("my_groups");
                            List<Group> groupList = new ArrayList<>();
                            for (int i = 0; i < myGroups.length(); i++) {
                                JSONObject group = myGroups.getJSONObject(i);
                                groupList.add(
                                        new Group(
                                                group.getInt("pk"),
                                                group.getString("name")
                                        )
                                );
                            }
                            data.setValue(groupList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        handleError(TAG, error, isRetry);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authenticationManagerSingleton.getCredential();
            }
        };
        requestQueueSingleton.addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void refreshData() {
        data.setValue(new ArrayList<Group>());
        if (authenticationManagerSingleton.getIsLoggedIn().getValue()) {
            retrieveData(false);
        }
    }
}
