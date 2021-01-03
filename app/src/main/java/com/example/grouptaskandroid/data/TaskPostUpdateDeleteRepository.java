package com.example.grouptaskandroid.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.grouptaskandroid.data.generics.PostUpdateDeleteRepository;
import com.example.grouptaskandroid.exception.network.AuthenticationFailedException;
import com.example.grouptaskandroid.model.Group;
import com.example.grouptaskandroid.model.Task;
import com.example.grouptaskandroid.model.User;
import com.example.grouptaskandroid.util.AuthenticationManagerSingleton;
import com.example.grouptaskandroid.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
        deleteTaskCallAPI(false, task);
    }

    public void deleteTaskCallAPI(final boolean isRetry, final Task task) {
        String url = Constants.url + "/tasks/" + task.getPk();
        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onDeleteSuccess();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error);
                        if (error.networkResponse != null) {
                            if (!isRetry) {
                                authenticationManagerSingleton.refreshToken(
                                        new AuthenticationManagerSingleton.RefreshCallback() {
                                            @Override
                                            public void refreshSuccessCallBack() {
                                                deleteTaskCallAPI(true, task);
                                            }

                                            @Override
                                            public void refreshFailCallBack() {
                                                errorState.setValue(new AuthenticationFailedException(error));
                                            }
                                        }
                                );
                            } else {
                                errorState.setValue(new AuthenticationFailedException(error));
                            }
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authenticationManagerSingleton.getCredential();
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                    JSONObject result = null;

                    if (jsonString != null && jsonString.length() > 0)
                        result = new JSONObject(jsonString);

                    return Response.success(result,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };
        requestQueueSingleton.addToRequestQueue(request);
    }

}
