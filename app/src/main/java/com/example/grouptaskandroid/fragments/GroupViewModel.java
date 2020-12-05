package com.example.grouptaskandroid.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.VolleyError;
import com.example.grouptaskandroid.data.MyGroupRepository;
import com.example.grouptaskandroid.model.GroupSummary;

import java.util.List;

public class GroupViewModel extends AndroidViewModel {

    public static final int DEFAULT_SELECTED_PK = -1;
    private MyGroupRepository myGroupRepository;
    private MutableLiveData<Integer> selectedGroupPk = new MutableLiveData<>(DEFAULT_SELECTED_PK);

    public GroupViewModel(@NonNull Application application) {
        super(application);
        myGroupRepository = new MyGroupRepository(application);
    }

    public MutableLiveData<List<GroupSummary>> getMyGroups() {
        return myGroupRepository.getGroups();
    }

    public MutableLiveData<VolleyError> getError() {
        return myGroupRepository.getErrorState();
    }

    public MutableLiveData<Integer> getSelectedGroupPk() {
        return selectedGroupPk;
    }

    public void setSelectedGroupPk(int pk) {
        selectedGroupPk.setValue(pk);
    }
}
