package com.example.muenzapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muenzapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authentication extends AppCompatActivity {
    //TODO passwort nicht sichtbar evtl da sonst weiteres zeichen zum sichtbar machen erfolderlich
    private Button registerButton;
    private Button loginButton;
    private EditText email;
    private EditText password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        loginButton = findViewById(R.id.loginButton);
        email = findViewById(R.id.emailText);
        password = findViewById(R.id.passwordText);
        registerButton = findViewById(R.id.registerButton);

        auth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener((v) -> {
            String txt_email = email.getText().toString();
            String txt_password = password.getText().toString();

            if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                runOnUiThread(() -> {
                    Toast.makeText(Authentication.this, "Fehlende Email oder Passwort!", Toast.LENGTH_SHORT).show();
                });
            } else {
                registerUser(txt_email, txt_password);
            }
        });


        loginButton.setOnClickListener((view) -> {
            startActivity(new Intent(Authentication.this, LoginActivity.class));
            finish();
        });
    }
    private void registerUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Authentication.this, task -> {
            if (task.isSuccessful()) {
                runOnUiThread(() -> {
                    Toast.makeText(Authentication.this, "Registrierung erfolgreich!", Toast.LENGTH_SHORT).show();
                });
                startActivity(new Intent(Authentication.this, Authentication.class));
                finish();
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(Authentication.this, "Registrierung fehlgeschlagen!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Der Benutzer ist bereits angemeldet
            startActivity(new Intent(Authentication.this, StartingPageActivity.class));
            finish(); // Aktuelle Activity schließen
        }
    }
}