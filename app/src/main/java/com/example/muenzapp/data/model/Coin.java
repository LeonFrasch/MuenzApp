package com.example.muenzapp.data.model;

import com.example.muenzapp.TableItem;

import java.util.Objects;

public class Coin {
    private int year;
    private TableItem country; // D, F, I, ...
    private TableItem value; // ONE, TWO, FIVE, ...
    private TableItem type; // CC1, CC2, CC3
    private TableItem letter; // A, D, F, G, J, else null

    public Coin() {}

    /**
     *      Internal Constructor for Coins
     * */
    public Coin(int year, TableItem country, TableItem value, TableItem letter, TableItem type) {
        this.year = year;
        this.country = country;
        this.value = value;
        this.letter = letter;
        this.type = type;
    }

    /** Create German Standard Coin (e.g. 3, ONE, D) */
    public static Coin createGermanStandard(int year, TableItem value, TableItem letter) {
        return new Coin(year, TableItem.D, value, letter, null);
    }

    /** Create Intern Standard Coin (e.g. 3, I, ONE) */
    public static Coin createInternStandard(int year, TableItem country, TableItem value) {
        return new Coin(year, country, value, null, null);
    }

    /** Create German Special Coin (e.g. 3, CC1, D) */
    public static Coin createGermanSpecial(int year, TableItem type, TableItem letter) {
        return new Coin(year, TableItem.D, null, letter, type);
    }

    /** Create Intern Special Coin (e.g. 3, I, CC1) */
    public static Coin createInternSpecial(int year, TableItem country, TableItem type) {
        return new Coin(year, country, null, null, type);
    }
    // Getter & Setter
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public TableItem getCountry() { return country; }
    public void setCountry(TableItem country) { this.country = country; }

    public TableItem getValue() { return value; }
    public void setValue(TableItem value) { this.value = value; }

    public TableItem getType() { return type; }
    public void setType(TableItem type) { this.type = type; }

    public TableItem getLetter() { return letter; }
    public void setLetter(TableItem letter) { this.letter = letter; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coin coin = (Coin) o;
        return year == coin.year &&
                country == coin.country &&
                value == coin.value &&
                type == coin.type &&
                letter == coin.letter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, country, value, type, letter);
    }
}