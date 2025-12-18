package com.example.muenzapp.utils;

public interface FirestoreCallback {
    void onSuccess();
    void onFailure(Exception e);
}