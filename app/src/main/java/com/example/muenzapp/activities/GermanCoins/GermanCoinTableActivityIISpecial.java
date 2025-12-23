package com.example.muenzapp.activities.GermanCoins;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muenzapp.R;
import com.example.muenzapp.TableItem;
import com.example.muenzapp.activities.BaseCoinTableActivity;
import com.example.muenzapp.data.model.Coin;
import com.example.muenzapp.utils.FirestoreCallback;
import com.example.muenzapp.utils.FirestoreDataCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.example.muenzapp.R.drawable.table_border;
import static com.example.muenzapp.R.drawable.table_border_active;
import static com.example.muenzapp.TableItem.*;

public class GermanCoinTableActivityIISpecial extends BaseCoinTableActivity {

    private String[] tableYears;

    @Override
    protected int getLayoutId() {
        return R.layout.german_coin_table_layout_ii_special;
    }

    @Override
    protected void setupButtonIDs() {
        buttonIDs = new int[][]{
                {R.id.Item00, R.id.Item01, R.id.Item02, R.id.Item03, R.id.Item04, R.id.Item05, R.id.Item06},
                {R.id.Item10, R.id.Item11, R.id.Item12, R.id.Item13, R.id.Item14, R.id.Item15, R.id.Item16},
                {R.id.Item20, R.id.Item21, R.id.Item22, R.id.Item23, R.id.Item24, R.id.Item25, R.id.Item26},
                {R.id.Item30, R.id.Item31, R.id.Item32, R.id.Item33, R.id.Item34, R.id.Item35, R.id.Item36},
                {R.id.Item40, R.id.Item41, R.id.Item42, R.id.Item43, R.id.Item44, R.id.Item45, R.id.Item46},
                {R.id.Item50, R.id.Item51, R.id.Item52, R.id.Item53, R.id.Item54, R.id.Item55, R.id.Item56},
                {R.id.Item60, R.id.Item61, R.id.Item62, R.id.Item63, R.id.Item64, R.id.Item65, R.id.Item66},
                {R.id.Item70, R.id.Item71, R.id.Item72, R.id.Item73, R.id.Item74, R.id.Item75, R.id.Item76},
                {R.id.Item80, R.id.Item81, R.id.Item82, R.id.Item83, R.id.Item84, R.id.Item85, R.id.Item86},
                {R.id.Item90, R.id.Item91, R.id.Item92, R.id.Item93, R.id.Item94, R.id.Item95, R.id.Item96},
                {R.id.Item100, R.id.Item101, R.id.Item102, R.id.Item103, R.id.Item104, R.id.Item105, R.id.Item106},
                {R.id.Item110, R.id.Item111, R.id.Item112, R.id.Item113, R.id.Item114, R.id.Item115, R.id.Item116},
                {R.id.Item120, R.id.Item121, R.id.Item122, R.id.Item123, R.id.Item124, R.id.Item125, R.id.Item126},
                {R.id.Item130, R.id.Item131, R.id.Item132, R.id.Item133, R.id.Item134, R.id.Item135, R.id.Item136},
                {R.id.Item140, R.id.Item141, R.id.Item142, R.id.Item143, R.id.Item144, R.id.Item145, R.id.Item146},
                {R.id.Item150, R.id.Item151, R.id.Item152, R.id.Item153, R.id.Item154, R.id.Item155, R.id.Item156},
                {R.id.Item160, R.id.Item161, R.id.Item162, R.id.Item163, R.id.Item164, R.id.Item165, R.id.Item166},
                {R.id.Item170, R.id.Item171, R.id.Item172, R.id.Item173, R.id.Item174, R.id.Item175, R.id.Item176},
                {R.id.Item180, R.id.Item181, R.id.Item182, R.id.Item183, R.id.Item184, R.id.Item185, R.id.Item186},
                {R.id.Item190, R.id.Item191, R.id.Item192, R.id.Item193, R.id.Item194, R.id.Item195, R.id.Item196},
                {R.id.Item200, R.id.Item201, R.id.Item202, R.id.Item203, R.id.Item204, R.id.Item205, R.id.Item206},
                {R.id.Item210, R.id.Item211, R.id.Item212, R.id.Item213, R.id.Item214, R.id.Item215, R.id.Item216},
                {R.id.Item220, R.id.Item221, R.id.Item222, R.id.Item223, R.id.Item224, R.id.Item225, R.id.Item226},
                {R.id.Item230, R.id.Item231, R.id.Item232, R.id.Item233, R.id.Item234, R.id.Item235, R.id.Item236},
                {R.id.Item240, R.id.Item241, R.id.Item242, R.id.Item243, R.id.Item244, R.id.Item245, R.id.Item246},
                {R.id.Item250, R.id.Item251, R.id.Item252, R.id.Item253, R.id.Item254, R.id.Item255, R.id.Item256}
        };
    }

    @Override
    protected void setupDefaultTable() {
        findViewById(R.id.openAddingYearSpecialII).setOnClickListener((v) -> {
            Intent intent = new Intent(this, GermanAddingActivityIISpecial.class);
            startActivity(intent);
        });

        // Initialize table logic to match the size of buttonIDs
        // First row is Header: Year, Type, A, D, F, G, J
        // Subsequent rows: Default to X, TYP, COLLECTED...
        table = new TableItem[buttonIDs.length][7];

        // Header
        table[0] = new TableItem[]{II, TYP, A, D, F, G, J};

        // Body
        for(int i = 1; i < buttonIDs.length; i++) {
            table[i] = new TableItem[]{X, TYP, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED};
        }
    }

