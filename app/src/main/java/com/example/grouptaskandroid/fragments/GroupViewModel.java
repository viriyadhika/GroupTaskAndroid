package com.example.grouptaskandroid.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.grouptaskandroid.data.MyGroupRepository;
import com.example.grouptaskandroid.model.Group;

import java.util.List;

public class GroupViewModel extends AndroidViewModel {

    public static final Group DEFAULT_SELECTED_GROUP = new Group(-1, "Sample Group");
    private MyGroupRepository myGroupRepository;
    private MutableLiveData<Group> selectedGroup = new MutableLiveData<>(DEFAULT_SELECTED_GROUP);

    public GroupViewModel(@NonNull Application application) {
        super(application);
        myGroupRepository = new MyGroupRepository(application);
    }

    public MutableLiveData<List<Group>> getMyGroups() {
        return myGroupRepository.getData();
    }

    public MutableLiveData<Exception> getError() {
        return myGroupRepository.getErrorState();
    }

    public MutableLiveData<Group> getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(Group group) {
        selectedGroup.setValue(group);
    }

    public void refreshData() {
        myGroupRepository.refreshData();
    }

}
