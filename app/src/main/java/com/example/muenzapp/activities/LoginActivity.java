package com.example.muenzapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.muenzapp.R;
import com.example.muenzapp.data.repository.AuthRepository;
import com.example.muenzapp.utils.FirestoreCallback;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailText;
    private TextInputEditText passwordText;
    private AuthRepository authRepository;
    private Button loginButton; // Can be MaterialButton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = AuthRepository.getInstance();

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.loginButton);
        View returnToAuth = findViewById(R.id.returnToAuth);

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

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Bitte Email und Passwort eingeben!", Toast.LENGTH_SHORT).show();
            return;
        }

        loginButton.setEnabled(false); // Disable button while loading

        authRepository.login(email, password, new FirestoreCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Login erfolgreich!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, StartingPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    loginButton.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Login fehlgeschlagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}