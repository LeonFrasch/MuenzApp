package com.example.muenzapp.activities.InternCoins;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muenzapp.R;
import com.example.muenzapp.TableItem;
import com.example.muenzapp.activities.BaseCoinTableActivity;
import com.example.muenzapp.activities.StartingPageActivity;
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
import static com.example.muenzapp.StaticHelper.findCoinCountryItem;
import static com.example.muenzapp.StaticHelper.findValueString;
import static com.example.muenzapp.TableItem.*;

public class InternCoinTableActivity extends BaseCoinTableActivity {

    private String[] tableYears;
    private TableItem coinCountry;
    private String countryStringRaw;

    @Override
    protected int getLayoutId() {
        return R.layout.intern_coin_table_layout;
    }

    @Override
    protected void setupButtonIDs() {
        buttonIDs = new int[][]{
                {R.id.Item00, R.id.Item01, R.id.Item02, R.id.Item03, R.id.Item04, R.id.Item05, R.id.Item06, R.id.Item07, R.id.Item08},
                {R.id.Item10, R.id.Item11, R.id.Item12, R.id.Item13, R.id.Item14, R.id.Item15, R.id.Item16, R.id.Item17, R.id.Item18},
                {R.id.Item20, R.id.Item21, R.id.Item22, R.id.Item23, R.id.Item24, R.id.Item25, R.id.Item26, R.id.Item27, R.id.Item28},
                {R.id.Item30, R.id.Item31, R.id.Item32, R.id.Item33, R.id.Item34, R.id.Item35, R.id.Item36, R.id.Item37, R.id.Item38},
                {R.id.Item40, R.id.Item41, R.id.Item42, R.id.Item43, R.id.Item44, R.id.Item45, R.id.Item46, R.id.Item47, R.id.Item48},
                {R.id.Item50, R.id.Item51, R.id.Item52, R.id.Item53, R.id.Item54, R.id.Item55, R.id.Item56, R.id.Item57, R.id.Item58},
                {R.id.Item60, R.id.Item61, R.id.Item62, R.id.Item63, R.id.Item64, R.id.Item65, R.id.Item66, R.id.Item67, R.id.Item68},
                {R.id.Item70, R.id.Item71, R.id.Item72, R.id.Item73, R.id.Item74, R.id.Item75, R.id.Item76, R.id.Item77, R.id.Item78},
                {R.id.Item80, R.id.Item81, R.id.Item82, R.id.Item83, R.id.Item84, R.id.Item85, R.id.Item86, R.id.Item87, R.id.Item88},
                {R.id.Item90, R.id.Item91, R.id.Item92, R.id.Item93, R.id.Item94, R.id.Item95, R.id.Item96, R.id.Item97, R.id.Item98},
                {R.id.Item100, R.id.Item101, R.id.Item102, R.id.Item103, R.id.Item104, R.id.Item105, R.id.Item106, R.id.Item107, R.id.Item108},
                {R.id.Item110, R.id.Item111, R.id.Item112, R.id.Item113, R.id.Item114, R.id.Item115, R.id.Item116, R.id.Item117, R.id.Item118},
                {R.id.Item120, R.id.Item121, R.id.Item122, R.id.Item123, R.id.Item124, R.id.Item125, R.id.Item126, R.id.Item127, R.id.Item128},
                {R.id.Item130, R.id.Item131, R.id.Item132, R.id.Item133, R.id.Item134, R.id.Item135, R.id.Item136, R.id.Item137, R.id.Item138},
                {R.id.Item140, R.id.Item141, R.id.Item142, R.id.Item143, R.id.Item144, R.id.Item145, R.id.Item146, R.id.Item147, R.id.Item148},
                {R.id.Item150, R.id.Item151, R.id.Item152, R.id.Item153, R.id.Item154, R.id.Item155, R.id.Item156, R.id.Item157, R.id.Item158},
                {R.id.Item160, R.id.Item161, R.id.Item162, R.id.Item163, R.id.Item164, R.id.Item165, R.id.Item166, R.id.Item167, R.id.Item168},
                {R.id.Item170, R.id.Item171, R.id.Item172, R.id.Item173, R.id.Item174, R.id.Item175, R.id.Item176, R.id.Item177, R.id.Item178},
                {R.id.Item180, R.id.Item181, R.id.Item182, R.id.Item183, R.id.Item184, R.id.Item185, R.id.Item186, R.id.Item187, R.id.Item188},
                {R.id.Item190, R.id.Item191, R.id.Item192, R.id.Item193, R.id.Item194, R.id.Item195, R.id.Item196, R.id.Item197, R.id.Item198},
                {R.id.Item200, R.id.Item201, R.id.Item202, R.id.Item203, R.id.Item204, R.id.Item205, R.id.Item206, R.id.Item207, R.id.Item208},
                {R.id.Item210, R.id.Item211, R.id.Item212, R.id.Item213, R.id.Item214, R.id.Item215, R.id.Item216, R.id.Item217, R.id.Item218},
                {R.id.Item220, R.id.Item221, R.id.Item222, R.id.Item223, R.id.Item224, R.id.Item225, R.id.Item226, R.id.Item227, R.id.Item228},
                {R.id.Item230, R.id.Item231, R.id.Item232, R.id.Item233, R.id.Item234, R.id.Item235, R.id.Item236, R.id.Item237, R.id.Item238},
                {R.id.Item240, R.id.Item241, R.id.Item242, R.id.Item243, R.id.Item244, R.id.Item245, R.id.Item246, R.id.Item247, R.id.Item248},
                {R.id.Item250, R.id.Item251, R.id.Item252, R.id.Item253, R.id.Item254, R.id.Item255, R.id.Item256, R.id.Item257, R.id.Item258}
        };
    }

