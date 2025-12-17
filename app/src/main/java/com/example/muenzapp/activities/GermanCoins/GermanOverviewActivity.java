package com.example.muenzapp.activities.GermanCoins;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.muenzapp.R;
import com.example.muenzapp.activities.StartingPageActivity;
import com.example.muenzapp.data.repository.CoinRepository;
import com.example.muenzapp.utils.FirestoreDataCallback;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class GermanOverviewActivity extends AppCompatActivity {

    // UI Referenzen
    private final int[] imageButtonIDs = {R.id.YearOne, R.id.YearTwo, R.id.YearThree, R.id.YearFour, R.id.YearFive, R.id.YearSix, R.id.YearEight, R.id.YearSeven};
    private final int[] textIDs = {R.id.yearONEText, R.id.yearTWOText, R.id.yearTHREEText, R.id.yearFOURText, R.id.yearFIVEText, R.id.yearSIXText, R.id.yearEIGHTText, R.id.yearSEVENText};

    // Mapping Array: Index -> Jahr (wird dynamisch gefüllt)
    private final int[] loadedYears = new int[8];

    private CoinRepository repository;
    private FirebaseAuth auth;
    private boolean isAdmin = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.german_overview_layout);

        repository = CoinRepository.getInstance();
        auth = FirebaseAuth.getInstance();

        setupNavigationButtons();
        checkAdminStatus();
        loadYearsFromDatabase();
    }

    private void setupNavigationButtons() {
        // Zurück zur Startseite
        findViewById(R.id.closeCoinYearAdding2).setOnClickListener(v -> {
            Intent intent = new Intent(this, StartingPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Zu Sonder II
        findViewById(R.id.openSonderII).setOnClickListener(v -> {
            Intent intent = new Intent(this, GermanCoinTableActivityIISpecial.class);
            startActivity(intent);
        });

        // Button zum Hinzufügen (Logik kommt in checkAdminStatus)
        findViewById(R.id.addCoinYear).setOnClickListener(this::createNewCoinTable);

        // Click Listener für die Jahr-Buttons
        for (int id : imageButtonIDs) {
            findViewById(id).setOnClickListener(this::openTableOfYear);
        }
    }

    private void checkAdminStatus() {
        if (auth.getCurrentUser() == null) return;

        repository.checkIsAdmin(auth.getUid(), new FirestoreDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                isAdmin = result;
                View addBtn = findViewById(R.id.addCoinYear);
                // Sichtbarkeit basierend auf Admin-Status setzen
                addBtn.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Overview", "Admin check failed", e);
                // Standardmäßig ausblenden bei Fehler
                findViewById(R.id.addCoinYear).setVisibility(View.GONE);
            }
        });
    }

    private void loadYearsFromDatabase() {
        repository.getAllGermanYears(new FirestoreDataCallback<List<Integer>>() {
            @Override
            public void onSuccess(List<Integer> years) {
                // UI muss immer auf dem Main Thread aktualisiert werden
                runOnUiThread(() -> updateYearButtons(years));
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Overview", "Fehler beim Laden der Jahre", e);
                Toast.makeText(GermanOverviewActivity.this, "Konnte Jahre nicht laden", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateYearButtons(List<Integer> uniqueYears) {
        // Wir haben max. 8 Buttons zur Verfügung
        int limit = Math.min(uniqueYears.size(), imageButtonIDs.length);

        for (int i = 0; i < limit; i++) {
            int year = uniqueYears.get(i);

            // 1. Jahr im Array speichern für den Click-Listener
            loadedYears[i] = year;

            // 2. Button sichtbar machen
            findViewById(imageButtonIDs[i]).setVisibility(View.VISIBLE);

            // 3. Text setzen
            TextView textView = findViewById(textIDs[i]);
            textView.setVisibility(View.VISIBLE);
            textView.setText(year < 10 ? "0" + year : String.valueOf(year));
        }

        // Restliche Buttons ausblenden (falls wir weniger Jahre haben als Buttons)
        for (int i = limit; i < imageButtonIDs.length; i++) {
            findViewById(imageButtonIDs[i]).setVisibility(View.GONE);
            findViewById(textIDs[i]).setVisibility(View.GONE);
        }
    }

    public void openTableOfYear(View view) {
        int clickedId = view.getId();
        int selectedYear = -100;

        // Finde heraus, welcher Button gedrückt wurde und hole das Jahr aus dem Array
        for (int i = 0; i < imageButtonIDs.length; i++) {
            if (clickedId == imageButtonIDs[i]) {
                selectedYear = loadedYears[i];
                break;
            }
        }

        if (selectedYear != -100 && selectedYear != 0) {
            Intent intent = new Intent(this, GermanCoinTableActivity.class);
            intent.putExtra("Year", selectedYear);
            startActivity(intent);
        }
    }

    public void createNewCoinTable(View view) {
        Intent intent = new Intent(this, GermanAddingActivity.class);
        startActivity(intent);
    }
}