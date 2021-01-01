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
import com.example.grouptaskandroid.model.Task;
import com.example.grouptaskandroid.model.User;
import com.example.grouptaskandroid.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class TaskRepository extends PostRepository<Task> {
    public static final String TAG = "TaskRepository";

    public TaskRepository(Context context) {
        super(context);
    }

    public void createTask(String name, String desc, int groupId, int inChargeId, String dueDate) {
        Task task = new Task(name, desc, new Group(groupId), new User(inChargeId), dueDate, false);
        callAPI(false, task);
    }

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
                        handleError(TAG, error, isRetry, task);
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
