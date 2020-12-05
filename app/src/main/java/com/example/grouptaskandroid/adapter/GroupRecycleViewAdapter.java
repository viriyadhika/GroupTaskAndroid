package com.example.grouptaskandroid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grouptaskandroid.R;
import com.example.grouptaskandroid.model.GroupSummary;

import java.util.List;

public class GroupRecycleViewAdapter extends RecyclerView.Adapter<GroupRecycleViewAdapter.ViewHolder> {

    public static final String TAG = "GroupRecycleViewAdapter";

    List<GroupSummary> myGroups;
    private GroupRecycleViewListener groupRecycleViewListener;

    public GroupRecycleViewAdapter(GroupRecycleViewListener listener) {
        super();
        groupRecycleViewListener = listener;
    }

    public interface GroupRecycleViewListener {
        void getGroupDetail(int pk);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_group_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.nameTextView.setText(
                myGroups.get(position).getName()
        );
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupRecycleViewListener.getGroupDetail(myGroups.get(position).getPk());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (myGroups == null) {
            return 0;
        }
        return myGroups.size();
    }

    public void setMyGroups(List<GroupSummary> myGroups) {
        this.myGroups = myGroups;
        notifyDataSetChanged();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView nameTextView;
        protected CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nameTextView = itemView.findViewById(R.id.group_row_name);
            this.cardView = itemView.findViewById(R.id.group_row_card);
        }
    }
}
