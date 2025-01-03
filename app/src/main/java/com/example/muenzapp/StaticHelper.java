package com.example.muenzapp;

import static com.example.muenzapp.TableItem.*;

public class StaticHelper {
    public static TableItem findCoinCountryItem(String item) {
        switch (item) {
            case "E": return E;
            case "L": return L;
            case "GR": return GR;
            case "FIN": return FIN;
            case "IR": return IR;
            case "NL": return NL;
            case "B": return B;
            case "F": return F;
            case "I": return I;
            case "A": return A;
            case "P": return P;
        }
        return null;
    }
    public static String findCoinCountryStringFull(String item) {
        switch (item) {
            case "E": return "Spanien";
            case "L": return "Luxemburg";
            case "GR": return "Griechenland";
            case "FIN": return "Finnland";
            case "IR": return "Irland";
            case "NL": return "Niederlande";
            case "B": return "Belgien";
            case "F": return "Frankreich";
            case "I": return "Italien";
            case "A": return "Österreich";
            case "P": return "Portugal";
        }
        return null;
    }
    public static String findValueString(TableItem tableItem) {
        switch (tableItem) {
            case ONE: return "1";
            case TWO: return "2";
            case FIVE: return "5";
            case TEN: return "10";
            case TWENTY: return "20";
            case FIFTY: return "50";
            case I: return "I";
            case II: return "II";
        }
        return null;
    }
    public static TableItem stringToTableItem(String stringOfTableItem) {
        switch (stringOfTableItem) {
            case "ONE": return ONE;
            case "TWO": return TWO;
            case "FIVE": return FIVE;
            case "TEN": return TEN;
            case "TWENTY": return TWENTY;
            case "FIFTY": return FIFTY;
            case "I": return I;
            case "II": return II;
        }
        return null;
    }
}
