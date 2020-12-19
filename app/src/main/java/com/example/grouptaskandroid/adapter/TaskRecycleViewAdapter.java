package com.example.grouptaskandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grouptaskandroid.R;
import com.example.grouptaskandroid.model.Task;

import java.util.List;

public class TaskRecycleViewAdapter extends RecyclerView.Adapter<TaskRecycleViewAdapter.ViewHolder> {

    private List<Task> taskList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task_row, parent, false);
        return new TaskRecycleViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.titleText.setText(task.getName());
        holder.descText.setText(task.getDesc());
        holder.groupNameText.setText(task.getGroup().getName());
        holder.inChargeText.setText(task.getInCharge().getUsername());
        holder.dueDateText.setText(task.getDueDate());
    }

    @Override
    public int getItemCount() {
        if (taskList == null) {
            return 0;
        }
        return taskList.size();
    }

    public void setTaskList(List<Task> tasks) {
        taskList = tasks;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView titleText;
        protected TextView descText;
        protected TextView groupNameText;
        protected TextView inChargeText;
        protected TextView dueDateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.task_row_title);
            descText = itemView.findViewById(R.id.task_row_desc);
            groupNameText = itemView.findViewById(R.id.task_row_group);
            inChargeText = itemView.findViewById(R.id.task_row_inCharge);
            dueDateText = itemView.findViewById(R.id.task_row_dueDate);
        }
    }

}
