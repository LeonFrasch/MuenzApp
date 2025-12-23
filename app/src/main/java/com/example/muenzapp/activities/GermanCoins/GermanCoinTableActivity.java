package com.example.muenzapp.activities.GermanCoins;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muenzapp.R;
import com.example.muenzapp.TableItem;
import com.example.muenzapp.activities.BaseCoinTableActivity;
import com.example.muenzapp.data.model.Coin;
import com.example.muenzapp.utils.FirestoreCallback;
import com.example.muenzapp.utils.FirestoreDataCallback;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.example.muenzapp.R.drawable.table_border;
import static com.example.muenzapp.R.drawable.table_border_active;
import static com.example.muenzapp.StaticHelper.findValueString;
import static com.example.muenzapp.TableItem.*;

public class GermanCoinTableActivity extends BaseCoinTableActivity {

    private int coinYear;

    @Override
    protected int getLayoutId() {
        return R.layout.german_coin_table_layout;
    }

    @Override
    protected void setupButtonIDs() {
        buttonIDs = new int[][]{
                {R.id.Item00, R.id.Item01, R.id.Item02, R.id.Item03, R.id.Item04, R.id.Item05, R.id.Item06, R.id.Item07, R.id.Item08},
                {R.id.Item10, R.id.Item11, R.id.Item12, R.id.Item13, R.id.Item14, R.id.Item15, R.id.Item16, R.id.Item17, R.id.Item18},
                {R.id.Item20, R.id.Item21, R.id.Item22, R.id.Item23, R.id.Item24, R.id.Item25, R.id.Item26, R.id.Item27, R.id.Item28},
                {R.id.Item30, R.id.Item31, R.id.Item32, R.id.Item33, R.id.Item34, R.id.Item35, R.id.Item36, R.id.Item37, R.id.Item38},
                {R.id.Item40, R.id.Item41, R.id.Item42, R.id.Item43, R.id.Item44, R.id.Item45, R.id.Item46, R.id.Item47, R.id.Item48},
                {R.id.Item50, R.id.Item51, R.id.Item52, R.id.Item53, R.id.Item54, R.id.Item55, R.id.Item56, R.id.Item57, R.id.Item58}
        };
    }

    @Override
    protected void setupDefaultTable() {
        coinYear = getIntent().getIntExtra("Year", -100);
        ((TextView) findViewById(buttonIDs[0][0])).setText(coinYear < 10 ? "0" + coinYear : String.valueOf(coinYear));

        table = new TableItem[][]{
                {X, ONE, TWO, FIVE, TEN, TWENTY, FIFTY, I, II},
                {A, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {D, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {F, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {G, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {J, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED}};
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
                Log.e("Activity", "Fehler beim Laden", e);
                Toast.makeText(GermanCoinTableActivity.this, "Fehler beim Laden", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateTableWithData(List<Coin> coins) {
        for (Coin coin : coins) {
            TableItem letter = coin.getLetter();
            TableItem value = coin.getValue();

            int letterIdx = getLetterindex(letter);
            int valueIdx = getValueIndex(value);

            if (letterIdx != -1 && valueIdx != -1) {
                table[letterIdx][valueIdx] = MISSING;
                findViewById(buttonIDs[letterIdx][valueIdx]).setBackground(getDrawable(table_border));
            }
        }
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
                findViewById(buttonIDs[i][j]).setOnClickListener(this::click);
            }
        }
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

        for (int row : rowWithMissing) {
            ((TextView)findViewById(buttonIDs[pointerRow][0])).setText(String.valueOf(table[row][0]));
            table[pointerRow][0] = table[row][0];

            int pointerColumn = 1;
            for (int column : columnWithMissing) {
                if (pointerRow == 1) {
                    ((TextView)findViewById(buttonIDs[0][pointerColumn])).setText(findValueString(table[0][column]));
                    table[0][pointerColumn] = table[0][column];
                }

                if (table[row][column] == MISSING) {
                    findViewById(buttonIDs[pointerRow][pointerColumn]).setBackground(getDrawable(table_border));
                    table[pointerRow][pointerColumn] = MISSING;
                } else {
                    findViewById(buttonIDs[pointerRow][pointerColumn]).setBackground(getDrawable(table_border_active));
                    table[pointerRow][pointerColumn] = COLLECTED;
                }
                pointerColumn++;
            }
            pointerRow++;
        }

        hideUnusedButtons(rowWithMissing, columnWithMissing, 1, 1);
    }

    private int getLetterindex(TableItem letter) {
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