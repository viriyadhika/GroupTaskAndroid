package com.example.grouptaskandroid.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.grouptaskandroid.R;
import com.example.grouptaskandroid.adapter.GroupRecycleViewAdapter;
import com.example.grouptaskandroid.adapter.TaskRecycleViewAdapter;
import com.example.grouptaskandroid.model.GroupSummary;
import com.example.grouptaskandroid.model.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = "TaskFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TaskViewModel taskViewModel;
    private TaskRecycleViewAdapter taskRecycleViewAdapter;
    private RecyclerView recyclerView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskFragment newInstance(String param1, String param2) {
        TaskFragment fragment = new TaskFragment();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_task, container, false);


        taskRecycleViewAdapter = new TaskRecycleViewAdapter();

        recyclerView = v.findViewById(R.id.task_recyclerView);
        recyclerView.setAdapter(taskRecycleViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        taskViewModel.getTask().observe(getActivity(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                taskRecycleViewAdapter.setTaskList(tasks);
            }
        });

//        taskRecycleViewAdapter = new TaskRecycleViewAdapter(new GroupRecycleViewAdapter.GroupRecycleViewListener() {
//            @Override
//            public void getGroupDetail(int pk) {
//                groupViewModel.setSelectedGroupPk(pk);
//            }
//        });



        return v;
    }

    @Override
    public void onResume() {
        taskViewModel.refreshData();
        super.onResume();
    }
}