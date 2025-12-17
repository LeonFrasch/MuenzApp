package com.example.muenzapp.data.repository;

import static android.content.ContentValues.TAG;
import static com.example.muenzapp.StaticHelper.stringToTableItem;

import android.util.Log;

import com.example.muenzapp.StaticHelper;
import com.example.muenzapp.data.model.CoinEntity;
import com.example.muenzapp.TableItem;
import com.example.muenzapp.data.model.IISpecial;
import com.example.muenzapp.data.model.IISpecialD;
import com.example.muenzapp.data.model.InternCoinEntity;
import com.example.muenzapp.utils.Constants;
import com.example.muenzapp.utils.FirestoreCallback;
import com.example.muenzapp.utils.FirestoreDataCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.Set;
import java.util.TreeSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoinRepository {

    private static CoinRepository instance;
    private final FirebaseFirestore db;

    private CoinRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized CoinRepository getInstance() {
        if (instance == null) {
            instance = new CoinRepository();
        }
        return instance;
    }

    /**
     * Prüft, ob die aktuelle User-ID Admin-Rechte hat.
     */
    public void checkIsAdmin(String uid, FirestoreDataCallback<Boolean> callback) {
        db.collection("userRole").get()
                .addOnSuccessListener(snapshots -> {
                    boolean isAdmin = false;
                    if (!snapshots.isEmpty()) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Object uidObj = doc.get("UID");
                            if (uidObj != null && uidObj.toString().equals(uid)) {
                                isAdmin = true;
                                break;
                            }
                        }
                    }
                    callback.onSuccess(isAdmin);
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     *      ==========================================
     *      DEUTSCHE MÜNZEN (Standard & Sonder)
     *      ==========================================
    */
    /** Standard Deutsche Münze */

    // (PUT): deutsche Standardmünze: Year:Value:Letter
    public void addGermanCoin(int year, TableItem value, TableItem letter, FirestoreCallback callback) {
        // Daten vorbereiten
        Map<String, Object> coinData = new HashMap<>();
        coinData.put(Constants.FIELD_YEAR, year);
        coinData.put(Constants.FIELD_VALUE, value.toString()); // Enum als String speichern
        coinData.put(Constants.FIELD_LETTER, letter.toString());

        // ID generieren (Example: "2002:ONE:A")
        String documentId = year + ":" + value + ":" + letter;

        // Schreiben in die Collection "D"
        db.collection(Constants.COLL_GERMANY)
                .document(documentId)
                .set(coinData, SetOptions.merge()) // Merge verhindert Überschreiben anderer Felder falls vorhanden
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // (GET): Lädt alle Münzen eines bestimmten Jahres aus der Collection "D".
    public void getGermanCoinsByYear(int year, FirestoreDataCallback<List<CoinEntity>> callback) {
        db.collection(Constants.COLL_GERMANY)
                .whereEqualTo(Constants.FIELD_YEAR, year)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CoinEntity> coinList = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            CoinEntity coin = new CoinEntity();

                            Long yearLong = doc.getLong(Constants.FIELD_YEAR);
                            coin.setCoinYear(yearLong != null ? yearLong.intValue() : 0);

                            String value = doc.getString(Constants.FIELD_VALUE);
                            coin.setCoinValue(stringToTableItem(value));

                            String letter = doc.getString(Constants.FIELD_LETTER);
                            coin.setCoinLetter(stringToTableItem(letter));

                            coinList.add(coin);
                        } catch (Exception e) {
                            Log.e("REPO", "Error parsing coin", e);
                        }
                    }
                    callback.onSuccess(coinList);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // (GET): Lädt alle Jahre, die in der deutschen Sammlung existieren. Nutzt ein TreeSet, damit die Jahre automatisch sortiert sind (2002, 2003...).
    public void getAllGermanYears(FirestoreDataCallback<List<Integer>> callback) {
        db.collection(Constants.COLL_GERMANY).get()
                .addOnSuccessListener(snapshots -> {
                    Set<Integer> uniqueYears = new TreeSet<>(); // TreeSet sortiert automatisch
                    for (DocumentSnapshot doc : snapshots) {
                        Long year = doc.getLong(Constants.FIELD_YEAR);
                        if (year != null) {
                            uniqueYears.add(year.intValue());
                        }
                    }
                    callback.onSuccess(new ArrayList<>(uniqueYears));
                })
                .addOnFailureListener(callback::onFailure);
    }

    // (DELETE): Löscht eine Münze.
    public void deleteGermanCoin(int year, TableItem value, TableItem letter, FirestoreCallback callback) {
        String documentId = year + ":" + value + ":" + letter;

        db.collection(Constants.COLL_GERMANY)
                .document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    /**Sonder Deutsche Münze*/

    // (PUT): Speichert eine deutsche Sondermünze (IISpecial): Year:Type:Letter
    public void addSpecialCoin(int year, TableItem type, TableItem letter, FirestoreCallback callback) {
        Map<String, Object> coinData = new HashMap<>();
        coinData.put(Constants.FIELD_YEAR, year);
        coinData.put("coinType", type.toString());
        coinData.put(Constants.FIELD_LETTER, letter.toString());

        String documentId = year + ":" + type + ":" + letter;

        db.collection(Constants.COLL_SPECIAL)
                .document(Constants.DOC_SPECIAL_II)
                .collection(Constants.COLL_GERMANY)
                .document(documentId)
                .set(coinData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // (PUT): Speichert eine deutsche Sondermünze.
    public void addGermanSpecialCoin(IISpecialD coin, FirestoreCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.FIELD_YEAR, coin.getCoinYear());
        data.put("coinType", coin.getCoinType().toString());
        data.put(Constants.FIELD_LETTER, coin.getCoinLetter().toString());

        String filename = coin.getCoinYear() + ":" + coin.getCoinType() + ":" + coin.getCoinLetter();

        db.collection(Constants.COLL_SPECIAL)
                .document(Constants.DOC_SPECIAL_II)
                .collection(Constants.COLL_GERMANY)
                .document(filename)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // (GET): Lädt ALLE deutschen Sondermünzen (IISonder).
    public void getGermanSpecialCoins(FirestoreDataCallback<List<IISpecialD>> callback) {
        db.collection(Constants.COLL_SPECIAL)
                .document(Constants.DOC_SPECIAL_II)
                .collection(Constants.COLL_GERMANY)
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<IISpecialD> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshots) {
                        try {
                            IISpecialD coin = new IISpecialD();
                            Long year = doc.getLong(Constants.FIELD_YEAR);
                            coin.setCoinYear(year != null ? year.intValue() : 0);
                            coin.setCoinType(stringToTableItem(doc.getString("coinType")));
                            coin.setCoinLetter(stringToTableItem(doc.getString(Constants.FIELD_LETTER)));
                            list.add(coin);
                        } catch (Exception e) {
                            Log.e("REPO", "Error parsing special coin", e);
                        }
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // (DELETE): Löscht eine deutsche Sondermünze.
    public void deleteGermanSpecialCoin(IISpecialD coin, FirestoreCallback callback) {
        String filename = coin.getCoinYear() + ":" + coin.getCoinType() + ":" + coin.getCoinLetter();

        db.collection(Constants.COLL_SPECIAL)
                .document(Constants.DOC_SPECIAL_II)
                .collection(Constants.COLL_GERMANY)
                .document(filename)
                .delete()
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    /**
     *          ==========================================
     *          INTERNATIONALE MÜNZEN
     *          ==========================================
     */
    /** Standard Intern Münze */

    // (PUT): Speichert eine internationale Münze.
    public void addInternCoin(String countryCode, int year, TableItem value, FirestoreCallback callback) {
        Map<String, Object> coinData = new HashMap<>();
        coinData.put(Constants.FIELD_YEAR, year);
        coinData.put(Constants.FIELD_VALUE, value.toString());

        String filename = year + ":" + value;

        db.collection(countryCode)
                .document(filename)
                .set(coinData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // (GET): Lädt alle internationalen Münzen eines Landes.
    public void getInternCoins(String countryCode, FirestoreDataCallback<List<InternCoinEntity>> callback) {
        db.collection(countryCode).get()
                .addOnSuccessListener(snapshots -> {
                    List<InternCoinEntity> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshots) {
                        try {
                            InternCoinEntity coin = new InternCoinEntity();
                            coin.setCoinCountry(StaticHelper.findCoinCountryItem(countryCode)); // Helper nutzen

                            Long year = doc.getLong(Constants.FIELD_YEAR);
                            coin.setCoinYear(year != null ? year.intValue() : 0);

                            String valStr = doc.getString(Constants.FIELD_VALUE);
                            coin.setCoinValue(StaticHelper.stringToTableItem(valStr));

                            list.add(coin);
                        } catch (Exception e) {
                            Log.e("REPO", "Error parsing intern coin", e);
                        }
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // (DELETE): Löscht eine internationale Münze.
    public void deleteInternCoin(String countryCode, int year, TableItem value, FirestoreCallback callback) {
        String filename = year + ":" + value;
        db.collection(countryCode)
                .document(filename)
                .delete()
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    /** Sonder Intern Münze */

    // (PUT): Speichert eine internationale Sondermünze.
    public void addInternSpecialCoin(String countryCode, int year, TableItem type, FirestoreCallback callback) {
        Map<String, Object> coinData = new HashMap<>();
        coinData.put(Constants.FIELD_YEAR, year);
        coinData.put("coinType", type.toString());
        coinData.put("coinCountry", countryCode);

        String filename = year + ":" + countryCode + ":" + type;

        db.collection(Constants.COLL_SPECIAL)
                .document(Constants.DOC_SPECIAL_II)
                .collection(countryCode)
                .document(filename)
                .set(coinData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // (GET): Lädt internationale Sondermünzen eines bestimmten Landes.
    public void getInternSpecialCoins(String countryCode, FirestoreDataCallback<List<IISpecial>> callback) {
        db.collection(Constants.COLL_SPECIAL)
                .document(Constants.DOC_SPECIAL_II)
                .collection(countryCode)
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<IISpecial> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshots) {
                        try {
                            IISpecial coin = new IISpecial();
                            Long year = doc.getLong(Constants.FIELD_YEAR);
                            coin.setCoinYear(year != null ? year.intValue() : 0);

                            String typeStr = doc.getString("coinType");
                            coin.setCoinType(StaticHelper.stringToTableItem(typeStr));

                            coin.setCoinCountry(StaticHelper.findCoinCountryItem(countryCode));

                            list.add(coin);
                        } catch (Exception e) {
                            Log.e("REPO", "Error parsing intern special coin", e);
                        }
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // (DELETE): Löscht eine internationale Sondermünze.
    public void deleteInternSpecialCoin(String countryCode, IISpecial coin, FirestoreCallback callback) {

        String filename = coin.getCoinYear() + ":" + countryCode + ":" + coin.getCoinType();

        db.collection(Constants.COLL_SPECIAL)
                .document(Constants.DOC_SPECIAL_II)
                .collection(countryCode)
                .document(filename)
                .delete()
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // Hinweis: Du musst eventuell auch die `addInternSpecialCoin` Methode leicht anpassen,
    // falls sie `IISpecial` Objekte statt einzelner Parameter erwarten soll,
    // aber für den Moment nutzen wir die Parameter-Version oder überladen sie.
    public void addInternSpecialCoin(IISpecial coin, FirestoreCallback callback) {
        // Wrapper für die existierende Methode oder eigene Implementierung
        String countryCode = coin.getCoinCountry().toString();
        addInternSpecialCoin(countryCode, coin.getCoinYear(), coin.getCoinType(), callback);
    }
}