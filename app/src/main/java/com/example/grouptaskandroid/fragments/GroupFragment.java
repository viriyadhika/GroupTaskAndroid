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
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.example.grouptaskandroid.R;
import com.example.grouptaskandroid.adapter.GroupRecycleViewAdapter;
import com.example.grouptaskandroid.model.GroupSummary;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment {

    public static final String TAG = "GroupFragment";

    private GroupViewModel groupViewModel;
    private RecyclerView recyclerView;
    private GroupRecycleViewAdapter recycleViewAdapter;

    public GroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupFragment newInstance(String param1, String param2) {
        GroupFragment fragment = new GroupFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group, container, false);


        recycleViewAdapter = new GroupRecycleViewAdapter(new GroupRecycleViewAdapter.GroupRecycleViewListener() {
            @Override
            public void getGroupDetail(int pk) {
                groupViewModel.setSelectedGroupPk(pk);
            }
        });
        recyclerView = v.findViewById(R.id.group_recyclerView);
        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Initiate all the view component
        groupViewModel = new ViewModelProvider(requireActivity()).get(GroupViewModel.class);
        groupViewModel.getMyGroups().observe(getActivity(), new Observer<List<GroupSummary>>() {
            @Override
            public void onChanged(List<GroupSummary> myGroups) {
                Log.d(TAG, "onChanged: " + myGroups);
                recycleViewAdapter.setMyGroups(myGroups);
            }
        });



        return v;
    }
}