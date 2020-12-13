package com.example.grouptaskandroid.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.grouptaskandroid.exception.RequiredFieldEmptyException;
import com.example.grouptaskandroid.util.AuthenticationManagerSingleton;

public class LoginViewModel extends AndroidViewModel {

    private AuthenticationManagerSingleton authenticationManagerSingleton = AuthenticationManagerSingleton.getInstance(getApplication());

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void login(String username, String password) throws RequiredFieldEmptyException{
        if (username.equals("") || password.equals("")) {
            throw new RequiredFieldEmptyException();
        }
        authenticationManagerSingleton.login(username, password);
    }

    public MutableLiveData<Boolean> getIsLoggedIn() {
        return authenticationManagerSingleton.getIsLoggedIn();
    }

    public MutableLiveData<Exception> getExceptionState() {
        return authenticationManagerSingleton.getExceptionState();
    }

}
