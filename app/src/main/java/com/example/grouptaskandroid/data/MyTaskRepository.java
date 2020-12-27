package com.example.grouptaskandroid.data;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.grouptaskandroid.data.generics.Repository;
import com.example.grouptaskandroid.model.Group;
import com.example.grouptaskandroid.model.Task;
import com.example.grouptaskandroid.model.User;
import com.example.grouptaskandroid.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyTaskRepository extends Repository<List<Task>> {

    public static final String TAG = "MyTaskRepository";

    public MyTaskRepository(Context context) {
        super(context);
        refreshData();
    }

    @Override
    public void callAPI(final boolean isRetry) {
        String tokenUrl = Constants.url + "/users/" + authenticationManagerSingleton.getUserId() + "/tasks";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                tokenUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray myTasks = response.getJSONArray("my_tasks");
                            List<Task> taskList = new ArrayList<>();
                            for (int i = 0; i < myTasks.length(); i++) {
                                JSONObject taskJSON = myTasks.getJSONObject(i);
                                JSONObject taskGroupJSON = taskJSON.getJSONObject("group");
                                Group taskGroup = new Group(
                                        taskGroupJSON.getInt("pk"),
                                        taskGroupJSON.getString("name")
                                );
                                JSONObject inCharge = taskJSON.getJSONObject("in_charge");
                                User userInCharge = new User(
                                        inCharge.getInt("pk"),
                                        inCharge.getString("username"),
                                        inCharge.getString("email")
                                );
                                Task task = new Task(
                                        taskJSON.getInt("pk"),
                                        taskJSON.getString("name"),
                                        taskJSON.getString("desc"),
                                        taskGroup,
                                        userInCharge,
                                        taskJSON.getString("due_date"),
                                        taskJSON.getBoolean("is_done")
                                );
                                taskList.add(task);
                            }
                            data.setValue(taskList);
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
        data.setValue(new ArrayList<Task>());
        if (authenticationManagerSingleton.getIsLoggedIn().getValue()) {
            callAPI(false);
        }
    }
}
