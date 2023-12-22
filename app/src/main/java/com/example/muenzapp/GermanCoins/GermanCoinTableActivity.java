package com.example.muenzapp.GermanCoins;

import com.example.muenzapp.Database.*;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.muenzapp.GermanCoins.GermanOverviewActivity;
import com.example.muenzapp.R;
import com.example.muenzapp.TableItem;

import java.util.*;
import java.util.concurrent.Executors;

import static com.example.muenzapp.R.drawable.table_border;
import static com.example.muenzapp.TableItem.*;

public class GermanCoinTableActivity extends AppCompatActivity {

    private TableItem[][] table; // default Aussehen der Tabelle
    private final int[][] buttonIDs = {{R.id.Item00, R.id.Item01, R.id.Item02, R.id.Item03, R.id.Item04, R.id.Item05, R.id.Item06, R.id.Item07, R.id.Item08}, {R.id.Item10, R.id.Item11, R.id.Item12, R.id.Item13, R.id.Item14, R.id.Item15, R.id.Item16, R.id.Item17, R.id.Item18}, {R.id.Item20, R.id.Item21, R.id.Item22, R.id.Item23, R.id.Item24, R.id.Item25, R.id.Item26, R.id.Item27, R.id.Item28}, {R.id.Item30, R.id.Item31, R.id.Item32, R.id.Item33, R.id.Item34, R.id.Item35, R.id.Item36, R.id.Item37, R.id.Item38}, {R.id.Item40, R.id.Item41, R.id.Item42, R.id.Item43, R.id.Item44, R.id.Item45, R.id.Item46, R.id.Item47, R.id.Item48}, {R.id.Item50, R.id.Item51, R.id.Item52, R.id.Item53, R.id.Item54, R.id.Item55, R.id.Item56, R.id.Item57, R.id.Item58}}; //Alle Items der Tabelle: alle mit 0 sind TextViews, sonst Buttons

    private CoinDatabase coinDatabase;
    private CollectionDao collectionDao;
    private int coinYear;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO: Problem ist, dass beim Spaltenlöschen diese Verschoben werden, also sollte es anders gespeichert werden
        super.onCreate(savedInstanceState);
        setContentView(R.layout.german_coin_table_layout);

