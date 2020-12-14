package com.example.grouptaskandroid.model;

public class Group {
    public int pk;
    public String name;

    public Group(int pk, String name) {
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
        return "Group{" +
                "pk=" + pk +
                ", name='" + name + '\'' +
                '}';
    }
}
