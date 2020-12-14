package com.example.grouptaskandroid.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.grouptaskandroid.data.GroupDetailRepository;
import com.example.grouptaskandroid.model.GroupDetail;

public class GroupDetailViewModel extends AndroidViewModel {
    private GroupDetailRepository groupDetailRepository;
    private int groupId;

    public GroupDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
        groupDetailRepository = new GroupDetailRepository(getApplication(), groupId);
    }

    public MutableLiveData<GroupDetail> getGroup() {
        return groupDetailRepository.getData();
    }

}
