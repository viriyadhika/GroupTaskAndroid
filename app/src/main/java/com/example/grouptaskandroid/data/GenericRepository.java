package com.example.grouptaskandroid.data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.VolleyError;
import com.example.grouptaskandroid.exception.AuthenticationFailedException;
import com.example.grouptaskandroid.exception.NoNetworkResponseException;
import com.example.grouptaskandroid.util.AuthenticationManagerSingleton;
import com.example.grouptaskandroid.util.RequestQueueSingleton;

import java.nio.charset.StandardCharsets;

public abstract class GenericRepository<T> implements IGenericRepository{
    protected Context context;
    protected AuthenticationManagerSingleton authenticationManagerSingleton;
    protected RequestQueueSingleton requestQueueSingleton;

    protected MutableLiveData<T> data = new MutableLiveData<>();

    protected MutableLiveData<Exception> errorState = new MutableLiveData<>();

    public GenericRepository(Context context) {
        this.context = context;

        authenticationManagerSingleton = AuthenticationManagerSingleton.getInstance(context);
        requestQueueSingleton = RequestQueueSingleton.getInstance(context);
    }

    public MutableLiveData<T> getData() {
        return data;
    }

    public MutableLiveData<Exception> getErrorState() {
        return errorState;
    }

    public void handleError(String TAG, final VolleyError error, final boolean isRetry) {
        Log.d(TAG, "onErrorResponse: " + error);
        if (error.networkResponse != null) {
            Log.d(TAG, "onErrorResponse: " + new String(error.networkResponse.data, StandardCharsets.UTF_8));
            if (error.networkResponse.statusCode == 401) {
                authenticationManagerSingleton.refreshToken(
                        new AuthenticationManagerSingleton.RefreshCallback() {
                            @Override
                            public void refreshSuccessCallBack() {
                                if (!isRetry) {
                                    retrieveData(true);
                                }
                            }

                            @Override
                            public void refreshFailCallBack() {
                                errorState.setValue(new AuthenticationFailedException(error));
                            }
                        }
                );
            }
        } else {
            errorState.setValue(new NoNetworkResponseException(error));
        }
    }


}
