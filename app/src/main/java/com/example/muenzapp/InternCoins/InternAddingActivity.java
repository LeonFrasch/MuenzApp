package com.example.muenzapp.InternCoins;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.muenzapp.GermanCoins.GermanAddingActivity;
import com.example.muenzapp.LoginActivity;
import com.example.muenzapp.R;
import com.example.muenzapp.Database.*;
import com.example.muenzapp.TableItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;
import static android.graphics.Color.TRANSPARENT;
import static com.example.muenzapp.R.*;
import static com.example.muenzapp.StaticHelper.*;

public class InternAddingActivity extends AppCompatActivity {
    final int[] buttonIDs = {id.addButtonONE, id.addButtonTWO, id.addButtonFIVE, id.addButtonTEN, id.addButtonTWENTY, id.addButtonFIFTY, id.addButtonI, id.addButtonII};
    int selectedCoinYear;
    EditText coinYear;
    Button addToDatabase;
    List<TableItem> selectedValues;
    TableItem selectedCoinCountry;
    FirebaseFirestore db;
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

        db = FirebaseFirestore.getInstance();

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
                if (!selectedValues.isEmpty() && selectedCoinYear >= 0) {
                    for (TableItem selectedValue : selectedValues) {
                        Map<String, Object> coin = new HashMap<>();
                        coin.put("coinYear", selectedCoinYear);
                        coin.put("coinValue", selectedValue);
                        String filename = selectedCoinYear + ":" + selectedValue;
                        db.collection(selectedCoinCountry.toString()).document(filename)
                                .set(coin, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
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
                    runOnUiThread(() -> {
                        Toast.makeText(InternAddingActivity.this, "Erfolgreich hinzugefügt!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // wenn nicht genug ausgewählt
                    runOnUiThread(() -> {
                        Toast.makeText(InternAddingActivity.this, "(Jahr, Buchstabe, Euro/Cent) notwendig!", Toast.LENGTH_SHORT).show();
                    });
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