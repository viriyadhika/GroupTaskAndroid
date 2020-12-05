package com.example.grouptaskandroid.model;

public class GroupSummary {
    public int pk;
    public String name;

    public GroupSummary(int pk, String name) {
        this.pk = pk;
        this.name = name;
    }

    public int getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "GroupSummary{" +
                "pk=" + pk +
                ", name='" + name + '\'' +
                '}';
    }
}
