package com.example.grouptaskandroid.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.grouptaskandroid.R;
import com.example.grouptaskandroid.model.Group;

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

    private GroupDetailViewModel groupViewModel;
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
        // Inflate the layout for this fragment
        groupViewModel = new ViewModelProvider(requireActivity()).get(GroupDetailViewModel.class);
        int groupId = GroupDetailFragmentArgs.fromBundle(getArguments()).getGroupId();
        groupViewModel.setGroupId(groupId);
        groupViewModel.getGroup().observe(getViewLifecycleOwner(),
                new Observer<Group>() {
                    @Override
                    public void onChanged(Group group) {
                        Log.d(TAG, "onChanged: " + group);
                    }
                }
        );

        return v;
    }
}