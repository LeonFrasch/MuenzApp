package com.example.muenzapp;

import com.example.muenzapp.GermanCoins.GermanOverviewActivity;
import com.example.muenzapp.InternCoins.InternCoinTableActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class StartingPageActivity extends AppCompatActivity {
    int[] imageButtonIDs = {R.id.YearONE, R.id.YearTWO, R.id.YearTHREE, R.id.YearFOUR, R.id.YearFIVE, R.id.YearSIX, R.id.YearSEVEN, R.id.YearEIGHT, R.id.YearNINE, R.id.YearTEN, R.id.YearELEVEN, R.id.YearTWELVE};
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_page_layout);

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