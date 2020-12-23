package com.example.grouptaskandroid.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.grouptaskandroid.data.GroupDetailRepository;
import com.example.grouptaskandroid.model.Group;
import com.example.grouptaskandroid.model.GroupDetail;

public class GroupDetailViewModel extends AndroidViewModel {
    private GroupDetailRepository groupDetailRepository;
    private Group group;

    public GroupDetailViewModel(@NonNull Application application) {
        super(application);
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

}
