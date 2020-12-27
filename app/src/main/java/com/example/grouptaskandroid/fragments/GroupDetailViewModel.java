package com.example.grouptaskandroid.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.grouptaskandroid.data.GroupDetailRepository;
import com.example.grouptaskandroid.data.TaskRepository;
import com.example.grouptaskandroid.data.generics.PostRepository;
import com.example.grouptaskandroid.model.Group;
import com.example.grouptaskandroid.model.GroupDetail;
import com.example.grouptaskandroid.model.User;
import com.example.grouptaskandroid.util.DateTimeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDetailViewModel extends AndroidViewModel {
    private GroupDetailRepository groupDetailRepository;
    private Group group;

    private Map<String, User> usernameToPkMap = new HashMap<>();
    private AddTaskDialogListener listener;
    private TaskRepository taskRepository;

    public GroupDetailViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(getApplication());
        taskRepository.setPostRepositoryListener(new PostRepository.PostRepositoryListener() {
            @Override
            public void onPostSuccess() {
                groupDetailRepository.refreshData();
            }
        });
    }

    public void setGroup(Group group) {
        this.group = group;
        groupDetailRepository = new GroupDetailRepository(getApplication(), group);
    }

    public MutableLiveData<GroupDetail> getGroup() {
        return groupDetailRepository.getData();
    }

    public MutableLiveData<Exception> getError() {
        return groupDetailRepository.getErrorState();
    }

    public void refreshData() {
        groupDetailRepository.refreshData();
    }

    public List<String> getMemberNames(List<User> members) {
        List<String> usernameList = new ArrayList<>();
        for (User member : members) {
            usernameList.add(member.getUsername());
            usernameToPkMap.put(member.getUsername(), member);
        }

        return usernameList;
    }

    public interface AddTaskDialogListener {
        void onDateFormatError();
    }

    public void setAddTaskDialogListener(AddTaskDialogListener listener) {
        this.listener = listener;
    }

    public void saveTask(String name,
                         String desc,
                         int groupId,
                         String inCharge,
                         String dueDate) {
        if (DateTimeHandler.verifyDate(dueDate)) {
            User user = usernameToPkMap.get(inCharge);
            taskRepository.createTask(
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
