package com.example.muenzapp.data.model;

import com.example.muenzapp.TableItem;

import java.util.Objects;

public class IISpecialD {
    private int coinYear;
    private TableItem coinType;
    private TableItem coinLetter;
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        IISpecialD otherCoin = (IISpecialD) obj;

        return coinYear == otherCoin.coinYear &&
                coinType == otherCoin.coinType &&
                coinLetter == otherCoin.coinLetter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coinYear, coinType, coinLetter);
    }
    // GETTER UND SETTER

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

    public TableItem getCoinLetter() {
        return coinLetter;
    }

    public void setCoinLetter(TableItem coinLetter) {
        this.coinLetter = coinLetter;
    }
}
