package com.example.grouptaskandroid.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.grouptaskandroid.util.AuthenticationManagerSingleton;

public class MainViewModel extends AndroidViewModel {

    private AuthenticationManagerSingleton authenticationManagerSingleton;

    public MainViewModel(@NonNull Application application) {
        super(application);
        authenticationManagerSingleton = AuthenticationManagerSingleton.getInstance(application);
    }

    public void logout() {
        authenticationManagerSingleton.handleLogOut();
    }

}
