package com.example.muenzapp.Database;

import androidx.annotation.Nullable;
import androidx.room.*;

import com.example.muenzapp.TableItem;

import java.util.Objects;

@Entity(tableName = "CoinEntity")
public class CoinEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "CoinYear")
    private int coinYear;
    @ColumnInfo(name = "CoinValue")
    private TableItem coinValue;
    @ColumnInfo(name = "CoinLetter")
    private TableItem coinLetter;
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CoinEntity otherCoin = (CoinEntity) obj;

        return coinYear == otherCoin.coinYear &&
                coinValue == otherCoin.coinValue &&
                coinLetter == otherCoin.coinLetter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coinYear, coinValue, coinLetter);
    }
    // GETTER UND SETTER

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCoinYear() {
        return coinYear;
    }

    public void setCoinYear(int coinYear) {
        this.coinYear = coinYear;
    }

    public TableItem getCoinValue() {
        return coinValue;
    }

    public void setCoinValue(TableItem coinValue) {
        this.coinValue = coinValue;
    }

    public TableItem getCoinLetter() {
        return coinLetter;
    }

    public void setCoinLetter(TableItem coinLetter) {
        this.coinLetter = coinLetter;
    }

}
