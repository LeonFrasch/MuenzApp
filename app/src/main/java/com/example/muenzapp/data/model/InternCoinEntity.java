package com.example.muenzapp.data.model;

import com.example.muenzapp.TableItem;

import java.util.Objects;

public class InternCoinEntity {
    private int coinYear;
    private TableItem coinValue;
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
