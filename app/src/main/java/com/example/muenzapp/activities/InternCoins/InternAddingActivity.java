package com.example.muenzapp.activities.InternCoins;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.muenzapp.R;
import com.example.muenzapp.TableItem;
import com.example.muenzapp.data.model.Coin;
import com.example.muenzapp.data.repository.CoinRepository;
import com.example.muenzapp.utils.FirestoreCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static android.graphics.Color.TRANSPARENT;
import static com.example.muenzapp.R.*;
import static com.example.muenzapp.StaticHelper.*;

public class InternAddingActivity extends AppCompatActivity {
    private final int[] buttonIDs = {id.addButtonONE, id.addButtonTWO, id.addButtonFIVE, id.addButtonTEN, id.addButtonTWENTY, id.addButtonFIFTY, id.addButtonI, id.addButtonII};
    private EditText coinYear;
    private List<TableItem> selectedValues;
    private String countryString;
    private CoinRepository repository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.intern_adding_layout);

        // Load Repository
        repository = CoinRepository.getInstance();
        selectedValues = new ArrayList<>();

        // Handle Intent Data
        countryString = getIntent().getStringExtra("coinCountry");
        ((TextView)findViewById(id.Country)).setText(findCoinCountryStringFull(countryString));

        // find UI Elements
        coinYear = findViewById(id.addYearEditText);
        Button addToDatabase = findViewById(id.addToDatabase);

        setupClickListeners();
        addToDatabase.setOnClickListener(v -> saveCoins());
    }

    private void setupClickListeners() {
        findViewById(R.id.closeCoinYearAdding).setOnClickListener((v) -> {
            Intent intent = new Intent(this, InternCoinTableActivity.class);
            intent.putExtra("coinCountry", countryString);
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
            coinYear.setError("Bitte gültiges Jahr eingeben");
            return;
        }

        if (selectedValues.isEmpty() || selectedCoinYear < 0) {
            Toast.makeText(this, "(Jahr, Buchstabe, Euro/Cent) notwendig!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Zähler für asynchrone Operationen
        int totalOps = selectedValues.size();
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

        // Schleife über alle ausgewählten Werte
        for (TableItem value : selectedValues) {
            repository.addInternCoin(Coin.createInternStandard(selectedCoinYear, findCoinCountryItem(countryString), value), callback);
        }
    }
    private void checkIfFinished(int total, int current, int errors) {
        if (current >= total) {
            runOnUiThread(() -> {
                if (errors > 0) {
                    Toast.makeText(InternAddingActivity.this, "Fertig, aber mit Fehlern.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InternAddingActivity.this, "Erfolgreich hinzugefügt!", Toast.LENGTH_SHORT).show();
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
        selectedValues.clear();
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public void doOnClick(View view) {
        TableItem item = getValueFromId(view.getId());
        if (!selectedValues.contains(item)) {
            selectedValues.add(item);
            view.setActivated(true);
            view.setBackground(getDrawable(drawable.red_border));
            // Hintergrund ändern
        } else {
            //Hintergrund ändern
            selectedValues.remove(item);
            view.setActivated(false);
            view.setBackgroundColor(TRANSPARENT);
        }
    }
    private TableItem getValueFromId(int id) {
        if (id == R.id.addButtonONE) return TableItem.ONE;
        if (id == R.id.addButtonTWO) return TableItem.TWO;
        if (id == R.id.addButtonFIVE) return TableItem.FIVE;
        if (id == R.id.addButtonTEN) return TableItem.TEN;
        if (id == R.id.addButtonTWENTY) return TableItem.TWENTY;
        if (id == R.id.addButtonFIFTY) return TableItem.FIFTY;
        if (id == R.id.addButtonI) return TableItem.I;
        if (id == R.id.addButtonII) return TableItem.II;
        return TableItem.X;
    }
}