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
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class StartingPageActivity extends AppCompatActivity {
    int[] imageButtonIDs = {R.id.YearONE, R.id.YearTWO, R.id.YearTHREE, R.id.YearFOUR, R.id.YearFIVE, R.id.YearSIX, R.id.YearSEVEN, R.id.YearEIGHT, R.id.YearNINE, R.id.YearTEN, R.id.YearELEVEN, R.id.YearTWELVE};
    private Button logOut;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_page_layout);

        logOut = findViewById(R.id.logOut);
        logOut.setOnClickListener((v) -> {
            // Benutzer ausloggen
            FirebaseAuth.getInstance().signOut();

            // Zeige eine Bestätigung, dass der Benutzer abgemeldet wurde
            runOnUiThread(() -> {
                Toast.makeText(StartingPageActivity.this, "Erfolgreich ausgelogt.", Toast.LENGTH_SHORT).show();
            });

            // Weiterleitung zur Login-Seite
            Intent intent = new Intent(StartingPageActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Beende die aktuelle Activity, um sicherzustellen, dass der Benutzer zurück zur Login-Seite geht
        });

        FirebaseApp.initializeApp(this);

        for (int id : imageButtonIDs) {
            findViewById(id).setOnClickListener(this::doOnClick);
        }
    }
    private void doOnClick(View view) {
        int i = 0;
        for (; i < imageButtonIDs.length; i++) {
            if (view.getId() == imageButtonIDs[i]) {
                break;
            }
        }
        String country = "";
        switch (i) {
            case 0: {
                country = "A";
                break;
            }
            case 7: {
                country = "I";
                break;
            }
            case 5: {
                country = "L";
                break;
            }
            case 4: {
                Intent intent = new Intent(this, GermanOverviewActivity.class);
                startActivity(intent);
                return;
            }
            case 3: {
                country = "GR";
                break;
            }
            case 2: {
                country = "NL";
                break;
            }
            case 1: {
                country = "F";
                break;
            }
            case 6: {
                country = "E";
                break;
            }
            case 8: {
                country = "IR";
                break;
            }
            case 9: {
                country = "B";
                break;
            }
            case 10: {
                country = "FIN";
                break;
            }
            case 11: {
                country = "P";
                break;
            }
        }
        Intent intent = new Intent(this, InternCoinTableActivity.class); //TODO change
        intent.putExtra("coinCountry", country);
        startActivity(intent);
    }
}