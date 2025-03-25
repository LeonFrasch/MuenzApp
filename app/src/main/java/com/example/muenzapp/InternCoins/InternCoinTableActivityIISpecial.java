package com.example.muenzapp.InternCoins;

import static android.content.ContentValues.TAG;
import static com.example.muenzapp.R.drawable.table_border;
import static com.example.muenzapp.R.drawable.table_border_active;
import static com.example.muenzapp.R.drawable.table_border_active_red;
import static com.example.muenzapp.R.drawable.table_border_red;
import static com.example.muenzapp.StaticHelper.findCoinCountryItem;
import static com.example.muenzapp.StaticHelper.stringToTableItem;
import static com.example.muenzapp.TableItem.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muenzapp.Database.IISpecial;
import com.example.muenzapp.GermanSpecialIIAddingActivity;
import com.example.muenzapp.InternSpecialIIAddingActivity;
import com.example.muenzapp.R;
import com.example.muenzapp.StartingPageActivity;
import com.example.muenzapp.TableItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

public class InternCoinTableActivityIISpecial extends AppCompatActivity {
    private TableItem[][] table; // default Aussehen der Tabelle
    private String[] tableYears;
    private List<IISpecial> collect;
    private List<IISpecial> missing;
    private TableItem coinCountry;

    private final int[][] buttonIDs = {{R.id.Item00, R.id.Item01, R.id.Item02, R.id.Item03},
            {R.id.Item10, R.id.Item11, R.id.Item12, R.id.Item13},
            {R.id.Item20, R.id.Item21, R.id.Item22, R.id.Item23},
            {R.id.Item30, R.id.Item31, R.id.Item32, R.id.Item33},
            {R.id.Item40, R.id.Item41, R.id.Item42, R.id.Item43},
            {R.id.Item50, R.id.Item51, R.id.Item52, R.id.Item53},
            {R.id.Item60, R.id.Item61, R.id.Item62, R.id.Item63},
            {R.id.Item70, R.id.Item71, R.id.Item72, R.id.Item73},
            {R.id.Item80, R.id.Item81, R.id.Item82, R.id.Item83},
            {R.id.Item90, R.id.Item91, R.id.Item92, R.id.Item93},
            {R.id.Item100, R.id.Item101, R.id.Item102, R.id.Item103},
            {R.id.Item110, R.id.Item111, R.id.Item112, R.id.Item113},
            {R.id.Item120, R.id.Item121, R.id.Item122, R.id.Item123},
            {R.id.Item130, R.id.Item131, R.id.Item132, R.id.Item133},
            {R.id.Item140, R.id.Item141, R.id.Item142, R.id.Item143},
            {R.id.Item150, R.id.Item151, R.id.Item152, R.id.Item153},
            {R.id.Item160, R.id.Item161, R.id.Item162, R.id.Item163},
            {R.id.Item170, R.id.Item171, R.id.Item172, R.id.Item173},
            {R.id.Item180, R.id.Item181, R.id.Item182, R.id.Item183},
            {R.id.Item190, R.id.Item191, R.id.Item192, R.id.Item193},
            {R.id.Item200, R.id.Item201, R.id.Item202, R.id.Item203},
            {R.id.Item210, R.id.Item211, R.id.Item212, R.id.Item213},
            {R.id.Item220, R.id.Item221, R.id.Item222, R.id.Item223},
            {R.id.Item230, R.id.Item231, R.id.Item232, R.id.Item233},
            {R.id.Item240, R.id.Item241, R.id.Item242, R.id.Item243},
            {R.id.Item250, R.id.Item251, R.id.Item252, R.id.Item253}}; //Alle Items der Tabelle: alle mit 0 sind TextViews, sonst Buttons
    // TODO hier erweiterbar
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private boolean isAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intern_coin_table_layout_ii_special);

        missing = new ArrayList<>();
        collect = new ArrayList<>();

        coinCountry = findCoinCountryItem(getIntent().getStringExtra("coinCountry"));

        findViewById(R.id.openAddingInternYearSpecialII).setOnClickListener((v) -> {
            Intent intent = new Intent(this, InternSpecialIIAddingActivity.class);
            intent.putExtra("coinCountry", getIntent().getStringExtra("coinCountry"));
            startActivity(intent);
        });

        //Logik Button zum Zurückgehen
        findViewById(R.id.closeCoinTable).setOnClickListener((v) -> {

            Executors.newSingleThreadExecutor().execute(() -> {
                for (IISpecial entity : missing) {
                    Map<String, Object> coin = new HashMap<>();
                    coin.put("coinYear", entity.getCoinYear());
                    coin.put("coinType", entity.getCoinType());
                    coin.put("coinCountry", entity.getCoinCountry());
                    String filename = entity.getCoinYear() + ":" + entity.getCoinCountry() + ":" + entity.getCoinType();
                    db.collection("IISonder").document(filename)
                            .set(coin, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                            .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                }
                for (IISpecial entity : collect) {
                    String filename = entity.getCoinYear() + ":" + entity.getCoinCountry() + ":" + entity.getCoinType();
                    System.out.println(filename);
                    db.collection("IISonder").document(filename)
                            .delete()
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                            .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                }
                if (!missing.isEmpty() || !collect.isEmpty()) {
                    runOnUiThread(() -> {
                        Toast.makeText(InternCoinTableActivityIISpecial.this, "Änderungen erfolgreich übernommen!", Toast.LENGTH_SHORT).show();
                    });
                }
            });

            Intent intent = new Intent(this, InternCoinTableActivity.class);
            intent.putExtra("coinCountry", getIntent().getStringExtra("coinCountry"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        table = new TableItem[][]{{II, CC1, CC2, CC3},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED}
        };
        //TODO hier erweiterbar

        db = FirebaseFirestore.getInstance(); // Database instance

        auth = FirebaseAuth.getInstance(); // Authentication instance

        db.collection("userRole").get().addOnSuccessListener(queryDocumentSnapshots0 -> {
            List<String> adminUIDs = new ArrayList<>();
            if (!queryDocumentSnapshots0.isEmpty()) {
                for (DocumentSnapshot document : queryDocumentSnapshots0.getDocuments()) {
                    Map<String, Object> admins = document.getData();
                    adminUIDs.add(admins.get("UID").toString());
                }
            }
            if (adminUIDs.contains(auth.getUid())) { // User ist admin
                isAdmin = true;
            }
            Executors.newSingleThreadExecutor().execute(() -> {
                List<Integer> coinYears = new ArrayList<>(); // list of all years with missing coins in db

                Map<Integer, List<IISpecial>> coinMap = new HashMap<>(); // map with missing year and corresponding missing values
                db.collection("IISonder").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Map<String, Object> data = document.getData();
                            TableItem coinType = stringToTableItem(data.get("coinType").toString());
                            Long coinYearLong = (Long) data.get("coinYear");
                            int coinYear = (int) coinYearLong.longValue();
                            if (!coinYears.contains(coinYear)) {
                                coinYears.add(coinYear);
                            }
                            IISpecial entity = new IISpecial();
                            entity.setCoinYear(coinYear);
                            entity.setCoinCountry(coinCountry);
                            entity.setCoinType(coinType);
                            List<IISpecial> store = new ArrayList<>(); // coins
                            if (coinMap.containsKey(coinYear)) {
                                store = coinMap.remove(coinYear); // and f. line for correctly editing the list
                            }
                            store.add(entity);
                            coinMap.put(coinYear, store);
                        }
                    }
                    Collections.sort(coinYears);
                    tableYears = new String[coinYears.size()];
                    int pointer = 0; // points at year
                    for (int year : coinYears) { // eventuell bereits hier in der Tabelle das Jahr setzen
                        for (IISpecial coinEntity : coinMap.get(year)) {
                            TableItem coinType = coinEntity.getCoinType();
                            int valuePlace = -100;
                            switch (coinType) {
                                case CC1: {
                                    valuePlace = 1;
                                    break;
                                }
                                case CC2: {
                                    valuePlace = 2;
                                    break;
                                }
                                case CC3: {
                                    valuePlace = 3;
                                    break;
                                }
                            } // setzt valuePlace
                            if (pointer + 1 == 26) break; //TODO hier erweitern
                            table[pointer + 1][valuePlace] = MISSING;
                        }
                        tableYears[pointer++] = year < 10 ? "0"+ year : year + "";
                    }
                    //                for (TableItem[] tableItems : table) { // for debugging
                    //                    System.out.println(Arrays.toString(tableItems));
                    //                }
                    // Unwichtige Zeilen und Spalten entfernen
                    Set<Integer> rowWithMissing = new HashSet<>();
                    Set<Integer> columnWithMissing = new HashSet<>();
                    for (int m = 1; m < table.length; m++) {
                        for (int n = 1; n < table[1].length; n++) {
                            if (table[m][n] == MISSING) {
                                rowWithMissing.add(m);
                                columnWithMissing.add(n);
                            }
                        }
                    }
                    rowWithMissing = new HashSet<>(rowWithMissing);
                    columnWithMissing = new HashSet<>(columnWithMissing);

                    //        System.out.println("Reihe mit Missing: "+ Arrays.toString(rowWithMissing.toArray()));
                    //        System.out.println("Zeile mit Missing: "+ Arrays.toString(columnWithMissing.toArray()));
                    int pointerRow = 1;
                    int pointerColumn = 1;
                    for (int row : rowWithMissing) {
                        ((TextView)findViewById(buttonIDs[pointerRow][0])).setText(tableYears[row - 1] + ""); // Jahr
                        //            System.out.println("Jahr zu Platzieren: " + table[row][0]+" at Place in Array ["+pointerRow+"][0]");
                        table[pointerRow][0] = table[row][0]; // TODO muss noch angepasst werden, da die Jahreszahl nicht als Item vorhanden ist Nein, da später nicht verewendet, nur mit tableYears[]
                        for (int column : columnWithMissing) {
                            if (pointerRow == 1) {
                                ((TextView)findViewById(buttonIDs[0][pointerColumn])).setText(table[0][column] + "");
                                //                    System.out.println("Letter zu Platzieren: "+table[0][column]+" at Place in Array [0]["+pointerColumn+"]");
                                table[0][pointerColumn] = table[0][column];
                            }
                            if (table[row][column] == MISSING) {
                                findViewById(buttonIDs[pointerRow][pointerColumn]).setBackground(getDrawable(table_border));
                                table[pointerRow][pointerColumn] = MISSING;
                                //                   System.out.println("log table at [" + row +"]["+column+"] as" + " Missing and Place in Array ["+pointerRow+"]["+pointerColumn+"]");
                            } else {
                                findViewById(buttonIDs[pointerRow][pointerColumn]).setBackground(getDrawable(table_border_active));
                                table[pointerRow][pointerColumn] = COLLECTED;
                                //                    System.out.println("log table at [" + row +"]["+column+"] as Collected " + "and Place in Array ["+pointerRow+"]["+pointerColumn+"]");
                            }
                            pointerColumn++;
                        }
                        pointerRow++;
                        pointerColumn = 1;
                    }

                    // andere Buttons ausblenden
                    int lastRow = table.length - 1;
                    int lastColumn = table[1].length - 1;
                    //System.out.println(Arrays.toString(rowWithMissing.toArray()));
                    for (int row = 1; row < table.length; row++) {
                        if (!rowWithMissing.contains(row)) {
                            for (int k = 0; k < table[1].length; k++) {
                                findViewById(buttonIDs[lastRow][k]).setVisibility(View.GONE);
                                //                    System.out.println("Zeile zu Entfernen: "+ table[lastRow][k]+" at Place in Array ["+lastRow+"]["+k+"]");
                            }
                            lastRow--;
                        }
                    }
                    //System.out.println(Arrays.toString(columnWithMissing.toArray()));
                    for (int column = 1; column < table[1].length; column++) {
                        if (!columnWithMissing.contains(column)) {
                            for (int k = 0; k < table.length; k++) {
                                findViewById(buttonIDs[k][lastColumn]).setVisibility(View.GONE);
                                //                System.out.println("Spalte zu Entfernen: "+ table[k][lastColumn]+" at Place in Array ["+k+"]["+lastColumn+"]");
                            }
                            lastColumn--;
                        }
                    }
                    if (true) { //isAdmin
                        for (int i = 1; i < table.length; i++) {
                            for (int j = 1; j < table[1].length; j++) {
                                findViewById(buttonIDs[i][j]).setOnClickListener(this::click);
                            }
                        }
                    }
                    if (!isAdmin) {

                    }
                }).addOnFailureListener(e -> System.out.println("FAILURE!!"));
            });
        });
    }
    private int row = -100;
    private int column = -100;
    public void click(View view) {

        //    for (TableItem[] tableItems : table) {
        //        System.out.println(Arrays.toString(tableItems));
        //    }

        //Hintergrund ändern
        int id = view.getId();
        boolean found = false;
        for (int n = 1; n < table.length; n++) {
            for (int m = 1; m < table[1].length; m++) {
                if (buttonIDs[n][m] == id) {
                    found = true;
                    row = n;
                    column = m;
                    break;
                }
            }
            if (found) break;
        } // row und column zuweisen
        boolean isActive = (COLLECTED == table[row][column]);
        if (isActive) { // not in Database
            IISpecial entity = new IISpecial();
            entity.setCoinType(table[0][column]);
            entity.setCoinCountry(coinCountry);
            entity.setCoinYear(Integer.parseInt(tableYears[row - 1]));
            if (!missing.contains(entity)) {
                missing.add(entity);
                view.setBackground(getDrawable(table_border_red));
            } else {
                missing.remove(entity);
                view.setBackground(getDrawable(table_border_active));
            }
        } else { // not active
            IISpecial entity = new IISpecial();
            entity.setCoinType(table[0][column]);
            entity.setCoinCountry(coinCountry);
            entity.setCoinYear(Integer.parseInt(tableYears[row - 1]));
            if (!collect.contains(entity)) {
                collect.add(entity);
                view.setBackground(getDrawable(table_border_active_red));
            } else {
                collect.remove(entity);
                view.setBackground(getDrawable(table_border));
            }
        }
        if (!missing.isEmpty() || !collect.isEmpty()) { //something will be changed
            findViewById(R.id.closeCoinTable).setBackground(getDrawable(table_border_red));
        } else {
            findViewById(R.id.closeCoinTable).setBackground(null);
        }
    }
}