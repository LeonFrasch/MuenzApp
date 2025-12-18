package com.example.muenzapp.data.model;

import com.example.muenzapp.TableItem;

import java.util.Objects;

public class IISpecial {
    private int coinYear;
    private TableItem coinType;
    private TableItem coinCountry;
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        IISpecial otherCoin = (IISpecial) obj;

        return coinYear == otherCoin.coinYear &&
                coinType == otherCoin.coinType &&
                coinCountry == otherCoin.coinCountry;
    }
    @Override
    public int hashCode() {
        return Objects.hash(coinYear, coinType, coinCountry);
    }
    // GETTER UND SETTER
    public TableItem getCoinCountry() {
        return coinCountry;
    }

    public void setCoinCountry(TableItem coinCountry) {
        this.coinCountry = coinCountry;
    }

    public int getCoinYear() {
        return coinYear;
    }

    public void setCoinYear(int coinYear) {
        this.coinYear = coinYear;
    }

    public TableItem getCoinType() {
        return coinType;
    }

    public void setCoinType(TableItem coinType) {
        this.coinType = coinType;
    }
}
