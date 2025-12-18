package com.example.muenzapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muenzapp.R;
import com.example.muenzapp.data.repository.AuthRepository;
import com.example.muenzapp.utils.FirestoreCallback;

public class LoginActivity extends AppCompatActivity {

    private EditText emailText;
    private EditText passwordText;
    private AuthRepository authRepository;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize
        authRepository = AuthRepository.getInstance();

        // UI setup
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        Button loginButton = findViewById(R.id.loginButton);
        Button returnToAuth = findViewById(R.id.returnToAuth);

        // Setup Listener
        returnToAuth.setOnClickListener((v) -> {
            Intent intent = new Intent(this, Authentication.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loginButton.setOnClickListener((v) -> attemptLogin());
    }
    private void attemptLogin() {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        // Einfache Validierung vor dem Senden
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Bitte Email und Passwort eingeben!", Toast.LENGTH_SHORT).show();
            return;
        }

        authRepository.login(email, password, new FirestoreCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Login erfolgreich!", Toast.LENGTH_SHORT).show();

                    // Weiter zur Startseite
                    Intent intent = new Intent(LoginActivity.this, StartingPageActivity.class);
                    // Flags verhindern, dass man per "Zurück" wieder im Login landet
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login fehlgeschlagen: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}