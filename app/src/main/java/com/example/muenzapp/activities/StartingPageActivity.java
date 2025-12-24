package com.example.muenzapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.muenzapp.R;
import com.example.muenzapp.activities.GermanCoins.GermanOverviewActivity;
import com.example.muenzapp.activities.InternCoins.InternCoinTableActivity;
import com.example.muenzapp.data.repository.AuthRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartingPageActivity extends AppCompatActivity {

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_page_layout);

        FirebaseApp.initializeApp(this);
        authRepository = AuthRepository.getInstance();

        // 1. Setup Profil-Button (vorher Logout)
        // Wir suchen jetzt nach der neuen ID "profileButton"
        View profileBtn = findViewById(R.id.profileButton);
        profileBtn.setOnClickListener(v -> showProfileDialog());

        // 2. Setup Cards
        initCards();
    }

    private void showProfileDialog() {
        // 1. Dialog Layout vorbereiten
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_profile, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // 2. Daten in den Dialog füllen
        TextView emailText = dialogView.findViewById(R.id.textProfileEmail);
        Button logoutBtn = dialogView.findViewById(R.id.btnDialogLogout);

        // Aktuellen User abrufen um Email anzuzeigen
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            emailText.setText(user.getEmail());
        } else {
            emailText.setText("Gast / Unbekannt");
        }

        // 3. Logout Logik im Dialog-Button
        logoutBtn.setOnClickListener(v -> {
            dialog.dismiss(); // Dialog schließen
            performLogout();  // Ausloggen ausführen
        });

        // Dialog anzeigen
        dialog.show();
    }

    private void initCards() {
        // Germany (Special Handling)
        setupCard(R.id.card_germany, "D", "Deutschland");

        // Others
        setupCard(R.id.card_austria, "A", "Österreich");
        setupCard(R.id.card_france, "F", "Frankreich");
        setupCard(R.id.card_netherlands, "NL", "Niederlande");
        setupCard(R.id.card_greece, "GR", "Griechenland");
        setupCard(R.id.card_luxembourg, "L", "Luxemburg");
        setupCard(R.id.card_spain, "E", "Spanien");
        setupCard(R.id.card_italy, "I", "Italien");
        setupCard(R.id.card_ireland, "IR", "Irland");
        setupCard(R.id.card_belgium, "B", "Belgien");
        setupCard(R.id.card_finland, "FIN", "Finnland");
        setupCard(R.id.card_portugal, "P", "Portugal");
    }

    private void setupCard(int cardId, String code, String name) {
        View cardView = findViewById(cardId);
        if (cardView == null) return;

        TextView textCode = cardView.findViewById(R.id.countryCodeText);
        TextView textName = cardView.findViewById(R.id.countryNameText);

        textCode.setText(code);
        textName.setText(name);

        cardView.setOnClickListener(v -> {
            if (cardId == R.id.card_germany) {
                startActivity(new Intent(StartingPageActivity.this, GermanOverviewActivity.class));
            } else {
                Intent intent = new Intent(StartingPageActivity.this, InternCoinTableActivity.class);
                intent.putExtra("coinCountry", code);
                startActivity(intent);
            }
        });
    }

    private void performLogout() {
        authRepository.logout();
        Toast.makeText(this, "Erfolgreich ausgeloggt.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}