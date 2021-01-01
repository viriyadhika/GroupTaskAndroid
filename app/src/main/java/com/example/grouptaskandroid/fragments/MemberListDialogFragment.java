package com.example.grouptaskandroid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
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
import android.widget.EditText;

import com.example.grouptaskandroid.R;
import com.example.grouptaskandroid.adapter.MemberRecycleViewAdapter;
import com.example.grouptaskandroid.exception.network.NotFoundException;
import com.example.grouptaskandroid.model.GroupDetail;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemberListDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemberListDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String TAG = "MemberListDialogFragment";

    private GroupDetailViewModel viewModel;
    private MemberRecycleViewAdapter adapter;

    private EditText memberUsername;
    private Button addMemberButton;

    public MemberListDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemberListDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemberListDialogFragment newInstance(String param1, String param2) {
        MemberListDialogFragment fragment = new MemberListDialogFragment();
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_member_list_dialog, null);
        builder.setView(v);
        // Inflate the layout for this fragment

        viewModel = new ViewModelProvider(requireActivity()).get(GroupDetailViewModel.class);
        adapter = new MemberRecycleViewAdapter();
        RecyclerView recyclerView = v.findViewById(R.id.member_list_memberList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewModel.getGroup().observe(this, new Observer<GroupDetail>() {
            @Override
            public void onChanged(GroupDetail groupDetail) {
                adapter.setMembers(
                        groupDetail.getMembers()
                );
            }
        });

        memberUsername = v.findViewById(R.id.member_list_memberUsername);
        addMemberButton = v.findViewById(R.id.member_list_addMemberButton);
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.addPersonToGroup(memberUsername.getText().toString());
            }
        });

        viewModel.getErrorAddMemberToGroup().observe(this, new Observer<Exception>() {
            @Override
            public void onChanged(Exception e) {
                if (e instanceof NotFoundException) {
                    NotFoundException notFoundException = (NotFoundException) e;
                    memberUsername.setError(notFoundException.getMessage());
                }
            }
        });

        return builder.create();
    }
}