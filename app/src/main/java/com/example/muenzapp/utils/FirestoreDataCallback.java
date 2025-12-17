package com.example.muenzapp.utils;

public interface FirestoreDataCallback<T> {
    void onSuccess(T data);
    void onFailure(Exception e);
}