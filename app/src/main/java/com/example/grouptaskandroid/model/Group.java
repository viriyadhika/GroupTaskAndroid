package com.example.grouptaskandroid.model;

import java.util.Objects;

public class Group {
    public int pk;
    public String name;

    public Group(int pk) {
        this.pk = pk;
    }

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return pk == group.pk &&
                Objects.equals(name, group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pk, name);
    }

    @Override
    public String toString() {
        return "Group{" +
                "pk=" + pk +
                ", name='" + name + '\'' +
                '}';
    }
}
