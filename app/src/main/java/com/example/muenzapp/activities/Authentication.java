package com.example.muenzapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muenzapp.R;
import com.example.muenzapp.data.repository.AuthRepository;
import com.example.muenzapp.utils.FirestoreCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Authentication extends AppCompatActivity {

    private TextView loginButton;
    private Button registerButton;
    private TextInputEditText emailText;
    private TextInputEditText passwordText;
    private TextInputLayout passwordInputLayout; // To show errors cleanly
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        authRepository = AuthRepository.getInstance();

        // UI setup
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);

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

        // Reset errors
        passwordInputLayout.setError(null);

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Fehlende Email oder Passwort!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            // Set error on the Layout, not the EditText, for better styling
            passwordInputLayout.setError("Passwort muss mind. 6 Zeichen lang sein");
            return;
        }

        performRegister(email, password);
    }

    private void performRegister(String email, String password) {
        // Disable button to prevent double clicks
        registerButton.setEnabled(false);

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
                    registerButton.setEnabled(true);
                    Toast.makeText(Authentication.this, "Fehler: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authRepository.isLoggedIn()) {
            navigateToStart();
        }
    }

    private void navigateToStart() {
        Intent intent = new Intent(Authentication.this, StartingPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}