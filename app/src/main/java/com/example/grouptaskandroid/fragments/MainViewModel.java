package com.example.grouptaskandroid.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.grouptaskandroid.util.AuthenticationManager;

public class MainViewModel extends AndroidViewModel {

    private AuthenticationManager authenticationManager;

    public MainViewModel(@NonNull Application application) {
        super(application);
        authenticationManager = new AuthenticationManager(application);
    }

    public void logout() {
        authenticationManager.handleLogOut();
    }

}
