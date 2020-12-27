package com.example.grouptaskandroid.model;

public class Task {
    private int pk;
    private String name;
    private String desc;
    private Group group;
    private User inCharge;
    private String dueDate;
    private boolean isDone;

    public Task(
            String name,
            String desc,
            Group group,
            User user,
            String dueDate,
            boolean isDone
    ) {
        this.name = name;
        this.desc = desc;
        this.group = group;
        this.inCharge = user;
        this.dueDate = dueDate;
        this.isDone = isDone;
    }

    public Task(int pk,
                String name,
                String desc,
                Group group,
                User user,
                String dueDate,
                boolean isDone) {
        this.pk = pk;
        this.name = name;
        this.desc = desc;
        this.group = group;
        this.inCharge = user;
        this.dueDate = dueDate;
        this.isDone = isDone;
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

    public Group getGroup() {
        return group;
    }

    public User getInCharge() {
        return inCharge;
    }

    public String getDueDate() {
        return dueDate;
    }

    public boolean isDone() {
        return isDone;
    }

    @Override
    public String toString() {
        return "Task{" +
                "pk=" + pk +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", group=" + group +
                ", inCharge=" + inCharge +
                ", dueDate='" + dueDate + '\'' +
                ", isDone=" + isDone +
                '}';
    }
}
