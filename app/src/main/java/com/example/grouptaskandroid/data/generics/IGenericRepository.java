package com.example.grouptaskandroid.data.generics;

public interface IGenericRepository {
    void refreshData();
    void callAPI(final boolean isRetry);
}
