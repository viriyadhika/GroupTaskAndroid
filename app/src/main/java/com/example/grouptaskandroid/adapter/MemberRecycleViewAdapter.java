package com.example.grouptaskandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grouptaskandroid.R;
import com.example.grouptaskandroid.model.User;

import java.util.List;

public class MemberRecycleViewAdapter extends RecyclerView.Adapter<MemberRecycleViewAdapter.ViewHolder>  {

    private List<User> members;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_member_row, parent, false);
        return new MemberRecycleViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.memberName.setText(members.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        if (members != null) {
            return members.size();
        } else return 0;
    }

    public void setMembers(List<User> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView memberName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.fragment_member_row_memberName);
        }
    }
}
