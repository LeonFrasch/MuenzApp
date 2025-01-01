package com.example.muenzapp.Firebase;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.muenzapp.TableItem;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Objects;

@IgnoreExtraProperties
public class InternCoinEntity {
    private String id;
    private int coinYear;
    private String coinValue;
    private String coinCountry;

    public InternCoinEntity() {}

    public InternCoinEntity(int coinYear, String coinValue, String coinCountry) {
        this.coinYear = coinYear;
        this.coinValue = coinValue;
        this.coinCountry = coinCountry;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCoinYear() {
        return coinYear;
    }

    public void setCoinYear(int coinYear) {
        this.coinYear = coinYear;
    }

    public String getCoinValue() {
        return coinValue;
    }

    public void setCoinValue(String coinValue) {
        this.coinValue = coinValue;
    }

    public String getCoinCountry() {
        return coinCountry;
    }

    public void setCoinCountry(String coinCountry) {
        this.coinCountry = coinCountry;
    }
}
