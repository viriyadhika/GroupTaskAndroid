package com.example.grouptaskandroid.model;

public class Task {
    private int pk;
    private String name;
    private String desc;
    private GroupSummary groupSummary;

    public Task(int pk, String name, String desc, GroupSummary groupSummary) {
        this.pk = pk;
        this.name = name;
        this.desc = desc;
        this.groupSummary = groupSummary;
    }

    public int getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public GroupSummary getGroupSummary() {
        return groupSummary;
    }

    @Override
    public String toString() {
        return "Task{" +
                "pk=" + pk +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", groupSummary=" + groupSummary +
                '}';
    }
}
