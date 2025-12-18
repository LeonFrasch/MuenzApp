package com.example.muenzapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muenzapp.R;
import com.example.muenzapp.data.repository.AuthRepository;
import com.example.muenzapp.utils.FirestoreCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authentication extends AppCompatActivity {
    //TODO passwort nicht sichtbar evtl da sonst weiteres zeichen zum sichtbar machen erforderlich
    private Button registerButton;
    private Button loginButton;
    private EditText emailText;
    private EditText passwordText;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // Initialize
        authRepository = AuthRepository.getInstance();

        // UI setup
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);

        // Setup Listener
        registerButton.setOnClickListener(v -> attemptRegistration());

        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(Authentication.this, LoginActivity.class));
            finish();
        });
    }
    private void attemptRegistration() {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Fehlende Email oder Passwort!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Passwortlänge prüfen (Firebase verlangt min. 6 Zeichen)
        if (password.length() < 6) {
            passwordText.setError("Passwort muss mind. 6 Zeichen lang sein");
            return;
        }

        performRegister(email, password);
    }
    private void performRegister(String email, String password) {
        authRepository.register(email, password, new FirestoreCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(Authentication.this, "Registrierung erfolgreich!", Toast.LENGTH_SHORT).show();
                    navigateToStart();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(Authentication.this, "Registrierung fehlgeschlagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Check ob User bereits eingeloggt ist (Repository nutzen)
        if (authRepository.isLoggedIn()) {
            navigateToStart();
        }
    }

    private void navigateToStart() {
        Intent intent = new Intent(Authentication.this, StartingPageActivity.class);
        // Flags setzen, damit man nicht mit "Zurück" wieder auf die Login-Seite kommt
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}