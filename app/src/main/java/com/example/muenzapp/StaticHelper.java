package com.example.muenzapp;

import static android.content.ContentValues.TAG;
import static com.example.muenzapp.TableItem.*;

import android.util.Log;

import com.example.muenzapp.Database.CoinEntity;
import com.example.muenzapp.Database.IISpecial;
import com.example.muenzapp.Database.IISpecialD;
import com.example.muenzapp.Database.InternCoinEntity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            case "A": return A;
            case "D": return D;
            case "F": return F;
            case "G": return G;
            case "J": return J;

            case "CC1": return CC1;
            case "CC2": return CC2;
            case "CC3": return CC3;
        }
        return null;
    }
    public static void changeDatabase(FirebaseFirestore db, TableItem coinCountry, String activity) {
        // get data of current database
        switch (activity) {
            case "SonderIID": { //case SonderII German
                db.collection("Sonder").document("IISonder").collection("D").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Map<String, Object> data = document.getData();
                            TableItem coinLetter = stringToTableItem(data.get("coinLetter").toString());
                            TableItem coinType = stringToTableItem(data.get("coinType").toString());
                            Long coinYearLong = (Long) data.get("coinYear");
                            int coinYear = (int) coinYearLong.longValue();

                            Map<String, Object> coin = new HashMap<>();
                            coin.put("coinYear", coinYear);
                            coin.put("coinLetter", coinLetter);
                            coin.put("coinType", coinType);

                            String filename = coinYear + ":" + coinType + ":" + coinLetter;
                            db.collection("coins").document("IISonder").collection("D").document(filename)
                                    .set(coin, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                        }
                    }
                });
            }
            case "SonderIIIntern": { //case SonderII Intern
                db.collection("Sonder").document("IISonder").collection(coinCountry + "").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Map<String, Object> data = document.getData();
                            TableItem coinType = stringToTableItem(data.get("coinType").toString());
                            Long coinYearLong = (Long) data.get("coinYear");
                            int coinYear = (int) coinYearLong.longValue();

                            Map<String, Object> coin = new HashMap<>();
                            coin.put("coinYear", coinYear);
                            coin.put("coinType", coinType);
                            coin.put("coinCountry", coinCountry);

                            String filename = coinYear + ":" + coinCountry + ":" + coinType;
                            db.collection("coins").document("IISonder").collection(coinCountry + "").document(filename)
                                    .set(coin, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                        }
                    }
                });
            }
            case "OrdinaryD": {
                db.collection("D").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Map<String, Object> data = document.getData();
                            Long coinYearLong = (Long) data.get("coinYear");
                            int coinYear = (int) coinYearLong.longValue();
                            TableItem coinValue = stringToTableItem(data.get("coinValue").toString());
                            TableItem coinLetter = stringToTableItem(data.get("coinLetter").toString());

                            Map<String, Object> coin = new HashMap<>();
                            coin.put("coinYear", coinYear);
                            coin.put("coinValue", coinValue);
                            coin.put("coinLetter", coinLetter);

                            String filename = coinYear + ":" + coinValue + ":" + coinLetter;
                            db.collection("coins").document("Ordinary").collection("D").document(filename)
                                    .set(coin, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                        }
                    }
                });
            }
            case "OrdinaryIntern": {
                db.collection(coinCountry.toString()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Map<String, Object> data = document.getData();
                            TableItem coinValue = stringToTableItem(data.get("coinValue").toString());
                            Long coinYearLong = (Long) data.get("coinYear");
                            int coinYear = (int) coinYearLong.longValue();

                            Map<String, Object> coin = new HashMap<>();
                            coin.put("coinYear", coinYear);
                            coin.put("coinValue", coinValue);
                            String filename = coinYear + ":" + coinValue;

                            db.collection("coins").document("Ordinary").collection(coinCountry + "").document(filename)
                                    .set(coin, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                        }
                    }
                });
            }
        }
    }
}
