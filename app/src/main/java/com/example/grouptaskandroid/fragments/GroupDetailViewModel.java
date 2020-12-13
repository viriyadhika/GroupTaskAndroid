package com.example.grouptaskandroid.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.grouptaskandroid.data.GroupRepository;
import com.example.grouptaskandroid.model.Group;

public class GroupDetailViewModel extends AndroidViewModel {
    private GroupRepository groupRepository;
    private int groupId;

    public GroupDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
        groupRepository = new GroupRepository(getApplication(), groupId);
    }

    public MutableLiveData<Group> getGroup() {
        return groupRepository.getData();
    }

}
