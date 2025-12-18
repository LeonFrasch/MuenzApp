package com.example.muenzapp.activities.InternCoins;

import static com.example.muenzapp.R.drawable.table_border;
import static com.example.muenzapp.R.drawable.table_border_active;
import static com.example.muenzapp.R.drawable.table_border_active_red;
import static com.example.muenzapp.R.drawable.table_border_red;
import static com.example.muenzapp.StaticHelper.findCoinCountryItem;
import static com.example.muenzapp.TableItem.*;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muenzapp.data.model.Coin;
import com.example.muenzapp.R;
import com.example.muenzapp.TableItem;
import com.example.muenzapp.data.repository.AuthRepository;
import com.example.muenzapp.data.repository.CoinRepository;
import com.example.muenzapp.utils.FirestoreCallback;
import com.example.muenzapp.utils.FirestoreDataCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class InternCoinTableActivityIISpecial extends AppCompatActivity {
    private TableItem[][] table; // default Aussehen der Tabelle
    private String[] tableYears;
    private List<Coin> collect;
    private List<Coin> missing;
    private TableItem coinCountry;
    private String countryStringRaw;
    private CoinRepository repository;
    private boolean isAdmin;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intern_coin_table_layout_ii_special);

        // Initialize
        repository = CoinRepository.getInstance();
        AuthRepository authRepository = AuthRepository.getInstance();
        missing = new ArrayList<>();
        collect = new ArrayList<>();

        // Handle Intent Data
        countryStringRaw = getIntent().getStringExtra("coinCountry");
        coinCountry = findCoinCountryItem(countryStringRaw);

        // UI Setup
        ((TextView) findViewById(buttonIDs[0][0])).setText(coinCountry.toString());
        setupDefaultTable();
        setupNavigationButtons();

        // Admin check
        if (authRepository.getCurrentUser() != null) {
            repository.checkIsAdmin(authRepository.getCurrentUser().getUid(), new FirestoreDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    isAdmin = result;
                    if (!isAdmin) {
                        findViewById(R.id.openAddingInternYearSpecialII).setVisibility(View.GONE);
                    } else {
                        setupAdminClickListeners();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    findViewById(R.id.openAddingInternYearSpecialII).setVisibility(View.GONE);
                }
            });
        } else {
            findViewById(R.id.openAddingInternYearSpecialII).setVisibility(View.GONE);
        }

        // Load data
        loadDataFromRepository();
    }
    private void setupNavigationButtons() {
        findViewById(R.id.openAddingInternYearSpecialII).setOnClickListener((v) -> {
            Intent intent = new Intent(this, InternAddingActivityIISpecial.class);
            intent.putExtra("coinCountry", countryStringRaw);
            startActivity(intent);
        });

        findViewById(R.id.closeCoinTable).setOnClickListener((v) -> saveChangesAndExit());
    }
    private void loadDataFromRepository() {
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
        // Sammeln und Sortieren
        List<Integer> coinYears = new ArrayList<>();
        Map<Integer, List<Coin>> coinMap = new HashMap<>();

        for (Coin coin : coins) {
            int year = coin.getYear();
            if (!coinYears.contains(year)) {
                coinYears.add(year);
            }
            coinMap.putIfAbsent(year, new ArrayList<>());
            coinMap.get(year).add(coin);
        }
        Collections.sort(coinYears);

        // In Tabelle füllen
        tableYears = new String[coinYears.size()];
        int pointer = 0;

        for (int year : coinYears) {
            if (pointer + 1 >= table.length) break;

            tableYears[pointer] = year < 10 ? "0" + year : String.valueOf(year);

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
            // Jahr setzen
            String yearText = (row - 1 < tableYears.length) ? tableYears[row - 1] : "";
            ((TextView) findViewById(buttonIDs[pointerRow][0])).setText(yearText);

            for (int column : columnWithMissing) {
                // Header (CC1, CC2...) setzen
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
            pointerColumn = 1;
        }

        hideUnusedButtons(rowWithMissing, columnWithMissing);
    }

    private void hideUnusedButtons(Set<Integer> activeRows, Set<Integer> activeCols) {
        int lastRow = table.length - 1;
        int lastColumn = table[1].length - 1;

        for (int row = 1; row < table.length; row++) {
            if (!activeRows.contains(row)) {
                for (int k = 0; k < table[1].length; k++) {
                    findViewById(buttonIDs[lastRow][k]).setVisibility(View.GONE);
                }
                lastRow--;
            }
        }
        for (int column = 1; column < table[1].length; column++) {
            if (!activeCols.contains(column)) {
                for (int k = 0; k < table.length; k++) {
                    findViewById(buttonIDs[k][lastColumn]).setVisibility(View.GONE);
                }
                lastColumn--;
            }
        }
    }

    private void saveChangesAndExit() {
        int totalOps = missing.size() + collect.size();
        if (totalOps == 0) {
            goBack();
            return;
        }

        AtomicInteger completedCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        FirestoreCallback callback = new FirestoreCallback() {
            @Override
            public void onSuccess() {
                checkIfFinished(totalOps, completedCount.incrementAndGet(), errorCount.get());
            }

            @Override
            public void onFailure(Exception e) {
                errorCount.incrementAndGet();
                checkIfFinished(totalOps, completedCount.incrementAndGet(), errorCount.get());
            }
        };

        for (Coin coin : missing) {
            repository.addInternSpecialCoin(coin, callback);
        }
        for (Coin coin : collect) {
            repository.deleteInternSpecialCoin(coin, callback);
        }
    }

    private void checkIfFinished(int total, int current, int errors) {
        if (current >= total) {
            runOnUiThread(() -> {
                if (errors > 0) {
                    Toast.makeText(this, "Fertig, aber mit Fehlern.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Änderungen gespeichert!", Toast.LENGTH_SHORT).show();
                }
                goBack();
            });
        }
    }

    private void goBack() {
        Intent intent = new Intent(this, InternCoinTableActivity.class);
        intent.putExtra("coinCountry", countryStringRaw);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void click(View view) {
        int id = view.getId();
        int row = -1, column = -1;
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
        }

        if (!found) return;

        String yearString = "";
        try {
            yearString = ((TextView) findViewById(buttonIDs[row][0])).getText().toString();
        } catch (Exception e) { return; }

        if (yearString.isEmpty()) return;
        int year = Integer.parseInt(yearString);

        Coin coin = Coin.createInternSpecial(year, coinCountry, table[0][column]);

        boolean isCollectedInTable = (COLLECTED == table[row][column]);

        if (isCollectedInTable) {
            if (!missing.contains(coin)) {
                missing.add(coin);
                view.setBackground(getDrawable(table_border_red));
            } else {
                missing.remove(coin);
                view.setBackground(getDrawable(table_border_active));
            }
        } else {
            if (!collect.contains(coin)) {
                collect.add(coin);
                view.setBackground(getDrawable(table_border_active_red));
            } else {
                collect.remove(coin);
                view.setBackground(getDrawable(table_border));
            }
        }

        if (!missing.isEmpty() || !collect.isEmpty()) {
            findViewById(R.id.closeCoinTable).setBackground(getDrawable(table_border_red));
        } else {
            findViewById(R.id.closeCoinTable).setBackground(null);
        }
    }

    private void setupDefaultTable() {
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
    }

    private void setupAdminClickListeners() {
        for (int i = 1; i < table.length; i++) {
            for (int j = 1; j < table[1].length; j++) {
                findViewById(buttonIDs[i][j]).setOnClickListener(this::click);
            }
        }
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