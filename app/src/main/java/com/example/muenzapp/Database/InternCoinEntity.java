package com.example.muenzapp.Database;

import androidx.room.*;

import com.example.muenzapp.TableItem;

import java.util.Objects;

@Entity(tableName = "InternCoinEntity")
public class InternCoinEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "CoinYear")
    private int coinYear;
    @ColumnInfo(name = "CoinValue")
    private TableItem coinValue;
    @ColumnInfo(name = "CoinCountry")
    private TableItem coinCountry;
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        InternCoinEntity internCoinEntity = (InternCoinEntity) obj;

        // Vergleich der Felder, ohne die id zu berücksichtigen
        return coinYear == internCoinEntity.coinYear &&
                Objects.equals(coinValue, internCoinEntity.coinValue) &&
                Objects.equals(coinCountry, internCoinEntity.coinCountry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coinYear, coinValue, coinCountry);
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

    public TableItem getCoinCountry() {
        return coinCountry;
    }

    public void setCoinCountry(TableItem coinCountry) {
        this.coinCountry = coinCountry;
    }
}
