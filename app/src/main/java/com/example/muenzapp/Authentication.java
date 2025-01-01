package com.example.muenzapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class Authentication extends AppCompatActivity {
    private Button registerButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);

        registerButton.setOnClickListener((view) -> {
            startActivity(new Intent(Authentication.this, RegisterActivity.class));
            finish();
        });
        loginButton.setOnClickListener((view) -> {
            startActivity(new Intent(Authentication.this, LoginActivity.class));
            finish();
        });
    }
}