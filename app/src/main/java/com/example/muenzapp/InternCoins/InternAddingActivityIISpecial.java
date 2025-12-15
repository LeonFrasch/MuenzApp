package com.example.muenzapp.InternCoins;

import static android.content.ContentValues.TAG;
import static android.graphics.Color.TRANSPARENT;

import static com.example.muenzapp.StaticHelper.findCoinCountryItem;
import static com.example.muenzapp.StaticHelper.findCoinCountryStringFull;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.muenzapp.R;
import com.example.muenzapp.TableItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class InternAddingActivityIISpecial extends AppCompatActivity {

    final int[] buttonIDs = {R.id.addButtonA, R.id.addButtonD, R.id.addButtonF, R.id.addButtonG, R.id.addButtonJ, R.id.addButtonCC1, R.id.addButtonCC2, R.id.addButtonCC3};
    int selectedCoinYear;
    EditText coinYear;
    Button addToDatabase;
    List<TableItem> selectedTypes;
    TableItem selectedCoinCountry;
    FirebaseFirestore db;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adding_layout_special_ii);
        selectedCoinCountry = findCoinCountryItem(getIntent().getStringExtra("coinCountry"));
        ((TextView)findViewById(R.id.country)).setText(findCoinCountryStringFull(getIntent().getStringExtra("coinCountry")));
        findViewById(R.id.closeCoinYearAdding).setOnClickListener((v) -> {
            Intent intent = new Intent(this, InternCoinTableActivityIISpecial.class);
            intent.putExtra("coinCountry", getIntent().getStringExtra("coinCountry"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        int[] unneededIDs = {R.id.letterText, R.id.addButtonA, R.id.addButtonD, R.id.addButtonF, R.id.addButtonG, R.id.addButtonJ};
        for (int id : unneededIDs) {
            findViewById(id).setVisibility(View.GONE);
        }

        db = FirebaseFirestore.getInstance();

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
                if (selectedTypes.size() > 0 && selectedCoinYear >= 0) {
                    for (TableItem selectedType : selectedTypes) {
                        Map<String, Object> coin = new HashMap<>();
                        coin.put("coinYear", selectedCoinYear);
                        coin.put("coinType", selectedType);
                        coin.put("coinCountry", selectedCoinCountry);
                        String filename = selectedCoinYear + ":" + selectedCoinCountry + ":" + selectedType;
                        db.collection("Sonder").document("IISonder").collection(selectedCoinCountry + "").document(filename)
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
                    selectedTypes = new ArrayList<>();
                    selectedCoinYear = Integer.MIN_VALUE;
                    runOnUiThread(() -> {
                        Toast.makeText(InternAddingActivityIISpecial.this, "Erfolgreich hinzugefügt!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // wenn nicht genug ausgewählt
                    runOnUiThread(() -> {
                        Toast.makeText(InternAddingActivityIISpecial.this, "(Jahr, Typ) notwendig!", Toast.LENGTH_SHORT).show();
                    });
                }
            });
            // alles gespeicherte Zurücksetzen → lokale Attribute

        });
    }
    //TODO Fall: zu viel gespeichert
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