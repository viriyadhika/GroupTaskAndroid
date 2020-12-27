package com.example.grouptaskandroid.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.grouptaskandroid.R;
import com.example.grouptaskandroid.exception.AuthenticationFailedException;
import com.example.grouptaskandroid.model.User;
import com.example.grouptaskandroid.util.Constants;
import com.example.grouptaskandroid.util.DateTimeHandler;

import java.util.ArrayList;
import java.util.List;

public class AddTaskDialogFragment extends DialogFragment implements GroupDetailViewModel.AddTaskDialogListener {

    public static final String TAG = "AddTaskDialogFragment";

    private Spinner inCharge;
    private ImageButton calendarButton;
    private EditText name;
    private EditText desc;
    private EditText dueDate;

    private View view;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_add_task, null);
        builder.setView(view);

        final GroupDetailViewModel viewModel = new ViewModelProvider(requireActivity()).get(GroupDetailViewModel.class);
        viewModel.setAddTaskDialogListener(this);

        name = view.findViewById(R.id.add_task_name);
        desc = view.findViewById(R.id.add_task_desc);

        dueDate = view.findViewById(R.id.add_task_dueDate);
        dueDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText castedView = (EditText) v;
                    if (!castedView.getText().toString().isEmpty()) {
                        String date = castedView.getText().toString();
                        viewModel.verifyDate(date);
                    }
                }
            }
        });

        inCharge = view.findViewById(R.id.add_task_inCharge);
        assert getArguments() != null;
        ArrayList<User> members = (ArrayList<User>) getArguments().getSerializable(Constants.BUNDLE_ADDTASKDIALOG_MEMBERS);
        List<String> usernameList = viewModel.getMemberNames(members);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),
                R.layout.support_simple_spinner_dropdown_item,
                usernameList);
        inCharge.setAdapter(adapter);

        final DatePickerDialog datePickerDialogFrom = new DatePickerDialog(requireContext());
        datePickerDialogFrom.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Plus one is because month start from 0
                String datePicked = DateTimeHandler.getCalendarText(year, month + 1, dayOfMonth);
                dueDate.setText(datePicked);
            }
        });

        calendarButton = view.findViewById(R.id.add_task_calendarButton);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialogFrom.show();
            }
        });

        final int groupId = getArguments().getInt(Constants.BUNDLE_ADDTASKDIALOG_GROUPID);
        return builder
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.saveTask(
                                name.getText().toString(),
                                desc.getText().toString(),
                                groupId,
                                inCharge.getSelectedItem().toString(),
                                dueDate.getText().toString()
                        );
                    }
                })
                .setNegativeButton(
                        "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }
                )
                .create();
    }

    @Override
    public void onDateFormatError() {
        dueDate.setError("Date format is invalid");
    }

}
