package com.example.grouptaskandroid.fragments;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.grouptaskandroid.data.GroupDetailRepository;
import com.example.grouptaskandroid.data.MyTaskRepository;
import com.example.grouptaskandroid.data.TaskRepository;
import com.example.grouptaskandroid.model.Task;
import com.example.grouptaskandroid.model.User;
import com.example.grouptaskandroid.util.DateTimeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTaskDialogViewModel extends AndroidViewModel {

    public static final String TAG = "AddTaskDialogViewModel";

    private Map<String, User> usernameToPkMap = new HashMap<>();
    private AddTaskDialogListener listener;
    private TaskRepository repository;

    public AddTaskDialogViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
    }

    public interface AddTaskDialogListener {
        void onDateFormatError();
    }

    public void setAddTaskDialogListener(AddTaskDialogListener listener) {
        this.listener = listener;
    }

    public List<String> getMemberNames(List<User> members) {
        List<String> usernameList = new ArrayList<>();
        for (User member : members) {
            usernameList.add(member.getUsername());
            usernameToPkMap.put(member.getUsername(), member);
        }

        return usernameList;
    }

    public void saveTask(String name,
                         String desc,
                         int groupId,
                         String inCharge,
                         String dueDate) {
        if (DateTimeHandler.verifyDate(dueDate)) {
            User user = usernameToPkMap.get(inCharge);
            //TODO: create the task and calls create task
            repository.createTask(
                    name,
                    desc,
                    groupId,
                    user.getPk(),
                    dueDate
            );
        } else {
            listener.onDateFormatError();
        }
    }

    public void verifyDate(String date) {
        if (!DateTimeHandler.verifyDate(date)) {
            listener.onDateFormatError();
        }
    }

}
