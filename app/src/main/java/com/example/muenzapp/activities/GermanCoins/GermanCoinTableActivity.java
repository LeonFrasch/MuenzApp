package com.example.muenzapp.activities.GermanCoins;

import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import com.example.muenzapp.R;
import com.example.muenzapp.TableItem;
import com.example.muenzapp.activities.BaseCoinTableActivity;
import com.example.muenzapp.data.model.Coin;
import com.example.muenzapp.utils.FirestoreCallback;
import com.example.muenzapp.utils.FirestoreDataCallback;

import java.util.List;

import static com.example.muenzapp.StaticHelper.findValueString;
import static com.example.muenzapp.TableItem.*;

public class GermanCoinTableActivity extends BaseCoinTableActivity {

    private int coinYear;

    @Override
    protected void initTableLogic() {
        Button btnAdd = findViewById(R.id.openAddingYearSpecialII);
        if (btnAdd != null) {
            // Check if admin inside the callback or hide later,
            // but initially set listener so it's ready.
            btnAdd.setOnClickListener(v -> {
                Intent intent = new Intent(this, GermanAddingActivityIISpecial.class);
                startActivity(intent);
            });
        }

        coinYear = getIntent().getIntExtra("Year", -100);

        table = new TableItem[][]{
                {X, ONE, TWO, FIVE, TEN, TWENTY, FIFTY, I, II},
                {A, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {D, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {F, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {G, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {J, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED}
        };
    }

    /**
     * Handles text formatting for Header Row (1, 2, 5...) and Top-Left Year
     */
    @Override
    protected String getTextForCell(int row, int col) {
        // Top-Left Corner: Year
        if (row == 0 && col == 0) {
            return coinYear < 10 ? "0" + coinYear : String.valueOf(coinYear);
        }
        // Header Row: Values (ONE -> "1")
        if (row == 0 && col > 0) {
            return findValueString(table[row][col]);
        }
        // Letters (A, D, F): Default toString
        return super.getTextForCell(row, col);
    }

    @Override
    protected void loadDataFromRepository() {
        repository.getGermanCoinsByYear(coinYear, new FirestoreDataCallback<List<Coin>>() {
            @Override
            public void onSuccess(List<Coin> coins) {
                runOnUiThread(() -> updateTableWithData(coins));
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(GermanCoinTableActivity.this, "Fehler beim Laden", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTableWithData(List<Coin> coins) {
        // 1. Mark the missing items in the table array
        for (Coin coin : coins) {
            int letterIdx = getLetterIndex(coin.getLetter());
            int valueIdx = getValueIndex(coin.getValue());

            if (letterIdx != -1 && valueIdx != -1) {
                table[letterIdx][valueIdx] = MISSING;
            }
        }
        // 2. Refresh the view visibility (using the fixed Base logic)
        optimizeTableLayout();
    }

    @Override
    protected Coin createCoinFromSelection(int row, int col) {
        return Coin.createGermanStandard(coinYear, table[0][col], table[row][0]);
    }

    @Override
    protected void addToRepo(Coin coin, FirestoreCallback callback) {
        repository.addGermanCoin(coin, callback);
    }

    @Override
    protected void deleteFromRepo(Coin coin, FirestoreCallback callback) {
        repository.deleteGermanCoin(coin, callback);
    }

    @Override
    protected void setupAdminClickListeners() {
        for (int i = 1; i < table.length; i++) {
            for (int j = 1; j < table[1].length; j++) {
                if (cellViews[i][j] != null) {
                    cellViews[i][j].setOnClickListener(this::click);
                }
            }
        }
    }

    private int getLetterIndex(TableItem letter) {
        switch (letter) {
            case A: return 1;
            case D: return 2;
            case F: return 3;
            case G: return 4;
            case J: return 5;
            default: return -1;
        }
    }

    private int getValueIndex(TableItem value) {
        switch (value) {
            case ONE: return 1;
            case TWO: return 2;
            case FIVE: return 3;
            case TEN: return 4;
            case TWENTY: return 5;
            case FIFTY: return 6;
            case I: return 7;
            case II: return 8;
            default: return -1;
        }
    }
}