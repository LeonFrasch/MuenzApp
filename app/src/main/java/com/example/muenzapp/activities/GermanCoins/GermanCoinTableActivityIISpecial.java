package com.example.muenzapp.activities.GermanCoins;

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

import static com.example.muenzapp.TableItem.*;

public class GermanCoinTableActivityIISpecial extends BaseCoinTableActivity {

    private final int PREDICTED_ROWS = 260;

    @Override
    protected void initTableLogic() {
        View btnAdd = findViewById(R.id.openAddingYear);
        if (btnAdd != null) {
            // Initially visible (or logic to hide if not admin in onAdminCheckFinished)
            btnAdd.setVisibility(View.VISIBLE);
            btnAdd.setOnClickListener(v -> {
                Intent intent = new Intent(this, GermanAddingActivityIISpecial.class);
                startActivity(intent);
            });
        }

        table = new TableItem[PREDICTED_ROWS][7];
        table[0] = new TableItem[]{II, TYP, A, D, F, G, J};

        for(int i = 1; i < PREDICTED_ROWS; i++) {
            table[i] = new TableItem[]{X, TYP, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED};
        }
    }

    /**
     * FIX: Column 1 is Type, so it must be Text, not a Button.
     */
    @Override
    protected boolean isCellText(int row, int col) {
        return super.isCellText(row, col) || col == 1;
    }

    @Override
    protected void onAdminCheckFinished(boolean isAdmin) {
        if (!isAdmin) {
            findViewById(R.id.openAddingYear).setVisibility(View.GONE);
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
            if (!coinYears.contains(year)) coinYears.add(year);

            coinMap.putIfAbsent(year, new HashMap<>());
            coinMap.get(year).putIfAbsent(type, new ArrayList<>());
            coinMap.get(year).get(type).add(coin);
        }
        Collections.sort(coinYears);

        int pointer = 0;

        for (int year : coinYears) {
            Map<TableItem, List<Coin>> coinsOfYear = coinMap.get(year);
            TableItem[] types = {CC1, CC2, CC3};

            for (TableItem type : types) {
                if (coinsOfYear != null && coinsOfYear.containsKey(type)) {
                    int currentRowIndex = pointer + 1;
                    if (currentRowIndex >= table.length) break;

                    // 1. Update Year Text
                    String yearString = year < 10 ? "0" + year : String.valueOf(year);
                    if (cellViews[currentRowIndex][0] instanceof TextView) {
                        ((TextView) cellViews[currentRowIndex][0]).setText(yearString);
                    }

                    // 2. Update Type Text
                    table[currentRowIndex][1] = type;
                    if (cellViews[currentRowIndex][1] instanceof TextView) {
                        ((TextView) cellViews[currentRowIndex][1]).setText(type.toString());
                    }

                    // 3. Mark Missing
                    for (Coin coinEntity : coinsOfYear.get(type)) {
                        int colIndex = getColumnForLetter(coinEntity.getLetter());
                        if (colIndex != -1) table[currentRowIndex][colIndex] = MISSING;
                    }
                    pointer++;
                }
            }
        }
        runOnUiThread(this::optimizeTableLayout);
    }

    @Override
    protected Coin createCoinFromSelection(int row, int col) {
        String yearString = "";
        try {
            yearString = ((TextView) cellViews[row][0]).getText().toString();
        } catch (Exception e) { return null; }

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
            // Start at Col 2, because 0=Year(Text), 1=Type(Text)
            for (int j = 2; j < table[1].length; j++) {
                if (cellViews[i][j] != null) {
                    cellViews[i][j].setOnClickListener(this::click);
                }
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