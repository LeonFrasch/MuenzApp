package com.example.muenzapp.activities.GermanCoins;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.muenzapp.R;
import com.example.muenzapp.TableItem;
import com.example.muenzapp.data.model.CoinEntity;
import com.example.muenzapp.data.repository.AuthRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.muenzapp.R.drawable.table_border;
import static com.example.muenzapp.R.drawable.table_border_active;
import static com.example.muenzapp.R.drawable.table_border_active_red;
import static com.example.muenzapp.R.drawable.table_border_red;
import static com.example.muenzapp.StaticHelper.findValueString;
import static com.example.muenzapp.TableItem.*;
import com.example.muenzapp.data.repository.CoinRepository;
import com.example.muenzapp.utils.FirestoreCallback;
import com.example.muenzapp.utils.FirestoreDataCallback;

public class GermanCoinTableActivity extends AppCompatActivity {
    private TableItem[][] table; // default Aussehen der Tabelle
    private final int[][] buttonIDs = {{R.id.Item00, R.id.Item01, R.id.Item02, R.id.Item03, R.id.Item04, R.id.Item05, R.id.Item06, R.id.Item07, R.id.Item08}, {R.id.Item10, R.id.Item11, R.id.Item12, R.id.Item13, R.id.Item14, R.id.Item15, R.id.Item16, R.id.Item17, R.id.Item18}, {R.id.Item20, R.id.Item21, R.id.Item22, R.id.Item23, R.id.Item24, R.id.Item25, R.id.Item26, R.id.Item27, R.id.Item28}, {R.id.Item30, R.id.Item31, R.id.Item32, R.id.Item33, R.id.Item34, R.id.Item35, R.id.Item36, R.id.Item37, R.id.Item38}, {R.id.Item40, R.id.Item41, R.id.Item42, R.id.Item43, R.id.Item44, R.id.Item45, R.id.Item46, R.id.Item47, R.id.Item48}, {R.id.Item50, R.id.Item51, R.id.Item52, R.id.Item53, R.id.Item54, R.id.Item55, R.id.Item56, R.id.Item57, R.id.Item58}}; //Alle Items der Tabelle: alle mit 0 sind TextViews, sonst Buttons
    private int coinYear;
    private List<CoinEntity> missing;
    private List<CoinEntity> collect;
    private CoinRepository repository;
    private boolean isAdmin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.german_coin_table_layout);

        // Initialize
        repository = CoinRepository.getInstance();
        AuthRepository authRepository = AuthRepository.getInstance();

        missing = new ArrayList<>();
        collect = new ArrayList<>();

        // Admin Check
        if (authRepository.getCurrentUser() != null) {
            String uid = authRepository.getCurrentUser().getUid();
            repository.checkIsAdmin(uid, new FirestoreDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    isAdmin = result;
                    if (isAdmin) {
                        setupAdminClickListeners();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("AdminCheck", "Fehler beim Prüfen der Rechte", e);
                }
            });
        }

        // UI Listener
        findViewById(R.id.closeCoinTable).setOnClickListener((v) -> saveChangesAndExit());

        coinYear = getIntent().getIntExtra("Year", -100);
        ((TextView) findViewById(buttonIDs[0][0])).setText(coinYear < 10 ? "0" + coinYear : String.valueOf(coinYear));

        setupDefaultTable();

        // Load Data
        loadDataFromRepository();
    }

    private void loadDataFromRepository() {
        repository.getGermanCoinsByYear(coinYear, new FirestoreDataCallback<List<CoinEntity>>() {
            @Override
            public void onSuccess(List<CoinEntity> coins) {
                // UI Thread wird durch das Callback im Repo meist schon beachtet,
                // aber zur Sicherheit bei UI Änderungen:
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
    private void updateTableWithData(List<CoinEntity> coins) {
        for (CoinEntity coin : coins) {
            TableItem letter = coin.getCoinLetter();
            TableItem value = coin.getCoinValue();

            int letterIdx = getLetterindex(letter);
            int valueIdx = getValueIndex(value);

            if (letterIdx != -1 && valueIdx != -1) {
                table[letterIdx][valueIdx] = MISSING;
                findViewById(buttonIDs[letterIdx][valueIdx]).setBackground(getDrawable(table_border));
            }
        }

        // Tabellenlogik (Zeilen/Spalten ausblenden)
        optimizeTableLayout();
    }

    private void saveChangesAndExit() {
        int totalOps = missing.size() + collect.size();
        if (totalOps == 0) {
            goBack();
            return;
        }

        // AtomicInteger ist thread-sicher
        AtomicInteger completedCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        FirestoreCallback progressCallback = new FirestoreCallback() {
            @Override
            public void onSuccess() {
                checkIfFinished(totalOps, completedCount.incrementAndGet(), errorCount.get());
            }

            @Override
            public void onFailure(Exception e) {
                errorCount.incrementAndGet();
                Log.e("SAVE", "Fehler beim Speichern", e);
                // Trotzdem weiterzählen, damit die App nicht hängen bleibt
                checkIfFinished(totalOps, completedCount.incrementAndGet(), errorCount.get());
            }
        };

        for (CoinEntity entity : missing) {
            repository.addGermanCoin(entity.getCoinYear(), entity.getCoinValue(), entity.getCoinLetter(), progressCallback);
        }

        for (CoinEntity entity : collect) {
            repository.deleteGermanCoin(entity.getCoinYear(), entity.getCoinValue(), entity.getCoinLetter(), progressCallback);
        }
    }

    private void checkIfFinished(int total, int current, int errors) {
        if (current >= total) {
            runOnUiThread(() -> {
                if (errors > 0) {
                    Toast.makeText(GermanCoinTableActivity.this,
                            "Fertig, aber " + errors + " Fehler aufgetreten.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(GermanCoinTableActivity.this,
                            "Alle Änderungen erfolgreich gespeichert!", Toast.LENGTH_SHORT).show();
                }
                goBack();
            });
        }
    }

    private void goBack() {
        Intent intent = new Intent(this, GermanOverviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // --- Helper Methoden für UI Logik (um onCreate sauber zu halten) ---

    private void setupAdminClickListeners() {
        for (int i = 1; i < table.length; i++) {
            for (int j = 1; j < table[1].length; j++) {
                findViewById(buttonIDs[i][j]).setOnClickListener(this::click);
            }
        }
    }

    private void setupDefaultTable() {
        table = new TableItem[][]{{X, ONE, TWO, FIVE, TEN, TWENTY, FIFTY, I, II},
                {A, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {D, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {F, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {G, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {J, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED}};
    }

    // Einfache Helper um Switch-Cases zu ersetzen
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void optimizeTableLayout() {
        // TreeSet verwenden, damit die Reihenfolge (1, 2, 3...) erhalten bleibt!
        // Sonst sind Buchstaben und Werte in der Tabelle vertauscht.
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
            table[pointerRow][0] = table[row][0]; // Header verschieben

            int pointerColumn = 1;
            for (int column : columnWithMissing) {
                if (pointerRow == 1) {
                    ((TextView)findViewById(buttonIDs[0][pointerColumn])).setText(findValueString(table[0][column]));
                    table[0][pointerColumn] = table[0][column]; // Header verschieben
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

    @SuppressLint("UseCompatLoadingForDrawables")
    public void click(View view) {
        int id = view.getId();
        boolean found = false;

        int row = -1;
        int column = -1;
        // Position suchen
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

        boolean isCollectedInTable = (COLLECTED == table[row][column]);

        // Entity erstellen für Vergleich
        CoinEntity coinEntity = new CoinEntity();
        coinEntity.setCoinYear(coinYear);
        coinEntity.setCoinValue(table[0][column]);
        coinEntity.setCoinLetter(table[row][0]);

        if (isCollectedInTable) {
            // WAR: Collected (Grün) -> WIRD: Missing (Rot)
            // Logik: Zu "missing" Liste hinzufügen (damit es in DB gespeichert wird)
            if (!missing.contains(coinEntity)) {
                missing.add(coinEntity);
                view.setBackground(getDrawable(table_border_red));
            } else {
                missing.remove(coinEntity);
                view.setBackground(getDrawable(table_border_active));
            }
        } else {
            // WAR: Missing (Rot) -> WIRD: Collected (Grün)
            // Logik: Zu "collect" Liste hinzufügen (damit es aus DB gelöscht wird)
            if (!collect.contains(coinEntity)) {
                collect.add(coinEntity);
                view.setBackground(getDrawable(table_border_active_red));
            } else {
                collect.remove(coinEntity);
                view.setBackground(getDrawable(table_border));
            }
        }

        // Button Update
        runOnUiThread(() -> {
            if (!missing.isEmpty() || !collect.isEmpty()) {
                findViewById(R.id.closeCoinTable).setBackground(getDrawable(table_border_red));
            } else {
                findViewById(R.id.closeCoinTable).setBackground(null);
            }
        });
    }
}
