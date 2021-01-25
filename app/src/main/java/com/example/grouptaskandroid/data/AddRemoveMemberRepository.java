package com.example.grouptaskandroid.data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.grouptaskandroid.exception.network.AuthenticationFailedException;
import com.example.grouptaskandroid.exception.network.BadRequestException;
import com.example.grouptaskandroid.exception.network.NoNetworkResponseException;
import com.example.grouptaskandroid.model.Group;
import com.example.grouptaskandroid.model.User;
import com.example.grouptaskandroid.util.AuthenticationManagerSingleton;
import com.example.grouptaskandroid.util.Constants;
import com.example.grouptaskandroid.util.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class AddRemoveMemberRepository {

    public static final String TAG = "AddRemoveMemberRepository";
    private AuthenticationManagerSingleton authenticationManagerSingleton;
    private RequestQueueSingleton requestQueueSingleton;
    private Listener listener;
    private MutableLiveData<Exception> errorState = new MutableLiveData<>();

    public AddRemoveMemberRepository(Context context) {
        authenticationManagerSingleton = AuthenticationManagerSingleton.getInstance(context);
        requestQueueSingleton = RequestQueueSingleton.getInstance(context);
    }

    public void addMember(Group group, User user) {
        callAPI(false, group, user);
    }

    public interface Listener {
        void onAddMemberDone();
        void onRemoveMemberDone();
    }

    public MutableLiveData<Exception> getErrorState() {
        return errorState;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void callAPI(boolean isRetry, Group group, User user) {
        String url = Constants.url + "/groups/" + group.getPk() + "/users/" + user.getPk();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: ");
                        listener.onAddMemberDone();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error);
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

    public void removeMember(Group group, User user) {
        callAPIRemoveMember(false, group, user);
    }

    public void callAPIRemoveMember(final boolean isRetry, final Group group, final User user) {
        String url = Constants.url + "/groups/" + group.getPk() + "/users/" + user.getPk();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onRemoveMemberDone();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error);
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode == Constants.RESPONSE_NOT_AUTHENTICATED) {
                                if (!isRetry) {
                                    authenticationManagerSingleton.refreshToken(
                                            new AuthenticationManagerSingleton.RefreshCallback() {
                                                @Override
                                                public void refreshSuccessCallBack() {
                                                    callAPIRemoveMember(true, group, user);
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

                            if (error.networkResponse.statusCode == Constants.RESPONSE_BAD_REQUEST) {
                                errorState.setValue(new BadRequestException(error));
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
