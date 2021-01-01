package com.example.grouptaskandroid.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.grouptaskandroid.data.generics.PostUpdateDeleteRepository;
import com.example.grouptaskandroid.model.Group;
import com.example.grouptaskandroid.model.Task;
import com.example.grouptaskandroid.model.User;
import com.example.grouptaskandroid.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class TaskPostUpdateDeleteRepository extends PostUpdateDeleteRepository<Task> {
    public static final String TAG = "TaskPostUpdateDeleteRepository";

    public TaskPostUpdateDeleteRepository(Context context) {
        super(context);
    }

    public void createTask(String name, String desc, int groupId, int inChargeId, String dueDate) {
        Task task = new Task(name, desc, new Group(groupId), new User(inChargeId), dueDate, false);
        callAPI(false, task);
    }

    @Override
    public void callAPI(final boolean isRetry, final Task task) {
        String url = Constants.url + "/tasks";
        JSONObject body = new JSONObject();
        try {
            body.put("name", task.getName());
            body.put("desc", task.getDesc());
            body.put("group", task.getGroup().getPk());
            body.put("in_charge", task.getInCharge().getPk());
            body.put("due_date", task.getDueDate());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response);
                        listener.onPostSuccess();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Log.d(TAG, "onResponse: " + error);
                        handlePostError(TAG, error, isRetry, task);
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

    public void deleteTask(Task task) {
        deleteTaskCallAPI(true, task);
    }

    public void deleteTaskCallAPI(boolean isRetry, Task task) {
        String url = Constants.url + "/tasks/" + task.getPk() ;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response);
                        listener.onDeleteSuccess();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.d(TAG, "getHeaders: " + authenticationManagerSingleton.getCredential());
                return authenticationManagerSingleton.getCredential();
            }
        };
        requestQueueSingleton.addToRequestQueue(request);
    }

}
