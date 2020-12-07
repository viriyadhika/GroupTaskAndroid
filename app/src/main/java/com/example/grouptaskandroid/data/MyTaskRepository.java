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
import com.example.grouptaskandroid.model.Task;
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

public class MyTaskRepository {
    private Context context;
    public static final String TAG = "MyTaskRepository";

    private AuthenticationManager authenticationManager;
    private RequestQueueSingleton requestQueueSingleton;

    private MutableLiveData<List<Task>> tasks = new MutableLiveData<>();

    private MutableLiveData<VolleyError> errorState = new MutableLiveData<>();

    public MyTaskRepository(Context context) {
        this.context = context;

        authenticationManager = new AuthenticationManager(context);
        requestQueueSingleton = RequestQueueSingleton.getInstance(context);
        if (authenticationManager.getIsLoggedIn().getValue()) {
            retrieveTaskData(false);
        }
    }

    public MutableLiveData<List<Task>> getTasks() {
        return tasks;
    }

    public MutableLiveData<VolleyError> getErrorState() { return errorState; }

    private void retrieveTaskData(final boolean isRetry) {

        String tokenUrl = Constants.url + "/users/" + authenticationManager.getUserId() + "/tasks";
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
                                JSONObject task = myTasks.getJSONObject(i);
                                JSONObject taskGroupJSON = task.getJSONObject("group");
                                GroupSummary taskGroup = new GroupSummary(
                                        taskGroupJSON.getInt("pk"),
                                        taskGroupJSON.getString("name")
                                );
                                taskList.add(
                                        new Task(
                                                task.getInt("pk"),
                                                task.getString("name"),
                                                task.getString("desc"),
                                                taskGroup
                                        )
                                );
                            }
                            tasks.setValue(taskList);
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
                                                retrieveTaskData(true);
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
