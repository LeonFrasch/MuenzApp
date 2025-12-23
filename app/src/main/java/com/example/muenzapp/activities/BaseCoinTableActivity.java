package com.example.muenzapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.muenzapp.R;
import com.example.muenzapp.TableItem;
import com.example.muenzapp.activities.GermanCoins.GermanOverviewActivity;
import com.example.muenzapp.data.model.Coin;
import com.example.muenzapp.data.repository.AuthRepository;
import com.example.muenzapp.data.repository.CoinRepository;
import com.example.muenzapp.utils.FirestoreCallback;
import com.example.muenzapp.utils.FirestoreDataCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.muenzapp.R.drawable.*;
import static com.example.muenzapp.TableItem.COLLECTED;

public abstract class BaseCoinTableActivity extends AppCompatActivity {

    protected TableItem[][] table;
    protected int[][] buttonIDs;
    protected List<Coin> missing;
    protected List<Coin> collect;
    protected CoinRepository repository;
    protected boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        repository = CoinRepository.getInstance();
        missing = new ArrayList<>();
        collect = new ArrayList<>();

        findViewById(R.id.closeCoinTable).setOnClickListener((v) -> saveChangesAndExit());

        performAdminCheck();
        setupButtonIDs();
        setupDefaultTable();
        loadDataFromRepository();
    }

    // --- Abstract Methods ---
    protected abstract int getLayoutId();
    protected abstract void setupButtonIDs();
    protected abstract void setupDefaultTable();
    protected abstract void loadDataFromRepository();
    protected abstract Coin createCoinFromSelection(int row, int col);
    protected abstract void addToRepo(Coin coin, FirestoreCallback callback);
    protected abstract void deleteFromRepo(Coin coin, FirestoreCallback callback);
    protected abstract void setupAdminClickListeners();

    // --- Shared Admin Check ---
    protected void performAdminCheck() {
        AuthRepository authRepository = AuthRepository.getInstance();
        if (authRepository.getCurrentUser() != null) {
            String uid = authRepository.getCurrentUser().getUid();
            repository.checkIsAdmin(uid, new FirestoreDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    isAdmin = result;
                    if (isAdmin) setupAdminClickListeners();
                    onAdminCheckFinished(result);
                }
                @Override
                public void onFailure(Exception e) {
                    Log.e("AdminCheck", "Fehler beim Prüfen der Rechte", e);
                }
            });
        } else {
            onAdminCheckFinished(false);
        }
    }

    protected void onAdminCheckFinished(boolean isAdmin) {
        // Hook for subclasses
    }

    // --- Shared Click Logic ---
    @SuppressLint("UseCompatLoadingForDrawables")
    public void click(View view) {
        int id = view.getId();
        int row = -1, col = -1;
        boolean found = false;

        // Find position
        for (int i = 0; i < buttonIDs.length; i++) {
            for (int j = 0; j < buttonIDs[i].length; j++) {
                if (buttonIDs[i][j] == id) {
                    row = i;
                    col = j;
                    found = true;
                    break;
                }
            }
            if (found) break;
        }

        if (!found || row == -1) return;

        Coin coin = createCoinFromSelection(row, col);
        if (coin == null) return;

        boolean isCollectedInTable = (COLLECTED == table[row][col]);

        if (isCollectedInTable) {
            // Logic: Collected (Green) -> Missing (Red)
            if (!missing.contains(coin)) {
                missing.add(coin);
                view.setBackground(getDrawable(table_border_red));
            } else {
                missing.remove(coin);
                view.setBackground(getDrawable(table_border_active));
            }
        } else {
            // Logic: Missing (Red) -> Collected (Green)
            if (!collect.contains(coin)) {
                collect.add(coin);
                view.setBackground(getDrawable(table_border_active_red));
            } else {
                collect.remove(coin);
                view.setBackground(getDrawable(table_border));
            }
        }
        updateCloseButton();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    protected void updateCloseButton() {
        runOnUiThread(() -> {
            if (!missing.isEmpty() || !collect.isEmpty()) {
                findViewById(R.id.closeCoinTable).setBackground(getDrawable(table_border_red));
            } else {
                findViewById(R.id.closeCoinTable).setBackground(null);
            }
        });
    }

    // --- Shared Save Logic ---
    protected void saveChangesAndExit() {
        int totalOps = missing.size() + collect.size();
        if (totalOps == 0) {
            goBack();
            return;
        }

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
                checkIfFinished(totalOps, completedCount.incrementAndGet(), errorCount.get());
            }
        };

        for (Coin coin : missing) addToRepo(coin, progressCallback);
        for (Coin coin : collect) deleteFromRepo(coin, progressCallback);
    }

    private void checkIfFinished(int total, int current, int errors) {
        if (current >= total) {
            runOnUiThread(() -> {
                String msg = (errors > 0) ? "Fertig, aber " + errors + " Fehler." : "Erfolgreich gespeichert!";
                Toast.makeText(this, msg, errors > 0 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
                goBack();
            });
        }
    }

    protected void goBack() {
        Intent intent = new Intent(this, GermanOverviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // --- UI Helper ---
    protected void hideUnusedButtons(Set<Integer> activeRows, Set<Integer> activeCols, int startRowCheck, int startColCheck) {
        int lastRow = buttonIDs.length - 1;
        int lastColumn = buttonIDs[0].length - 1;

        for (int row = startRowCheck; row < buttonIDs.length; row++) {
            if (!activeRows.contains(row)) {
                for (int k = 0; k < buttonIDs[0].length; k++) {
                    View v = findViewById(buttonIDs[lastRow][k]);
                    if(v != null) v.setVisibility(View.GONE);
                }
                lastRow--;
            }
        }
        for (int column = startColCheck; column < buttonIDs[0].length; column++) {
            if (!activeCols.contains(column)) {
                for (int k = 0; k < buttonIDs.length; k++) {
                    View v = findViewById(buttonIDs[k][lastColumn]);
                    if(v != null) v.setVisibility(View.GONE);
                }
                lastColumn--;
            }
        }
    }
}