    @Override
    protected void onAdminCheckFinished(boolean isAdmin) {
        if (!isAdmin) {
            findViewById(R.id.openAddingYearSpecialII).setVisibility(View.GONE);
        }
    }

    @Override
    protected void loadDataFromRepository() {
        repository.getGermanSpecialCoins(new FirestoreDataCallback<List<Coin>>() {
            @Override
            public void onSuccess(List<Coin> data) {
                processData(data);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(GermanCoinTableActivityIISpecial.this, "Fehler beim Laden", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processData(List<Coin> allCoins) {
        List<Integer> coinYears = new ArrayList<>();
        Map<Integer, Map<TableItem, List<Coin>>> coinMap = new HashMap<>();

        for (Coin coin : allCoins) {
            int year = coin.getYear();
            TableItem type = coin.getType();

            if (!coinYears.contains(year)) {
                coinYears.add(year);
            }

            coinMap.putIfAbsent(year, new HashMap<>());
            Map<TableItem, List<Coin>> typeMap = coinMap.get(year);

            typeMap.putIfAbsent(type, new ArrayList<>());
            typeMap.get(type).add(coin);
        }
        Collections.sort(coinYears);

        tableYears = new String[coinYears.size() * 3];
        int pointer = 0;

        for (int year : coinYears) {
            Map<TableItem, List<Coin>> coinsOfYear = coinMap.get(year);
            TableItem[] types = {CC1, CC2, CC3};

            for (TableItem type : types) {
                if (coinsOfYear != null && coinsOfYear.containsKey(type)) {
                    int currentRowIndex = pointer + 1;
                    if (currentRowIndex >= table.length) break;

                    table[currentRowIndex][1] = type;
                    tableYears[pointer] = year < 10 ? "0" + year : String.valueOf(year);

                    for (Coin coinEntity : coinsOfYear.get(type)) {
                        int colIndex = getColumnForLetter(coinEntity.getLetter());
                        if (colIndex != -1) {
                            table[currentRowIndex][colIndex] = MISSING;
                        }
                    }
                    pointer++;
                }
            }
        }
        runOnUiThread(this::optimizeTableLayout);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void optimizeTableLayout() {
        Set<Integer> rowWithMissing = new TreeSet<>();
        Set<Integer> columnWithMissing = new TreeSet<>();

        for (int m = 1; m < table.length; m++) {
            for (int n = 1; n < table[1].length; n++) {
                if (table[m][n] == MISSING) {
                    rowWithMissing.add(m);
                    columnWithMissing.add(n);
                }
            }
        }

        int pointerRow = 1;
        int pointerColumn = 2; // Starts at col 2 (after year and type)

        for (int row : rowWithMissing) {
            // Set Year (Col 0)
            String yearText = (row - 1 < tableYears.length) ? tableYears[row - 1] : "";
            ((TextView) findViewById(buttonIDs[pointerRow][0])).setText(yearText);

            // Set Type (Col 1)
            ((TextView) findViewById(buttonIDs[pointerRow][1])).setText(String.valueOf(table[row][1]));
            table[pointerRow][1] = table[row][1];

            for (int column : columnWithMissing) {
                if (pointerRow == 1) {
                    ((TextView) findViewById(buttonIDs[0][pointerColumn])).setText(String.valueOf(table[0][column]));
                    table[0][pointerColumn] = table[0][column];
                }

                View btn = findViewById(buttonIDs[pointerRow][pointerColumn]);
                if (table[row][column] == MISSING) {
                    btn.setBackground(getDrawable(table_border));
                    table[pointerRow][pointerColumn] = MISSING;
                } else {
                    btn.setBackground(getDrawable(table_border_active));
                    table[pointerRow][pointerColumn] = COLLECTED;
                }
                pointerColumn++;
            }
            pointerRow++;
            pointerColumn = 2;
        }

        hideUnusedButtons(rowWithMissing, columnWithMissing, 1, 2);
    }

    @Override
    protected Coin createCoinFromSelection(int row, int col) {
        String yearString = "";
        try {
            yearString = ((TextView) findViewById(buttonIDs[row][0])).getText().toString();
        } catch (Exception e) {
            return null;
        }

        if(yearString.isEmpty()) return null;

        int year = Integer.parseInt(yearString);
        return Coin.createGermanSpecial(year, table[row][1], table[0][col]);
    }

    @Override
    protected void addToRepo(Coin coin, FirestoreCallback callback) {
        repository.addGermanSpecialCoin(coin, callback);
    }

    @Override
    protected void deleteFromRepo(Coin coin, FirestoreCallback callback) {
        repository.deleteGermanSpecialCoin(coin, callback);
    }

    @Override
    protected void setupAdminClickListeners() {
        for (int i = 1; i < table.length; i++) {
            for (int j = 2; j < table[1].length; j++) {
                findViewById(buttonIDs[i][j]).setOnClickListener(this::click);
            }
        }
    }

    private int getColumnForLetter(TableItem letter) {
        switch (letter) {
            case A: return 2;
            case D: return 3;
            case F: return 4;
            case G: return 5;
            case J: return 6;
            default: return -1;
        }
    }
}