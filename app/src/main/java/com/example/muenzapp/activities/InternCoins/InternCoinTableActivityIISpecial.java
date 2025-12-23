package com.example.muenzapp.activities.InternCoins;

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

import static com.example.muenzapp.StaticHelper.findCoinCountryItem;
import static com.example.muenzapp.TableItem.*;

public class InternCoinTableActivityIISpecial extends BaseCoinTableActivity {

    private TableItem coinCountry;
    private String countryStringRaw;
    private final int MAX_ROWS = 260;

    @Override
    protected void initTableLogic() {
        countryStringRaw = getIntent().getStringExtra("coinCountry");
        coinCountry = findCoinCountryItem(countryStringRaw);

        // FIX: Make visible and set listener
        View btnAdd = findViewById(R.id.openAddingYear);
        if (btnAdd != null) {
            btnAdd.setVisibility(View.VISIBLE);
            btnAdd.setOnClickListener((v) -> {
                Intent intent = new Intent(this, InternAddingActivityIISpecial.class);
                intent.putExtra("coinCountry", countryStringRaw);
                startActivity(intent);
            });
        }

        table = new TableItem[MAX_ROWS][4];
        table[0] = new TableItem[]{coinCountry, CC1, CC2, CC3};

        for(int i = 1; i < MAX_ROWS; i++) {
            table[i] = new TableItem[]{X, COLLECTED, COLLECTED, COLLECTED};
        }
    }

    @Override
    protected String getTextForCell(int row, int col) {
        if (row == 0 && col == 0) return coinCountry.toString();
        return super.getTextForCell(row, col);
    }

    @Override
    protected void onAdminCheckFinished(boolean isAdmin) {
        if (!isAdmin) {
            findViewById(R.id.openAddingYear).setVisibility(View.GONE);
        }
    }

    @Override
    protected void loadDataFromRepository() {
        repository.getInternSpecialCoins(countryStringRaw, new FirestoreDataCallback<List<Coin>>() {
            @Override
            public void onSuccess(List<Coin> coins) {
                processData(coins);
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(InternCoinTableActivityIISpecial.this, "Fehler beim Laden", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processData(List<Coin> coins) {
        List<Integer> coinYears = new ArrayList<>();
        Map<Integer, List<Coin>> coinMap = new HashMap<>();

        for (Coin coin : coins) {
            int year = coin.getYear();
            if (!coinYears.contains(year)) coinYears.add(year);
            coinMap.putIfAbsent(year, new ArrayList<>());
            coinMap.get(year).add(coin);
        }
        Collections.sort(coinYears);

        int pointer = 0;
        for (int year : coinYears) {
            if (pointer + 1 >= table.length) break;

            String yearString = year < 10 ? "0" + year : String.valueOf(year);
            if(cellViews[pointer+1][0] instanceof TextView) {
                ((TextView) cellViews[pointer+1][0]).setText(yearString);
            }

            for (Coin coin : coinMap.get(year)) {
                int colIndex = getColumnForType(coin.getType());
                if (colIndex != -1) {
                    table[pointer + 1][colIndex] = MISSING;
                }
            }
            pointer++;
        }
        runOnUiThread(this::optimizeTableLayout);
    }

    @Override
    protected Coin createCoinFromSelection(int row, int col) {
        String yearString = "";
        try {
            yearString = ((TextView) cellViews[row][0]).getText().toString();
        } catch (Exception e) { return null; }

        if (yearString.isEmpty()) return null;
        int year = Integer.parseInt(yearString);

        return Coin.createInternSpecial(year, coinCountry, table[0][col]);
    }

    @Override
    protected void addToRepo(Coin coin, FirestoreCallback callback) {
        repository.addInternSpecialCoin(coin, callback);
    }

    @Override
    protected void deleteFromRepo(Coin coin, FirestoreCallback callback) {
        repository.deleteInternSpecialCoin(coin, callback);
    }

    @Override
    protected void setupAdminClickListeners() {
        for (int i = 1; i < table.length; i++) {
            for (int j = 1; j < table[1].length; j++) {
                if(cellViews[i][j] != null) {
                    cellViews[i][j].setOnClickListener(this::click);
                }
            }
        }
    }

    @Override
    protected void goBack() {
        Intent intent = new Intent(this, InternCoinTableActivity.class);
        intent.putExtra("coinCountry", countryStringRaw);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private int getColumnForType(TableItem type) {
        switch (type) {
            case CC1: return 1;
            case CC2: return 2;
            case CC3: return 3;
            default: return -1;
        }
    }
}