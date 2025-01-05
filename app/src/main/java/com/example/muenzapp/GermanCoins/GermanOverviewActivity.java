package com.example.muenzapp.GermanCoins;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.muenzapp.R;
import com.example.muenzapp.StartingPageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class GermanOverviewActivity extends AppCompatActivity {
    int[] imageButtonIDs = {R.id.YearOne, R.id.YearTwo, R.id.YearThree, R.id.YearFour, R.id.YearFive, R.id.YearSix, R.id.YearEight, R.id.YearSeven};
    int[] years = {0, 0, 0, 0, 0, 0, 0, 0};
    int[] textIDs = {R.id.yearONEText, R.id.yearTWOText, R.id.yearTHREEText, R.id.yearFOURText, R.id.yearFIVEText, R.id.yearSIXText, R.id.yearEIGHTText, R.id.yearSEVENText};
    private List<Integer> allYears = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private boolean isAdmin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.german_overview_layout);

        db = FirebaseFirestore.getInstance(); // Database instance

        auth = FirebaseAuth.getInstance(); // Authentication instance

        findViewById(R.id.closeCoinYearAdding2).setOnClickListener(v -> { // close Screen -> go back to StartingPage
            Intent intent = new Intent(this, StartingPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        db.collection("userRole").get().addOnSuccessListener(queryDocumentSnapshots0 -> {
            List<String> adminUIDs = new ArrayList<>();
            if (!queryDocumentSnapshots0.isEmpty()) {
                for (DocumentSnapshot document : queryDocumentSnapshots0.getDocuments()) {
                    Map<String, Object> admins = document.getData();
                    adminUIDs.add(admins.get("UID").toString());
                }
            }
            if (adminUIDs.contains(auth.getUid())) { // User ist admin
                isAdmin = true;
            }
            for (int id : imageButtonIDs) {
                findViewById(id).setOnClickListener(this::openTableOfYear);
            }
            Executors.newSingleThreadExecutor().execute(() -> db.collection("D").get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Map<String, Object> data = document.getData();
                        Long coinYearLong = (Long) data.get("coinYear");
                        int coinYear = (int) coinYearLong.longValue();
                        if (!allYears.contains(coinYear)) {
                            allYears.add(coinYear);
                        }
                    }
                    runOnUiThread(() -> {
                        for (int i = 0; i < allYears.size(); i++) {
                            if (i == 8) {
                                break;
                            }
                            findViewById(imageButtonIDs[i]).setVisibility(View.VISIBLE);
                            years[i] = allYears.get(i);
                            findViewById(textIDs[i]).setVisibility(View.VISIBLE);

                            String year = allYears.get(i) < 10 ? "0" + allYears.get(i) : allYears.get(i) + "";
                            ((TextView)findViewById(textIDs[i])).setText(year);
                        }
                    });
                }
            }));
            findViewById(R.id.addCoinYear).setOnClickListener(this::createNewCoinTable);
            if (isAdmin) {
                findViewById(R.id.addCoinYear).setVisibility(View.GONE);
            }
        });
    }
    public void openTableOfYear(View view) {
        int year = -100;
        int id = view.getId();
        for (int i = 0; i < 8; i++) {
            if (id == imageButtonIDs[i]) {
                year = years[i];
            }
        }
        Intent intent = new Intent(this, GermanCoinTableActivity.class);
        intent.putExtra("Year", year);
        startActivity(intent);
    }
    public void createNewCoinTable(View view) {
        Intent intent = new Intent(this, GermanAddingActivity.class);
        startActivity(intent);
    }
}