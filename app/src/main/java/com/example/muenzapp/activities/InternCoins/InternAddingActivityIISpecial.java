package com.example.muenzapp.activities.InternCoins;

import static android.graphics.Color.TRANSPARENT;

import static com.example.muenzapp.StaticHelper.findCoinCountryItem;
import static com.example.muenzapp.StaticHelper.findCoinCountryStringFull;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.muenzapp.R;
import com.example.muenzapp.TableItem;
import com.example.muenzapp.data.model.Coin;
import com.example.muenzapp.data.repository.CoinRepository;
import com.example.muenzapp.utils.FirestoreCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class InternAddingActivityIISpecial extends AppCompatActivity {

    private final int[] buttonIDs = {R.id.addButtonCC1, R.id.addButtonCC2, R.id.addButtonCC3};
    private final int[] unneededIDs = {R.id.letterText, R.id.addButtonA, R.id.addButtonD, R.id.addButtonF, R.id.addButtonG, R.id.addButtonJ};
    private EditText coinYear;
    private List<TableItem> selectedTypes;
    private String countryStringRaw;
    private CoinRepository repository;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adding_layout_special_ii);

        repository = CoinRepository.getInstance();
        selectedTypes = new ArrayList<>();

        // Handle Intent Data
        countryStringRaw = getIntent().getStringExtra("coinCountry");

        // UI Setup
        ((TextView)findViewById(R.id.country)).setText(findCoinCountryStringFull(countryStringRaw));

        coinYear = findViewById(R.id.addYearEditText);
        Button addToDatabase = findViewById(R.id.addToDatabase);

        setupViews();
        setupClickListeners();

        addToDatabase.setOnClickListener(v -> saveCoins());
    }
    private void setupViews() {
        // Nicht benötigte Buttons (Buchstaben) ausblenden
        for (int id : unneededIDs) {
            findViewById(id).setVisibility(View.GONE);
        }
    }
    private void setupClickListeners() {
        findViewById(R.id.closeCoinYearAdding).setOnClickListener((v) -> {
            Intent intent = new Intent(this, InternCoinTableActivityIISpecial.class);
            intent.putExtra("coinCountry", countryStringRaw);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        for (int id : buttonIDs) {
            findViewById(id).setOnClickListener(this::doOnClick);
        }
    }
    private void saveCoins() {
        String yearString = coinYear.getText().toString();
        int selectedCoinYear;
        try {
            selectedCoinYear = Integer.parseInt(yearString);
        } catch (NumberFormatException e) {
            coinYear.setError("Bitte Jahr eingeben");
            return;
        }

        if (selectedTypes.isEmpty() || selectedCoinYear < 0) {
            Toast.makeText(this, "(Jahr, Typ) notwendig!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Zähler für Async Calls
        int totalOps = selectedTypes.size();
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

        for (TableItem selectedType : selectedTypes) {
            repository.addInternSpecialCoin(Coin.createInternSpecial(selectedCoinYear, findCoinCountryItem(countryStringRaw), selectedType), callback);
        }
    }
    private void checkIfFinished(int total, int current, int errors) {
        if (current >= total) {
            runOnUiThread(() -> {
                if (errors > 0) {
                    Toast.makeText(this, "Fertig, aber mit Fehlern.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Erfolgreich hinzugefügt!", Toast.LENGTH_SHORT).show();
                    resetUI();
                }
            });
        }
    }

    private void resetUI() {
        for (int id : buttonIDs) {
            View btn = findViewById(id);
            btn.setBackgroundColor(TRANSPARENT);
            btn.setActivated(false);
        }
        coinYear.setText("");
        selectedTypes.clear();
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public void doOnClick(View view) {
        TableItem item = getTypesFromId(view.getId());
        if (!selectedTypes.contains(item)) {
            selectedTypes.add(item);
            view.setActivated(true);
            view.setBackground(getDrawable(R.drawable.red_border));
            // Hintergrund ändern
        } else {
            //Hintergrund ändern
            selectedTypes.remove(item);
            view.setActivated(false);
            view.setBackgroundColor(TRANSPARENT);
        }
    }
    private TableItem getTypesFromId(int id) {
        if (id == R.id.addButtonCC1) return TableItem.CC1;
        if (id == R.id.addButtonCC2) return TableItem.CC2;
        if (id == R.id.addButtonCC3) return TableItem.CC3;
        return TableItem.X;
    }
}