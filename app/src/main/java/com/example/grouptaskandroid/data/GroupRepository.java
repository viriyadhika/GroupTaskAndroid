package com.example.grouptaskandroid.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.grouptaskandroid.model.Group;
import com.example.grouptaskandroid.model.GroupSummary;
import com.example.grouptaskandroid.model.Task;
import com.example.grouptaskandroid.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupRepository extends GenericRepository<Group> {

    private static final String TAG = "GroupRepository";
    private int groupId;

    public GroupRepository(Context context, int groupId) {
        super(context);
        this.groupId = groupId;
        refreshData();
    }

    @Override
    public void refreshData() {
        if (authenticationManagerSingleton.getIsLoggedIn().getValue()) {
            retrieveData(false);
        }
    }

    @Override
    public void retrieveData(final boolean isRetry) {
        String url = Constants.url + "/groups/" + groupId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int pk = response.getInt("pk");
                            String groupName = response.getString("name");
                            JSONArray tasks = response.getJSONArray("group_tasks");
                            List<Task> taskList = new ArrayList<>();
                            for (int i = 0; i < tasks.length(); i++) {
                                JSONObject task = tasks.getJSONObject(i);
                                JSONObject taskGroup = task.getJSONObject("group");
                                GroupSummary groupSummary = new GroupSummary(
                                        taskGroup.getInt("pk"),
                                        taskGroup.getString("name")
                                );
                                taskList.add(
                                        new Task(
                                                task.getInt("pk"),
                                                task.getString("name"),
                                                task.getString("desc"),
                                                groupSummary
                                        )
                                );
                            }
                            data.setValue(new Group(pk, groupName, taskList));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleError(TAG, error, isRetry);
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authenticationManagerSingleton.getCredential();
            }
        };

        requestQueueSingleton.addToRequestQueue(jsonObjectRequest);
    }
}
