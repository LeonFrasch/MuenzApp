package com.example.muenzapp.Database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private static CoinDatabase database;
    public static synchronized CoinDatabase getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(),CoinDatabase.class, "coin-database").fallbackToDestructiveMigration().build();
        }
        return database;
    }
}
