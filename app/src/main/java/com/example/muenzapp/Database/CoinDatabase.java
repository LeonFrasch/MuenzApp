package com.example.muenzapp.Database;

import androidx.room.*;
@Database(entities = {CoinEntity.class, InternCoinEntity.class}, version = 1)
public abstract class CoinDatabase extends RoomDatabase {
    public abstract CollectionDao collectionDao();
}
