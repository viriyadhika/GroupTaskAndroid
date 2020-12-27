package com.example.grouptaskandroid.data.generics;

public interface IGenericPostRepository<T> {
    void callAPI(boolean isRetry, T data);
}
