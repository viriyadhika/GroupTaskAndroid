package com.example.grouptaskandroid.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.VolleyError;
import com.example.grouptaskandroid.data.MyTaskRepository;
import com.example.grouptaskandroid.model.Task;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    MyTaskRepository myTaskRepository;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        myTaskRepository = new MyTaskRepository(application);
    }

    public MutableLiveData<List<Task>> getTask() {
        return myTaskRepository.getTasks();
    }

    public MutableLiveData<VolleyError> getError() {
        return myTaskRepository.getErrorState();
    }

}