        findViewById(R.id.closeCoinTable).setOnClickListener((v) -> {
            Intent intent = new Intent(this, GermanOverviewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        coinYear = getIntent().getIntExtra("Year", -100);
        ((TextView) findViewById(buttonIDs[0][0])).setText(coinYear < 10 ? "0" + coinYear : coinYear + "");
        //table = new TableItem[][]{{X, ONE, TWO, FIVE},{A, COLLECTED, COLLECTED, COLLECTED},{D, COLLECTED, COLLECTED, COLLECTED},{F, COLLECTED, COLLECTED, COLLECTED}};
        table = new TableItem[][]{{X, ONE, TWO, FIVE, TEN, TWENTY, FIFTY, I, II},{A, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},{D, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},{F, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED}, {G, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED}, {J, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED}};

        // Daten aus Datenbank verwenden:
        coinDatabase = DatabaseClient.getInstance(this);
        collectionDao = coinDatabase.collectionDao();
    /*    Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                collectionDao.deleteAllCoins();
            }
        }); */
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CoinEntity> tableOfYear = collectionDao.getMissingCoinsOfYear(coinYear);
            //    System.out.println("Vor Delete: "+tableOfYear.size());
            for (CoinEntity coinEntity : tableOfYear) {
                TableItem coinLetter = coinEntity.getCoinLetter();
                TableItem coinValue = coinEntity.getCoinValue();
                int letterPlace = -100;
                switch (coinLetter) {
                    case A: {
                        letterPlace = 1;
                        break;
                    }
                    case D: {
                        letterPlace = 2;
                        break;
                    }
                    case F: {
                        letterPlace = 3;
                        break;
                    }
                    case G: {
                        letterPlace = 4;
                        break;
                    }
                    case J: {
                        letterPlace = 5;
                        break;
                    }
                }
                int valuePlace = -100;
                switch (coinValue) {
                    case ONE: {
                        valuePlace = 1;
                        break;
                    }
                    case TWO: {
                        valuePlace = 2;
                        break;
                    }
                    case FIVE: {
                        valuePlace = 3;
                        break;
                    }
                    case TEN: {
                        valuePlace = 4;
                        break;
                    }
                    case TWENTY: {
                        valuePlace = 5;
                        break;
                    }
                    case FIFTY: {
                        valuePlace = 6;
                        break;
                    }
                    case I: {
                        valuePlace = 7;
                        break;
                    }
                    case II: {
                        valuePlace = 8;
                        break;
                    }
                }
                //    System.out.println("Aus Datenbank hinzugefügt: table["+letterPlace+"]["+valuePlace+"]");
                table[letterPlace][valuePlace] = MISSING;
                findViewById(buttonIDs[letterPlace][valuePlace]).setBackground(getDrawable(table_border)); // eventuell unnötig da erst später gemacht
            }
            //        for (TableItem[] tableItems : table) {
            //            System.out.println(Arrays.toString(tableItems));
            //        }
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
                ((TextView)findViewById(buttonIDs[pointerRow][0])).setText(String.valueOf(table[row][0]));
                //            System.out.println("Buchstabe zu Platzieren: " + table[row][0]+" at Place in Array ["+pointerRow+"][0]");
                table[pointerRow][0] = table[row][0];
                for (int column : columnWithMissing) {
                    if (pointerRow == 1) {
                        ((TextView)findViewById(buttonIDs[0][pointerColumn])).setText(findValue(table[0][column]));
                        //                    System.out.println("Zahl zu Platzieren: "+table[0][column]+" at Place in Array [0]["+pointerColumn+"]");
                        table[0][pointerColumn] = table[0][column];
                    }
                    if (table[row][column] == MISSING) {
                        findViewById(buttonIDs[pointerRow][pointerColumn]).setBackground(getDrawable(table_border));
                        table[pointerRow][pointerColumn] = MISSING;
                        //                    System.out.println("log table at [" + row +"]["+column+"] as" + " Missing and Place in Array ["+pointerRow+"]["+pointerColumn+"]");
                    } else {
                        findViewById(buttonIDs[pointerRow][pointerColumn]).setBackground(getDrawable(R.drawable.table_border_active));
                        table[pointerRow][pointerColumn] = COLLECTED;
                        //                    System.out.println("log table at [" + row +"]["+column+"] as Collected " + "and Place in Array ["+pointerRow+"]["+pointerColumn+"]");
                    }
                    pointerColumn++;
                }
                pointerRow++;
                pointerColumn = 1;
            }


            //    for (TableItem[] tableItems : table) { // for debugging
            //        System.out.println(Arrays.toString(tableItems));
            //        }

            // andere Buttons ausblenden
            int lastRow = table.length - 1;
            int lastColumn = table[1].length - 1;
            for (int row = 1; row < table.length; row++) {
                if (!rowWithMissing.contains(row)) {
                    for (int k = 0; k < table[1].length; k++) {
                        findViewById(buttonIDs[lastRow][k]).setVisibility(View.INVISIBLE);
                        //                    System.out.println("Zeile zu Entfernen: "+ table[lastRow][k]+" at Place in Array ["+lastRow+"]["+k+"]");
                    }
                    lastRow--;
                }
            }
            for (int column = 1; column < table[1].length; column++) {
                if (!columnWithMissing.contains(column)) {
                    for (int k = 0; k < table.length; k++) {
                        findViewById(buttonIDs[k][lastColumn]).setVisibility(View.INVISIBLE);
                        //                System.out.println("Spalte zu Entfernen: "+ table[k][lastColumn]+" at Place in Array ["+k+"]["+lastColumn+"]");
                    }
                    lastColumn--;
                }
            }
            /*
            // Problem Row and Collumn geht nur wenn gleich
            for (int rowANDcollumn = 1; rowANDcollumn < table.length; rowANDcollumn++) {
                if (!rowWithMissing.contains(rowANDcollumn)) {
                    for (int k = 0; k < table[1].length; k++) {
                        findViewById(buttonIDs[lastRow][k]).setVisibility(View.INVISIBLE);
                        System.out.println("Zeile zu Entfernen: "+ table[lastRow][k]+" at Place in Array ["+lastRow+"]["+k+"]");
                    }
                    lastRow--;
                }
                if (!columnWithMissing.contains(rowANDcollumn)) {
                    for (int k = 0; k < table.length; k++) { // zu table.length geändert
                        findViewById(buttonIDs[k][lastColumn]).setVisibility(View.INVISIBLE);
                        System.out.println("Spalte zu Entfernen: "+ table[k][lastColumn]+" at Place in Array ["+k+"]["+lastColumn+"]");
                    }
                    lastColumn--;
                }
            }

             */
        });
        for (int i = 1; i < table.length; i++) {
            for (int j = 1; j < table[1].length; j++) {
                findViewById(buttonIDs[i][j]).setOnClickListener(this::click);
            }
        }

        // alle anderen Buttons ausblenden
        //TODO Problem: wenn falsch entfernt wurde wieder rückgängig machen
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
                    //TODO anstelle von n und m speichern eventuell Buchstabe und Zahl speichern
                    row = n;
                    column = m;
                    break;
                }
            }
            if (found) break;
        }
        boolean isActive = (COLLECTED == table[row][column]);
        if (isActive) {
            table[row][column] = MISSING;
            Executors.newSingleThreadExecutor().execute(() -> {
                CoinEntity coinEntity = new CoinEntity();
                coinEntity.setCoinValue(table[0][row]);
                coinEntity.setCoinLetter(table[column][0]);
                coinEntity.setCoinYear(coinYear);
                collectionDao.insertCoin(coinEntity);
            });
            view.setBackground(getDrawable(table_border)); // wieder normal
        } else {
            table[row][column] = COLLECTED;
            view.setBackground(getDrawable(R.drawable.table_border_active));
            Executors.newSingleThreadExecutor().execute(() -> {
                collectionDao.foundCoin(coinYear, table[0][column], table[row][0]);
                //TODO Problem in table ist noch alte wert
                //            System.out.println("CoinValue: "+table[0][column]);
                //            System.out.println("CoinLetter: "+table[row][0]);
                //    collectionDao.foundCoinByID(buttonIDs[row][column]);
                //    List<CoinEntity> coins = collectionDao.getMissingCoinsOfYear(coinYear);
                //            System.out.println("Nach Delete: "+coins.size());
            });
        }

    }


    // Helper-Methoden:
    private String findValue(TableItem tableItem) {
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
}