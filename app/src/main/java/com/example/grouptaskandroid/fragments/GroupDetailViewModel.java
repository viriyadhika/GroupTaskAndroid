package com.example.grouptaskandroid.fragments;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.grouptaskandroid.data.AddRemoveMemberRepository;
import com.example.grouptaskandroid.data.GroupDetailRepository;
import com.example.grouptaskandroid.data.TaskPostUpdateDeleteRepository;
import com.example.grouptaskandroid.data.ConvertUsernameToIdRepository;
import com.example.grouptaskandroid.data.generics.PostUpdateDeleteRepository;
import com.example.grouptaskandroid.model.Group;
import com.example.grouptaskandroid.model.GroupDetail;
import com.example.grouptaskandroid.model.Task;
import com.example.grouptaskandroid.model.User;
import com.example.grouptaskandroid.util.DateTimeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDetailViewModel extends AndroidViewModel {
    public static final String TAG = "GroupDetailViewModel";

    private GroupDetailRepository groupDetailRepository;
    private Group group;

    private Map<String, User> usernameToPkMap = new HashMap<>();
    private AddTaskDialogListener listener;
    private TaskPostUpdateDeleteRepository taskPostUpdateDeleteRepository;
    private AddRemoveMemberRepository addRemoveMemberRepository;
    private ConvertUsernameToIdRepository convertUsernameToIdRepository;

    private ErrorState errorState;

    public static class ErrorState {
        MutableLiveData<Exception> groupDetailError;
        MutableLiveData<Exception> addRemoveMemberError;
        public ErrorState(
            MutableLiveData<Exception> groupDetailError,
            MutableLiveData<Exception> addRemoveMemberError) {
            this.groupDetailError = groupDetailError;
            this.addRemoveMemberError = addRemoveMemberError;
        }

        public MutableLiveData<Exception> getGroupDetailError() {
            return groupDetailError;
        }

        public MutableLiveData<Exception> getAddRemoveMemberError() {
            return addRemoveMemberError;
        }
    }

    public GroupDetailViewModel(@NonNull Application application) {
        super(application);
        taskPostUpdateDeleteRepository = new TaskPostUpdateDeleteRepository(application);
        taskPostUpdateDeleteRepository.setListener(new PostUpdateDeleteRepository.Listener() {
            @Override
            public void onPostSuccess() {
                refreshData();
            }

            @Override
            public void onDeleteSuccess() {
                refreshData();
            }
        });
        addRemoveMemberRepository = new AddRemoveMemberRepository(application);
        convertUsernameToIdRepository = new ConvertUsernameToIdRepository(getApplication());
        groupDetailRepository = new GroupDetailRepository(getApplication());
        errorState = new ErrorState(
                groupDetailRepository.getErrorState(),
                addRemoveMemberRepository.getErrorState()
        );
    }

    public void setGroup(Group group) {
        this.group = group;
        groupDetailRepository.retrieveGroupData(group);

    }

    public MutableLiveData<GroupDetail> getGroup() {
        return groupDetailRepository.getData();
    }

    public ErrorState getError() {
        return errorState;
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

    public void deleteTask(Task task) {
        taskPostUpdateDeleteRepository.deleteTask(task);
    }

    // Add Task Dialog
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
            taskPostUpdateDeleteRepository.createTask(
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

    // Add a person to a group
    public void addPersonToGroup(String username) {
        findUsernameThenAddToGroup(username);
    }

    private void findUsernameThenAddToGroup(String username) {
        convertUsernameToIdRepository.setUsername(username);
        convertUsernameToIdRepository.setListener(new ConvertUsernameToIdRepository.ConvertUsernameToIdRepositoryListener() {
            @Override
            public void onResultDone(User user) {
                addPersonToGroup(user);
            }
        });
        convertUsernameToIdRepository.refreshData();
    }

    public MutableLiveData<Exception> getErrorAddMemberToGroup() {
        return convertUsernameToIdRepository.getErrorState();
    }

    private void addPersonToGroup(User user) {
        addRemoveMemberRepository.setListener(new AddRemoveMemberRepository.Listener() {
            @Override
            public void onAddMemberDone() {
                groupDetailRepository.refreshData();
            }

            @Override
            public void onRemoveMemberDone() {
                groupDetailRepository.refreshData();
            }

        });
        addRemoveMemberRepository.addMember(group, user);
    }

    //Remove person from a group
    public void removePersonFromGroup(User user) {
        addRemoveMemberRepository.removeMember(group, user);
    }
}
