package com.example.grouptaskandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grouptaskandroid.R;
import com.example.grouptaskandroid.model.User;

import java.util.List;

public class MemberRecycleViewAdapter extends RecyclerView.Adapter<MemberRecycleViewAdapter.ViewHolder>  {

    private List<User> members;
    private Listener listener;

    public interface Listener {
        void onDeleteMember(User user);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_member_row, parent, false);
        return new MemberRecycleViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.memberName.setText(members.get(position).getUsername());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteMember(members.get(position));
            }
        });
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
        protected ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.fragment_member_row_memberName);
            deleteButton = itemView.findViewById(R.id.fragment_member_row_deleteButton);
        }
    }
}
