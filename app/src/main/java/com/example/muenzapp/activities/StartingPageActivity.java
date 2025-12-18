package com.example.muenzapp.activities;

import com.example.muenzapp.R;
import com.example.muenzapp.activities.GermanCoins.GermanOverviewActivity;
import com.example.muenzapp.activities.InternCoins.InternCoinTableActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.example.muenzapp.data.repository.AuthRepository;
import com.google.firebase.FirebaseApp;

public class StartingPageActivity extends AppCompatActivity {
    int[] imageButtonIDs = {R.id.YearONE, R.id.YearTWO, R.id.YearTHREE, R.id.YearFOUR, R.id.YearFIVE, R.id.YearSIX, R.id.YearSEVEN, R.id.YearEIGHT, R.id.YearNINE, R.id.YearTEN, R.id.YearELEVEN, R.id.YearTWELVE};
    private AuthRepository authRepository;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_page_layout);

        FirebaseApp.initializeApp(this);
        authRepository = AuthRepository.getInstance();

        setupLogoutButton();
        setupCountryButtons();
    }
    private void setupLogoutButton() {
        Button logOut = findViewById(R.id.logOut);
        logOut.setOnClickListener(v -> {
            authRepository.logout();

            Toast.makeText(StartingPageActivity.this, "Erfolgreich ausgelogt.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(StartingPageActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    private void setupCountryButtons() {
        for (int id : imageButtonIDs) {
            findViewById(id).setOnClickListener(this::handleCountryClick);
        }
    }
    private void handleCountryClick(View view) {
        int id = view.getId();

        if (id == R.id.YearFIVE) {
            Intent intent = new Intent(this, GermanOverviewActivity.class);
            startActivity(intent);
            return;
        }

        // Alle anderen Länder
        String countryCode = getCountryCodeFromId(id);

        if (!countryCode.isEmpty()) {
            Intent intent = new Intent(this, InternCoinTableActivity.class);
            intent.putExtra("coinCountry", countryCode);
            startActivity(intent);
        }
    }
    private String getCountryCodeFromId(int id) {
        if (id == R.id.YearONE) return "A";         // Österreich (Index 0)
        if (id == R.id.YearTWO) return "F";         // Frankreich (Index 1)
        if (id == R.id.YearTHREE) return "NL";      // Niederlande (Index 2)
        if (id == R.id.YearFOUR) return "GR";       // Griechenland (Index 3)
        // YearFIVE ist DE (Index 4) - oben behandelt
        if (id == R.id.YearSIX) return "L";         // Luxemburg (Index 5)
        if (id == R.id.YearSEVEN) return "E";       // Spanien (Index 6)
        if (id == R.id.YearEIGHT) return "I";       // Italien (Index 7)
        if (id == R.id.YearNINE) return "IR";       // Irland (Index 8)
        if (id == R.id.YearTEN) return "B";         // Belgien (Index 9)
        if (id == R.id.YearELEVEN) return "FIN";    // Finnland (Index 10)
        if (id == R.id.YearTWELVE) return "P";      // Portugal (Index 11)

        return "";
    }
}