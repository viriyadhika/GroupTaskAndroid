package com.example.grouptaskandroid.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.example.grouptaskandroid.R;
import com.example.grouptaskandroid.adapter.TaskRecycleViewAdapter;
import com.example.grouptaskandroid.model.Group;
import com.example.grouptaskandroid.model.GroupDetail;
import com.example.grouptaskandroid.model.Task;
import com.example.grouptaskandroid.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private GroupDetailViewModel groupDetailViewModel;
    private TaskRecycleViewAdapter recycleViewAdapter;
    private RecyclerView recyclerView;
    private Button memberButton;
    private FloatingActionButton fab;

    private Spinner inChargeSpinner;

    public static final String TAG = "GroupDetailFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroupDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupDetailFragment newInstance(String param1, String param2) {
        GroupDetailFragment fragment = new GroupDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_detail, container, false);

        memberButton = v.findViewById(R.id.group_detail_memberButton);
        fab = v.findViewById(R.id.group_detail_fab);

        recyclerView = v.findViewById(R.id.group_detail_recyclerView);
        recycleViewAdapter = new TaskRecycleViewAdapter();
        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleViewAdapter.setListener(new TaskRecycleViewAdapter.Listener() {
            @Override
            public void onDeleteTask(Task task) {
                groupDetailViewModel.deleteTask(task);
            }
        });

        // Inflate the layout for this fragment
        groupDetailViewModel = new ViewModelProvider(requireActivity()).get(GroupDetailViewModel.class);
        final int groupId = GroupDetailFragmentArgs.fromBundle(getArguments()).getGroupId();
        String title = GroupDetailFragmentArgs.fromBundle(getArguments()).getTitle();
        groupDetailViewModel.setGroup(new Group(groupId, title));

        //I do this to ensure that GroupViewModel is filled with data before the button can be pressed
        groupDetailViewModel.getGroup().observe(getViewLifecycleOwner(),
                new Observer<GroupDetail>() {
                    @Override
                    public void onChanged(final GroupDetail groupDetail) {
                        recycleViewAdapter.setTaskList(groupDetail.getTask());
                        fab.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AddTaskDialogFragment addTaskDialogFragment = new AddTaskDialogFragment();
                                        Bundle args = new Bundle();

                                        ArrayList<User> membersList = new ArrayList<>(groupDetail.getMembers());
                                        args.putSerializable(AddTaskDialogFragment.BUNDLE_ADDTASKDIALOG_MEMBERS, membersList);
                                        args.putInt(AddTaskDialogFragment.BUNDLE_ADDTASKDIALOG_GROUPID, groupId);

                                        addTaskDialogFragment.setArguments(args);

                                        addTaskDialogFragment.show(
                                                requireActivity().getSupportFragmentManager(),
                                                "AddNewTask"
                                        );
                                    }
                                }
                        );
                        memberButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MemberListDialogFragment memberListDialogFragment = new MemberListDialogFragment();

                                memberListDialogFragment.show(
                                        requireActivity().getSupportFragmentManager(),
                                        "MemberList"
                                );
                            }
                        });
                    }
                }
        );


        return v;
    }

    @Override
    public void onResume() {
        groupDetailViewModel.refreshData();
        super.onResume();
    }
}