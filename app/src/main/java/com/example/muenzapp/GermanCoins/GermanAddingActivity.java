package com.example.muenzapp.GermanCoins;

import com.example.muenzapp.Database.*;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.muenzapp.R;
import com.example.muenzapp.TableItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static android.graphics.Color.TRANSPARENT;

public class GermanAddingActivity extends AppCompatActivity {
    final int[] buttonIDs = {R.id.addButtonA, R.id.addButtonD, R.id.addButtonF, R.id.addButtonG, R.id.addButtonJ, R.id.addButtonONE, R.id.addButtonTWO, R.id.addButtonFIVE, R.id.addButtonTEN, R.id.addButtonTWENTY, R.id.addButtonFIFTY, R.id.addButtonI, R.id.addButtonII};
    int selectedCoinYear;
    EditText coinYear;
    Button addToDatabase;
    List<TableItem> selectedLetters;
    List<TableItem> selectedValues;
    CoinDatabase coinDatabase;
    CollectionDao collectionDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.german_adding_layout);
        findViewById(R.id.closeCoinYearAdding).setOnClickListener((v) -> {
            Intent intent = new Intent(this, GermanOverviewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        coinDatabase = DatabaseClient.getInstance(this);
        collectionDao = coinDatabase.collectionDao();

        selectedLetters = new ArrayList<>();
        selectedValues = new ArrayList<>();
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
                if (selectedLetters.size() > 0 && selectedValues.size() > 0 && selectedCoinYear != 0) {
                    List<CoinEntity> coinsOfYear = collectionDao.getMissingCoinsOfYear(selectedCoinYear);
                    for (TableItem selectedLetter : selectedLetters) {
                        for (TableItem selectedValue : selectedValues) {
                            CoinEntity entity = new CoinEntity();
                            entity.setCoinYear(selectedCoinYear);
                            entity.setCoinLetter(selectedLetter);
                            entity.setCoinValue(selectedValue);
                            if (!coinsOfYear.contains(entity)) { // gar nicht notwendig, da beim Löschen immer alle mit Value und Letter gelöscht werden. Aber Vorteil: weniger gespeichert = nicht doppelt gespeichert
                                collectionDao.insertCoin(entity);
                            }
                        }
                    }
                    runOnUiThread(() -> {
                        for (int id : buttonIDs) {
                            // nach hinzufügen gedrückt müssen alle Knöpfe wieder resettet werden, also nicht nur umrahmung weg, sondern auch, dass sie geklickt wurden:
                            findViewById(id).setBackgroundColor(TRANSPARENT);
                            findViewById(id).setActivated(false);
                        }
                        coinYear.setText("");
                    });
                    selectedLetters = new ArrayList<>();
                    selectedValues = new ArrayList<>();
                    selectedCoinYear = Integer.MIN_VALUE;
                } else {
                    // wenn nicht genug ausgewählt
                }
            });
            // alles gespeicherte Zurücksetzen → lokale Attribute

        });
    }
    //TODO Fall: zu viel gespeichert
    public void doOnClick(View view) {
        TableItem item = getLetterFromId(view.getId());
        if (item == TableItem.X) { // Kein Buchstabe, sondern Zahl
            item = getValueFromId(view.getId());
            if (!selectedValues.contains(item)) {
                selectedValues.add(item);
                view.setActivated(true);
                view.setBackground(getDrawable(R.drawable.red_border));
                // Hintergrund ändern
            } else {
                //Hintergrund ändern
                selectedValues.remove(item);
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