    @Override
    protected void setupDefaultTable() {
        // Handle Intent Data here, as this is called in onCreate
        countryStringRaw = getIntent().getStringExtra("coinCountry");
        coinCountry = findCoinCountryItem(countryStringRaw);

        // UI Setup
        ((TextView) findViewById(buttonIDs[0][0])).setText(coinCountry.toString());

        setupNavigationButtons();

        // Init Table Array (9 columns)
        table = new TableItem[buttonIDs.length][9];
        // Header
        table[0] = new TableItem[]{coinCountry, ONE, TWO, FIVE, TEN, TWENTY, FIFTY, I, II};
        // Body (Default)
        for(int i = 1; i < buttonIDs.length; i++) {
            table[i] = new TableItem[]{X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED};
        }
    }

    private void setupNavigationButtons() {
        findViewById(R.id.openAddingInternYear).setOnClickListener(v -> {
            Intent intent = new Intent(this, InternAddingActivity.class);
            intent.putExtra("coinCountry", countryStringRaw);
            startActivity(intent);
        });

        findViewById(R.id.openSonderIIIntern).setOnClickListener(v -> {
            Intent intent = new Intent(this, InternCoinTableActivityIISpecial.class);
            intent.putExtra("coinCountry", countryStringRaw);
            startActivity(intent);
        });
    }

    @Override
    protected void onAdminCheckFinished(boolean isAdmin) {
        if (!isAdmin) {
            findViewById(R.id.openAddingInternYear).setVisibility(View.GONE);
        }
    }

    @Override
    protected void loadDataFromRepository() {
        repository.getInternCoins(countryStringRaw, new FirestoreDataCallback<List<Coin>>() {
            @Override
            public void onSuccess(List<Coin> coins) {
                processData(coins);
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(InternCoinTableActivity.this, "Fehler beim Laden", Toast.LENGTH_SHORT).show();
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

        tableYears = new String[coinYears.size()];
        int pointer = 0;

        for (int year : coinYears) {
            if (pointer + 1 >= table.length) break;

            tableYears[pointer] = year < 10 ? "0" + year : String.valueOf(year);

            for (Coin coin : coinMap.get(year)) {
                int colIndex = getColumnForValue(coin.getValue());
                if (colIndex != -1) {
                    table[pointer + 1][colIndex] = MISSING;
                }
            }
            pointer++;
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
        int pointerColumn = 1;

        for (int row : rowWithMissing) {
            String yearText = (row - 1 < tableYears.length) ? tableYears[row - 1] : "";
            ((TextView) findViewById(buttonIDs[pointerRow][0])).setText(yearText);

            for (int column : columnWithMissing) {
                if (pointerRow == 1) {
                    ((TextView) findViewById(buttonIDs[0][pointerColumn])).setText(findValueString(table[0][column]));
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
            pointerColumn = 1;
        }

        hideUnusedButtons(rowWithMissing, columnWithMissing, 1, 1);
    }

    @Override
    protected Coin createCoinFromSelection(int row, int col) {
        String yearString = "";
        try {
            yearString = ((TextView) findViewById(buttonIDs[row][0])).getText().toString();
        } catch (Exception e) { return null; }

        if (yearString.isEmpty()) return null;
        int year = Integer.parseInt(yearString);

        return Coin.createInternStandard(year, coinCountry, table[0][col]);
    }

    @Override
    protected void addToRepo(Coin coin, FirestoreCallback callback) {
        repository.addInternCoin(coin, callback);
    }

    @Override
    protected void deleteFromRepo(Coin coin, FirestoreCallback callback) {
        repository.deleteInternCoin(coin, callback);
    }

    @Override
    protected void setupAdminClickListeners() {
        for (int i = 1; i < table.length; i++) {
            for (int j = 1; j < table[1].length; j++) {
                findViewById(buttonIDs[i][j]).setOnClickListener(this::click);
            }
        }
    }

    @Override
    protected void goBack() {
        Intent intent = new Intent(this, StartingPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private int getColumnForValue(TableItem value) {
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