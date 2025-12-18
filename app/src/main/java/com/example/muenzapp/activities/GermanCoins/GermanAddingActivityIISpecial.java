package com.example.muenzapp.activities.GermanCoins;

import static android.content.ContentValues.TAG;
import static android.graphics.Color.TRANSPARENT;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muenzapp.R;
import com.example.muenzapp.TableItem;
import com.example.muenzapp.data.repository.CoinRepository;
import com.example.muenzapp.utils.FirestoreCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class GermanAddingActivityIISpecial extends AppCompatActivity {

    final int[] buttonIDs = {R.id.addButtonA, R.id.addButtonD, R.id.addButtonF, R.id.addButtonG, R.id.addButtonJ, R.id.addButtonCC1, R.id.addButtonCC2, R.id.addButtonCC3};
    int selectedCoinYear;
    EditText coinYear;
    Button addToDatabase;
    List<TableItem> selectedLetters;
    List<TableItem> selectedTypes;
    private CoinRepository repository;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adding_layout_special_ii);
        findViewById(R.id.closeCoinYearAdding).setOnClickListener((v) -> {
            Intent intent = new Intent(this, GermanCoinTableActivityIISpecial.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.country).setVisibility(View.GONE);

        repository = CoinRepository.getInstance();

        selectedLetters = new ArrayList<>();
        selectedTypes = new ArrayList<>();
        selectedCoinYear = Integer.MIN_VALUE;
        for (int id : buttonIDs) {
            findViewById(id).setOnClickListener(this::doOnClick);
        }
        coinYear = findViewById(R.id.addYearEditText);
        addToDatabase = findViewById(R.id.addToDatabase);

        addToDatabase.setOnClickListener(v -> {
            String yearString = coinYear.getText().toString();
            try {
                selectedCoinYear = Integer.parseInt(yearString);
            } catch (NumberFormatException e) {
                // falsches Format // TODO
            }
            Executors.newSingleThreadExecutor().execute(() -> {
                if (selectedLetters.size() > 0 && selectedTypes.size() > 0 && selectedCoinYear >= 0) {

                    final int totalOperations = selectedLetters.size() * selectedTypes.size();
                    final int[] completedOperations = {0};

                    for (TableItem letter : selectedLetters) {
                        for (TableItem type : selectedTypes) {

                            repository.addSpecialCoin(selectedCoinYear, type, letter, new FirestoreCallback() {
                                @Override
                                public void onSuccess() {
                                    completedOperations[0]++;
                                    Log.d(TAG, "Stored Coin: " + type + " " + letter);

                                    // When all coins are stored: UI Feedback
                                    if (completedOperations[0] == totalOperations) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(GermanAddingActivityIISpecial.this, "All coin successfully stored!", Toast.LENGTH_SHORT).show();
                                            resetUI();
                                        });
                                    }
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    Log.e(TAG, "Error whilst storing", e);
                                }
                            });
                        }
                    }
                } else {
                    // wenn nicht genug ausgewählt
                    runOnUiThread(() -> {
                        Toast.makeText(GermanAddingActivityIISpecial.this, "(Jahr, Typ, Buchstabe) notwendig!", Toast.LENGTH_SHORT).show();
                    });
                }
            });
            // alles gespeicherte Zurücksetzen → lokale Attribute

        });
    }
    //TODO Fall: zu viel gespeichert
    private void resetUI() {
        runOnUiThread(() -> {
            for (int id : buttonIDs) {
                // nach hinzufügen gedrückt müssen alle Knöpfe wieder resettet werden, also nicht nur umrahmung weg, sondern auch, dass sie geklickt wurden:
                findViewById(id).setBackgroundColor(TRANSPARENT);
                findViewById(id).setActivated(false);
            }
            coinYear.setText("");
        });
        selectedLetters = new ArrayList<>();
        selectedTypes = new ArrayList<>();
        selectedCoinYear = Integer.MIN_VALUE;
    }
    public void doOnClick(View view) {
        TableItem item = getLetterFromId(view.getId());
        if (item == TableItem.X) { // Kein Buchstabe, sondern Typ
            item = getTypeFromId(view.getId());
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
        } else {
            if (!selectedLetters.contains(item)) {
                selectedLetters.add(item);
                view.setActivated(true);
                view.setBackground(getDrawable(R.drawable.red_border));
                // Hintergrund ändern
            } else {
                // Hintergrund ändern
                selectedLetters.remove(item);
                view.setActivated(false);
                view.setBackgroundColor(TRANSPARENT);
            }
        }
    }
    private TableItem getLetterFromId(int id) {
        if (id == R.id.addButtonA) return TableItem.A;
        if (id == R.id.addButtonD) return TableItem.D;
        if (id == R.id.addButtonF) return TableItem.F;
        if (id == R.id.addButtonG) return TableItem.G;
        if (id == R.id.addButtonJ) return TableItem.J;
        return TableItem.X;
    }
    private TableItem getTypeFromId(int id) {
        if (id == R.id.addButtonCC1) return TableItem.CC1;
        if (id == R.id.addButtonCC2) return TableItem.CC2;
        if (id == R.id.addButtonCC3) return TableItem.CC3;
        return TableItem.X;
    }
}