package com.example.grouptaskandroid.data;

public interface IGenericRepository {
    void refreshData();
    void retrieveData(final boolean isRetry);
}
