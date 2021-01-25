package com.example.grouptaskandroid.data;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.grouptaskandroid.data.generics.Repository;
import com.example.grouptaskandroid.model.Group;
import com.example.grouptaskandroid.model.GroupDetail;
import com.example.grouptaskandroid.model.Task;
import com.example.grouptaskandroid.model.User;
import com.example.grouptaskandroid.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupDetailRepository extends Repository<GroupDetail> {

    private static final String TAG = "GroupDetailRepository";
    private Group group;

    public GroupDetailRepository(Context context) {
        super(context);
    }

    public void retrieveGroupData(Group group) {
        this.group = group;
        refreshData();
    }

    @Override
    public void refreshData() {
        if (authenticationManagerSingleton.getIsLoggedIn().getValue()) {
            callAPI(false);
        }
    }

    @Override
    public void callAPI(final boolean isRetry) {
        String url = Constants.url + "/groups/" + group.getPk();

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
                                JSONObject taskGroupJSON = task.getJSONObject("group");
                                Group taskGroup = new Group(
                                        taskGroupJSON.getInt("pk"),
                                        taskGroupJSON.getString("name")
                                );
                                JSONObject inCharge = task.getJSONObject("in_charge");
                                User userInCharge = new User(
                                        inCharge.getInt("pk"),
                                        inCharge.getString("username"),
                                        inCharge.getString("email")
                                );
                                taskList.add(
                                        new Task(
                                                task.getInt("pk"),
                                                task.getString("name"),
                                                task.getString("desc"),
                                                taskGroup,
                                                userInCharge,
                                                task.getString("due_date"),
                                                task.getBoolean("is_done")
                                        )
                                );
                            }
                            JSONArray members = response.getJSONArray("members");
                            List<User> memberList = new ArrayList<>();
                            for (int i = 0; i < members.length(); i++) {
                                JSONObject memberJSON = members.getJSONObject(i);
                                User member = new User(
                                        memberJSON.getInt("pk"),
                                        memberJSON.getString("username"),
                                        memberJSON.getString("email")
                                );
                                memberList.add(member);
                            }

                            data.setValue(new GroupDetail(pk, groupName, taskList, memberList));
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
