package com.example.muenzapp.GermanCoins;

import static com.example.muenzapp.StaticHelper.stringToTableItem;

import com.example.muenzapp.Database.*;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.muenzapp.R;
import com.example.muenzapp.StartingPageActivity;
import com.example.muenzapp.TableItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class GermanOverviewActivity extends AppCompatActivity {
    //TODO getRole mit admin gespeichert in db; mit auth.getID vergleichen: je nach Rolle anpassen! sichtbarkeit und klickbarkeit

    int[] imageButtonIDs = {R.id.YearOne, R.id.YearTwo, R.id.YearThree, R.id.YearFour, R.id.YearFive, R.id.YearSix, R.id.YearEight, R.id.YearSeven};
    int[] years = {0, 0, 0, 0, 0, 0, 0, 0};
    int[] textIDs = {R.id.yearONEText, R.id.yearTWOText, R.id.yearTHREEText, R.id.yearFOURText, R.id.yearFIVEText, R.id.yearSIXText, R.id.yearEIGHTText, R.id.yearSEVENText};
    //CoinDatabase coinDatabase;
    //CollectionDao collectionDao;
    List<Integer> allYears = new ArrayList<>();
    FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.german_overview_layout);

   //     coinDatabase = DatabaseClient.getInstance(this);
   //     collectionDao = coinDatabase.collectionDao();
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.closeCoinYearAdding2).setOnClickListener(v -> {
            Intent intent = new Intent(this, StartingPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        for (int id : imageButtonIDs) {
            findViewById(id).setOnClickListener(this::openTableOfYear);
        }
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                db.collection("D").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Map<String, Object> data = document.getData();
                            Long coinYearLong = (Long) data.get("coinYear");
                            int coinYear = (int) coinYearLong.longValue();
                            if (!allYears.contains(coinYear)) {
                                allYears.add(coinYear);
                            }
                        }
                        // allYears = collectionDao.getDifferentYears();

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
                });
            }
        });
        findViewById(R.id.addCoinYear).setOnClickListener(this::createNewCoinTable);

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