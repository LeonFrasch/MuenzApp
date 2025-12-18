package com.example.muenzapp.data.repository;

import static com.example.muenzapp.StaticHelper.stringToTableItem;

import android.util.Log;

import com.example.muenzapp.StaticHelper;
import com.example.muenzapp.data.model.Coin;
import com.example.muenzapp.TableItem;
import com.example.muenzapp.utils.Constants;
import com.example.muenzapp.utils.FirestoreCallback;
import com.example.muenzapp.utils.FirestoreDataCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
    // ==========================================
    //  HELPER INTERFACE & METHODS
    // ==========================================

    private interface Mapper<T> {
        T map(DocumentSnapshot doc) throws Exception;
    }

    private <T> void fetchList(Query query, Mapper<T> mapper, FirestoreDataCallback<List<T>> callback) {
        query.get()
                .addOnSuccessListener(snapshots -> {
                    List<T> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshots) {
                        try {
                            T item = mapper.map(doc);
                            list.add(item);
                        } catch (Exception e) {
                            Log.e("REPO", "Error parsing document: " + doc.getId(), e);
                        }
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onFailure);
    }

    private void saveDocument(CollectionReference collection, String docId, Map<String, Object> data, FirestoreCallback callback) {
        collection.document(docId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    private void deleteDocument(CollectionReference collection, String docId, FirestoreCallback callback) {
        collection.document(docId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Prüft, ob die aktuelle User-ID Admin-Rechte hat.
     */
    public void checkIsAdmin(String uid, FirestoreDataCallback<Boolean> callback) {
        db.collection("userRole").whereEqualTo("UID", uid).get()
                .addOnSuccessListener(snapshots -> callback.onSuccess(!snapshots.isEmpty()))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     *      ==========================================
     *      DEUTSCHE MÜNZEN (Standard & Sonder)
     *      ==========================================
    */
    /** Standard Deutsche Münze */

    // (PUT): deutsche Standardmünze: Year:Value:Letter
    public void addGermanCoin(Coin coin, FirestoreCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.FIELD_YEAR, coin.getYear());
        data.put(Constants.FIELD_VALUE, coin.getValue().toString());
        data.put(Constants.FIELD_LETTER, coin.getLetter().toString());

        String id = coin.getYear() + ":" + coin.getValue() + ":" + coin.getLetter();
        saveDocument(db.collection(Constants.COLL_GERMANY), id, data, callback);
    }

    // (GET): Lädt alle Münzen eines bestimmten Jahres aus der Collection "D".
    public void getGermanCoinsByYear(int year, FirestoreDataCallback<List<Coin>> callback) {
        Query query = db.collection(Constants.COLL_GERMANY).whereEqualTo(Constants.FIELD_YEAR, year);

        fetchList(query, doc -> {
            int y = doc.getLong(Constants.FIELD_YEAR).intValue();
            TableItem val = stringToTableItem(doc.getString(Constants.FIELD_VALUE));
            TableItem letter = stringToTableItem(doc.getString(Constants.FIELD_LETTER));
            return Coin.createGermanStandard(y, val, letter);
        }, callback);
    }

    // (GET): Lädt alle Jahre, die in der deutschen Sammlung existieren. Nutzt ein TreeSet, damit die Jahre automatisch sortiert sind (2002, 2003...).
    public void getAllGermanYears(FirestoreDataCallback<List<Integer>> callback) {
        fetchList(db.collection(Constants.COLL_GERMANY), doc -> doc.getLong(Constants.FIELD_YEAR).intValue(), new FirestoreDataCallback<List<Integer>>() {
            @Override
            public void onSuccess(List<Integer> data) {
                Set<Integer> uniqueYears = new TreeSet<>(data);
                callback.onSuccess(new ArrayList<>(uniqueYears));
            }
            @Override
            public void onFailure(Exception e) { callback.onFailure(e); }
        });
    }

    // (DELETE): Löscht eine Münze.
    public void deleteGermanCoin(Coin coin, FirestoreCallback callback) {
        String id = coin.getYear() + ":" + coin.getValue() + ":" + coin.getLetter();
        deleteDocument(db.collection(Constants.COLL_GERMANY), id, callback);
    }

    /**Sonder Deutsche Münze*/

    // (PUT): Speichert eine deutsche Sondermünze (IISpecial): Year:Type:Letter
    public void addGermanSpecialCoin(Coin coin, FirestoreCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.FIELD_YEAR, coin.getYear());
        data.put("coinType", String.valueOf(coin.getType()));
        data.put(Constants.FIELD_LETTER, String.valueOf(coin.getLetter()));

        String id = coin.getYear() + ":" + coin.getType() + ":" + coin.getLetter();
        CollectionReference col = db.collection(Constants.COLL_SPECIAL)
                .document(Constants.DOC_SPECIAL_II).collection(Constants.COLL_GERMANY);

        saveDocument(col, id, data, callback);
    }
    // (GET): Gibt alle deutschen Sondermünzen
    public void getGermanSpecialCoins(FirestoreDataCallback<List<Coin>> callback) {
        CollectionReference col = db.collection(Constants.COLL_SPECIAL)
                .document(Constants.DOC_SPECIAL_II).collection(Constants.COLL_GERMANY);

        fetchList(col, doc -> {
            int y = doc.getLong(Constants.FIELD_YEAR).intValue();
            TableItem type = stringToTableItem(doc.getString("coinType"));
            TableItem letter = stringToTableItem(doc.getString(Constants.FIELD_LETTER));
            return Coin.createGermanSpecial(y, type, letter);
        }, callback);
    }

    // (DELETE): Löscht eine deutsche Sondermünze.
    public void deleteGermanSpecialCoin(Coin coin, FirestoreCallback callback) {
        String id = coin.getYear() + ":" + coin.getType() + ":" + coin.getLetter();
        CollectionReference col = db.collection(Constants.COLL_SPECIAL)
                .document(Constants.DOC_SPECIAL_II).collection(Constants.COLL_GERMANY);

        deleteDocument(col, id, callback);
    }

    /**
     *          ==========================================
     *          INTERNATIONALE MÜNZEN
     *          ==========================================
     */
    /** Standard Intern Münze */

    // (PUT): Speichert eine internationale Münze.
    public void addInternCoin(Coin coin, FirestoreCallback callback) {
        String countryCode = coin.getCountry().toString();
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.FIELD_YEAR, coin.getYear());
        data.put(Constants.FIELD_VALUE, coin.getValue().toString());

        String id = coin.getYear() + ":" + coin.getValue();
        saveDocument(db.collection(countryCode), id, data, callback);
    }

    // (GET): Lädt alle internationalen Münzen eines Landes.
    public void getInternCoins(String countryCode, FirestoreDataCallback<List<Coin>> callback) {
        fetchList(db.collection(countryCode), doc -> {
            int y = doc.getLong(Constants.FIELD_YEAR).intValue();
            TableItem val = stringToTableItem(doc.getString(Constants.FIELD_VALUE));
            TableItem country = StaticHelper.findCoinCountryItem(countryCode);
            return Coin.createInternStandard(y, country, val);
        }, callback);
    }

    // (DELETE): Löscht eine internationale Münze.
    public void deleteInternCoin(Coin coin, FirestoreCallback callback) {
        String countryCode = coin.getCountry().toString();
        String id = coin.getYear() + ":" + coin.getValue();
        deleteDocument(db.collection(countryCode), id, callback);
    }

    /** Sonder Intern Münze */

    // (PUT): Speichert eine internationale Sondermünze.
    public void addInternSpecialCoin(Coin coin, FirestoreCallback callback) {
        String countryCode = coin.getCountry().toString();
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.FIELD_YEAR, coin.getYear());
        data.put("coinType", coin.getType().toString());
        data.put("coinCountry", countryCode);

        String id = coin.getYear() + ":" + countryCode + ":" + coin.getType();
        CollectionReference col = db.collection(Constants.COLL_SPECIAL)
                .document(Constants.DOC_SPECIAL_II).collection(countryCode);

        saveDocument(col, id, data, callback);
    }

    // (GET): Lädt internationale Sondermünzen eines bestimmten Landes.
    public void getInternSpecialCoins(String countryCode, FirestoreDataCallback<List<Coin>> callback) {
        CollectionReference col = db.collection(Constants.COLL_SPECIAL)
                .document(Constants.DOC_SPECIAL_II).collection(countryCode);

        fetchList(col, doc -> {
            int y = doc.getLong(Constants.FIELD_YEAR).intValue();
            TableItem type = stringToTableItem(doc.getString("coinType"));
            TableItem country = StaticHelper.findCoinCountryItem(countryCode);
            return Coin.createInternSpecial(y, country, type);
        }, callback);
    }

    // (DELETE): Löscht eine internationale Sondermünze.
    public void deleteInternSpecialCoin(Coin coin, FirestoreCallback callback) {
        String countryCode = coin.getCountry().toString();
        String id = coin.getYear() + ":" + countryCode + ":" + coin.getType();
        CollectionReference col = db.collection(Constants.COLL_SPECIAL)
                .document(Constants.DOC_SPECIAL_II).collection(countryCode);

        deleteDocument(col, id, callback);
    }
}