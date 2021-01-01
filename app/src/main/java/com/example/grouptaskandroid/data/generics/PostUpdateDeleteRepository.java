package com.example.grouptaskandroid.data.generics;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.VolleyError;
import com.example.grouptaskandroid.exception.network.AuthenticationFailedException;
import com.example.grouptaskandroid.util.AuthenticationManagerSingleton;
import com.example.grouptaskandroid.util.Constants;
import com.example.grouptaskandroid.util.RequestQueueSingleton;

import java.nio.charset.StandardCharsets;

public abstract class PostUpdateDeleteRepository<T>  implements IGenericPostRepository<T> {
    protected AuthenticationManagerSingleton authenticationManagerSingleton;
    protected RequestQueueSingleton requestQueueSingleton;

    protected Listener listener;
    protected MutableLiveData<Exception> errorState = new MutableLiveData<>();

    public PostUpdateDeleteRepository(Context context) {
        authenticationManagerSingleton = AuthenticationManagerSingleton.getInstance(context);
        requestQueueSingleton = RequestQueueSingleton.getInstance(context);
    }

    public MutableLiveData<Exception> getErrorState() {
        return errorState;
    }

    public interface Listener {
        void onPostSuccess();
        void onDeleteSuccess();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void handlePostError(String TAG, final VolleyError error, final boolean isRetry, final T data) {
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

    public void handleDeleteError(String TAG, final VolleyError error, final boolean isRetry, final T data){
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
