package com.example.grouptaskandroid.model;

import java.util.List;

public class GroupDetail {
    int pk;
    String name;
    List<Task> task;
    List<User> members;

    public GroupDetail(int pk, String name, List<Task> task, List<User> members) {
        this.pk = pk;
        this.name = name;
        this.task = task;
        this.members = members;
    }

    @Override
    public String toString() {
        return "GroupDetail{" +
                "pk=" + pk +
                ", name='" + name + '\'' +
                ", task=" + task +
                ", members=" + members +
                '}';
    }
}
