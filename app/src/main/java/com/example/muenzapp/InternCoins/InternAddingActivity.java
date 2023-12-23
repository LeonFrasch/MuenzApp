package com.example.muenzapp.InternCoins;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.muenzapp.R;
import com.example.muenzapp.Database.*;
import com.example.muenzapp.TableItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static android.graphics.Color.TRANSPARENT;
import static com.example.muenzapp.R.*;
import static com.example.muenzapp.StaticHelper.*;

public class InternAddingActivity extends AppCompatActivity {

    final int[] buttonIDs = {id.addButtonONE, id.addButtonTWO, id.addButtonFIVE, id.addButtonTEN, id.addButtonTWENTY, id.addButtonFIFTY, id.addButtonI, id.addButtonII};
    int selectedCoinYear;
    EditText coinYear;
    Button addToDatabase;
    List<TableItem> selectedValues;
    CoinDatabase coinDatabase;
    CollectionDao collectionDao;
    TableItem selectedCoinCountry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.intern_adding_layout);
        selectedCoinCountry = findCoinCountryItem(getIntent().getStringExtra("coinCountry"));

        ((TextView)findViewById(id.Country)).setText(findCoinCountryStringFull(getIntent().getStringExtra("coinCountry")));

        findViewById(id.closeCoinYearAdding).setOnClickListener((v) -> {
            Intent intent = new Intent(this, InternCoinTableActivity.class); //TODO change
            intent.putExtra("coinCountry", getIntent().getStringExtra("coinCountry"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        coinDatabase = DatabaseClient.getInstance(this);
        collectionDao = coinDatabase.collectionDao();

        selectedValues = new ArrayList<>();
        selectedCoinYear = Integer.MIN_VALUE;
        for (int id : buttonIDs) {
            findViewById(id).setOnClickListener(this::doOnClick);
        }
        coinYear = findViewById(id.addYearEditText);
        addToDatabase = findViewById(id.addToDatabase);


        addToDatabase.setOnClickListener(v -> {
            String yearString = coinYear.getText().toString();
            try {
                selectedCoinYear = Integer.parseInt(yearString);
            } catch (NumberFormatException e) {
                // falsches Format // TODO
            }
            Executors.newSingleThreadExecutor().execute(() -> {
                if (selectedValues.size() > 0 && selectedCoinYear >= 0) {
                    List<InternCoinEntity> coinsOfYear = collectionDao.getMissingInternationalCoinsOfYearAndCountry(selectedCoinCountry, selectedCoinYear);
                    for (TableItem selectedValue : selectedValues) {
                        InternCoinEntity entity = new InternCoinEntity();
                        entity.setCoinYear(selectedCoinYear);
                        entity.setCoinValue(selectedValue);
                        entity.setCoinCountry(selectedCoinCountry);
                        if (!coinsOfYear.contains(entity)) { // gar nicht notwendig, da beim Löschen immer alle mit Value und Letter gelöscht werden. Aber Vorteil: weniger gespeichert = nicht doppelt gespeichert
                            collectionDao.insertInternationalCoin(entity);
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