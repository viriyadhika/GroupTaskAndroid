package com.example.grouptaskandroid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationManagerSingleton {

    public static final String TAG = "util.AuthenticationManagerSingleton";
    private static final String REFRESH = "refresh";
    private static final String ACCESS = "access";

    private static AuthenticationManagerSingleton instance;

    private Context context;

    private SharedPreferences preferences;

    //Auth States
    private int userId;
    private Map<String, String> credential;
    private MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>(false);
    private MutableLiveData<Exception> exceptionState = new MutableLiveData<>();

    private AuthenticationManagerSingleton(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        updateAuthState();
    }

    public static synchronized AuthenticationManagerSingleton getInstance(Context ctx) {
        if (instance == null) {
            instance = new AuthenticationManagerSingleton(ctx);
        }
        return instance;
    }

    // Get the four auth states
    public MutableLiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

    public Map<String, String> getCredential() {
        return credential;
    }

    public int getUserId() {
        return userId;
    }

    public MutableLiveData<Exception> getExceptionState() {
        return exceptionState;
    }

    public void login(final String username, String password) {
        String tokenUrl = Constants.url + "/api/token/";

        final JSONObject credentials = new JSONObject();

        try {
            credentials.put("username", username);
            credentials.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                tokenUrl,
                credentials,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        Map<String, String> responseMap = convertJSONToMap(response);
                        Map<String, String> credentials = new HashMap<>();
                        credentials.put("Authorization", "Bearer " + responseMap.get(ACCESS));

                        new ConvertUsernameToIdTask(
                                username,
                                context,
                                credentials,
                                new ConvertUsernameToIdTask.ConvertUsernameToIdCallback() {
                                    @Override
                                    public void callback(int id) {
                                        saveCredential(convertJSONToMap(response), id);
                                        updateAuthState();
                                    }
                                }
                        ).run();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String body = "";
                        //TODO: Username is wrong
                        if (error.networkResponse.statusCode == 401) {
                            handleLoginError(error);
                        }
                        if (error.networkResponse.data != null) {
                            body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        }
                        Log.d(TAG, "onErrorResponse: " + body);
                    }
                }
        );

        RequestQueueSingleton.getInstance(this.context).addToRequestQueue(jsonObjectRequest);
    }

    private static class ConvertUsernameToIdTask {

        protected ConvertUsernameToIdTask(String username,
                                          Context context,
                                          Map<String, String> credentials,
                                          ConvertUsernameToIdCallback callback) {
            this.username = username;
            this.context = context;
            this.credentials = credentials;
            this.convertUsernameToIdCallback = callback;
        }

        private String username;
        private Context context;
        private Map<String, String> credentials;

        protected interface ConvertUsernameToIdCallback {
            void callback(int userId);
        }

        protected ConvertUsernameToIdCallback convertUsernameToIdCallback;

        protected void run() {
            String convertIdUrl = Constants.url + "/users/" + username;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    convertIdUrl,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int userId = response.getInt("pk");
                                convertUsernameToIdCallback.callback(userId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error.networkResponse.data != null) {
                                Log.d(TAG, error.networkResponse.headers.toString());
                            }
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return credentials;
                }
            };

            RequestQueueSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
        }
    }

    private Map<String, String> convertJSONToMap(JSONObject jwtToken) {
        Map<String, String> result = new HashMap<>();
        try {
            result.put(ACCESS, jwtToken.getString(ACCESS));
            result.put(REFRESH, jwtToken.getString(REFRESH));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void saveCredential(Map<String, String> jwtToken, int id) {
        SharedPreferences.Editor preferenceEditor = preferences.edit();
        preferenceEditor.putInt(Constants.PREFS_USERID, id);
        preferenceEditor.putString(Constants.PREFS_ACCESS_TOKEN, jwtToken.get(ACCESS));
        preferenceEditor.putString(Constants.PREFS_REFRESH_TOKEN, jwtToken.get(REFRESH));
        preferenceEditor.apply();
    }

    private void saveRefreshCredential(JSONObject jwtToken) {
        SharedPreferences.Editor preferenceEditor = preferences.edit();
        try {
            String accessToken = jwtToken.getString(ACCESS);
            preferenceEditor.putString(Constants.PREFS_ACCESS_TOKEN, accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        preferenceEditor.apply();
    }

    private void updateAuthState() {
        isLoggedIn.setValue(hasCredentials());

        if (hasCredentials()) {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("Authorization", "Bearer " + preferences.getString(Constants.PREFS_ACCESS_TOKEN, "empty!"));
            this.credential = credentials;
            this.userId = preferences.getInt(Constants.PREFS_USERID, -1);
        }
    }

    //TODO: to implement proper credential checking
    private boolean hasCredentials() {
        return preferences.contains(Constants.PREFS_ACCESS_TOKEN);
    }

    public void handleLoginError(Exception e) {
        exceptionState.setValue(e);
    }

    public interface RefreshCallback {
        void refreshSuccessCallBack();
        void refreshFailCallBack();
    }

    public void refreshToken(final RefreshCallback refreshCallback) {
        String tokenUrl = Constants.url + "/api/token/refresh/";

        final JSONObject access = new JSONObject();

        try {
            access.put("refresh", preferences.getString(Constants.PREFS_REFRESH_TOKEN, "empty!"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                tokenUrl,
                access,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        saveRefreshCredential(response);
                        updateAuthState();
                        refreshCallback.refreshSuccessCallBack();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String body = "no network response";
                        if (error.networkResponse.statusCode == 401) {
                            handleLoginError(error);
                            refreshCallback.refreshFailCallBack();
                        }
                        if (error.networkResponse.data != null) {
                            body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        }
                        Log.d(TAG, " refresh error " + body);
                    }
                }
        );

        RequestQueueSingleton.getInstance(this.context).addToRequestQueue(jsonObjectRequest);
    }

    public void handleLogOut() {
        SharedPreferences.Editor preferenceEditor = preferences.edit();
        preferenceEditor.remove(Constants.PREFS_USERID);
        preferenceEditor.remove(Constants.PREFS_ACCESS_TOKEN);
        preferenceEditor.remove(Constants.PREFS_REFRESH_TOKEN);
        preferenceEditor.apply();
    }

}
