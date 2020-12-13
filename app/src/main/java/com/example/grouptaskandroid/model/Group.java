package com.example.grouptaskandroid.model;

import java.util.List;

public class Group {
    int pk;
    String name;
    List<Task> task;
    List<User> members;

    public Group(int pk, String name, List<Task> task) {
        this.pk = pk;
        this.name = name;
        this.task = task;
    }

    @Override
    public String toString() {
        return "Group{" +
                "pk=" + pk +
                ", name='" + name + '\'' +
                ", task=" + task +
                ", members=" + members +
                '}';
    }
}
