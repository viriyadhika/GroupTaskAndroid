package com.example.grouptaskandroid.data.generics;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.VolleyError;
import com.example.grouptaskandroid.exception.AuthenticationFailedException;
import com.example.grouptaskandroid.model.Task;
import com.example.grouptaskandroid.util.AuthenticationManagerSingleton;
import com.example.grouptaskandroid.util.Constants;
import com.example.grouptaskandroid.util.RequestQueueSingleton;

import java.nio.charset.StandardCharsets;

public abstract class PostRepository<T>  implements IGenericPostRepository<T> {
    protected AuthenticationManagerSingleton authenticationManagerSingleton;
    protected RequestQueueSingleton requestQueueSingleton;

    protected MutableLiveData<Exception> errorState;

    public PostRepository(Context context) {
        authenticationManagerSingleton = AuthenticationManagerSingleton.getInstance(context);
        requestQueueSingleton = RequestQueueSingleton.getInstance(context);
    }

    public void handleError(String TAG, final VolleyError error, final boolean isRetry, final T data) {
        if (error.networkResponse != null) {
            if (error.networkResponse.statusCode == Constants.RESPONSE_NOT_AUTHENTICATED) {
                if (!isRetry) {
                    authenticationManagerSingleton.refreshToken(new AuthenticationManagerSingleton.RefreshCallback() {
                        @Override
                        public void refreshSuccessCallBack() {
                            callAPI(true, data);
                        }

                        @Override
                        public void refreshFailCallBack() {
                            errorState.setValue(new AuthenticationFailedException(error));
                        }
                    });
                } else {
                    errorState.setValue(new AuthenticationFailedException(error));
                }
            }
            Log.d(TAG, "onErrorResponse: " + new String(error.networkResponse.data, StandardCharsets.UTF_8));
        }
    }
}