package com.example.muenzapp.InternCoins;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.muenzapp.R;
import com.example.muenzapp.StartingPageActivity;
import com.example.muenzapp.Database.*;
import com.example.muenzapp.TableItem;
import java.util.*;
import java.util.concurrent.Executors;

import static com.example.muenzapp.R.drawable.table_border;
import static com.example.muenzapp.StaticHelper.*;
import static com.example.muenzapp.TableItem.*;
public class InternCoinTableActivity extends AppCompatActivity {
    private TableItem[][] table; // default Aussehen der Tabelle
    private String[] tableYears;
    private List<CoinEntity> collect;
    private List<CoinEntity> missing;
    private final int[][] buttonIDs = {{R.id.Item00, R.id.Item01, R.id.Item02, R.id.Item03, R.id.Item04, R.id.Item05, R.id.Item06, R.id.Item07, R.id.Item08},
            {R.id.Item10, R.id.Item11, R.id.Item12, R.id.Item13, R.id.Item14, R.id.Item15, R.id.Item16, R.id.Item17, R.id.Item18},
            {R.id.Item20, R.id.Item21, R.id.Item22, R.id.Item23, R.id.Item24, R.id.Item25, R.id.Item26, R.id.Item27, R.id.Item28},
            {R.id.Item30, R.id.Item31, R.id.Item32, R.id.Item33, R.id.Item34, R.id.Item35, R.id.Item36, R.id.Item37, R.id.Item38},
            {R.id.Item40, R.id.Item41, R.id.Item42, R.id.Item43, R.id.Item44, R.id.Item45, R.id.Item46, R.id.Item47, R.id.Item48},
            {R.id.Item50, R.id.Item51, R.id.Item52, R.id.Item53, R.id.Item54, R.id.Item55, R.id.Item56, R.id.Item57, R.id.Item58},
            {R.id.Item60, R.id.Item61, R.id.Item62, R.id.Item63, R.id.Item64, R.id.Item65, R.id.Item66, R.id.Item67, R.id.Item68},
            {R.id.Item70, R.id.Item71, R.id.Item72, R.id.Item73, R.id.Item74, R.id.Item75, R.id.Item76, R.id.Item77, R.id.Item78},
            {R.id.Item80, R.id.Item81, R.id.Item82, R.id.Item83, R.id.Item84, R.id.Item85, R.id.Item86, R.id.Item87, R.id.Item88},
            {R.id.Item90, R.id.Item91, R.id.Item92, R.id.Item93, R.id.Item94, R.id.Item95, R.id.Item96, R.id.Item97, R.id.Item98},
            {R.id.Item100, R.id.Item101, R.id.Item102, R.id.Item103, R.id.Item104, R.id.Item105, R.id.Item106, R.id.Item107, R.id.Item108},
            {R.id.Item110, R.id.Item111, R.id.Item112, R.id.Item113, R.id.Item114, R.id.Item115, R.id.Item116, R.id.Item117, R.id.Item118},
            {R.id.Item120, R.id.Item121, R.id.Item122, R.id.Item123, R.id.Item124, R.id.Item125, R.id.Item126, R.id.Item127, R.id.Item128},
            {R.id.Item130, R.id.Item131, R.id.Item132, R.id.Item133, R.id.Item134, R.id.Item135, R.id.Item136, R.id.Item137, R.id.Item138},
            {R.id.Item140, R.id.Item141, R.id.Item142, R.id.Item143, R.id.Item144, R.id.Item145, R.id.Item146, R.id.Item147, R.id.Item148},
            {R.id.Item150, R.id.Item151, R.id.Item152, R.id.Item153, R.id.Item154, R.id.Item155, R.id.Item156, R.id.Item157, R.id.Item158},
            {R.id.Item160, R.id.Item161, R.id.Item162, R.id.Item163, R.id.Item164, R.id.Item165, R.id.Item166, R.id.Item167, R.id.Item168},
            {R.id.Item170, R.id.Item171, R.id.Item172, R.id.Item173, R.id.Item174, R.id.Item175, R.id.Item176, R.id.Item177, R.id.Item178},
            {R.id.Item180, R.id.Item181, R.id.Item182, R.id.Item183, R.id.Item184, R.id.Item185, R.id.Item186, R.id.Item187, R.id.Item188},
            {R.id.Item190, R.id.Item191, R.id.Item192, R.id.Item193, R.id.Item194, R.id.Item195, R.id.Item196, R.id.Item197, R.id.Item198},
            {R.id.Item200, R.id.Item201, R.id.Item202, R.id.Item203, R.id.Item204, R.id.Item205, R.id.Item206, R.id.Item207, R.id.Item208},
            {R.id.Item210, R.id.Item211, R.id.Item212, R.id.Item213, R.id.Item214, R.id.Item215, R.id.Item216, R.id.Item217, R.id.Item218},
            {R.id.Item220, R.id.Item221, R.id.Item222, R.id.Item223, R.id.Item224, R.id.Item225, R.id.Item226, R.id.Item227, R.id.Item228},
            {R.id.Item230, R.id.Item231, R.id.Item232, R.id.Item233, R.id.Item234, R.id.Item235, R.id.Item236, R.id.Item237, R.id.Item238},
            {R.id.Item240, R.id.Item241, R.id.Item242, R.id.Item243, R.id.Item244, R.id.Item245, R.id.Item246, R.id.Item247, R.id.Item248},
            {R.id.Item250, R.id.Item251, R.id.Item252, R.id.Item253, R.id.Item254, R.id.Item255, R.id.Item256, R.id.Item257, R.id.Item258}}; //Alle Items der Tabelle: alle mit 0 sind TextViews, sonst Buttons
    // TODO hier erweitern
    private CoinDatabase coinDatabase;
    private CollectionDao collectionDao;
    private TableItem coinCountry;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intern_coin_table_layout);

        //Logik Button zum Zurückgehen
        findViewById(R.id.closeCoinTable).setOnClickListener((v) -> {
            Intent intent = new Intent(this, StartingPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        findViewById(R.id.openAddingInternYear).setOnClickListener(v -> {
            Intent intent = new Intent(this, InternAddingActivity.class);
            intent.putExtra("coinCountry", getIntent().getStringExtra("coinCountry"));
            startActivity(intent);
        });
        coinCountry = findCoinCountryItem(getIntent().getStringExtra("coinCountry"));

        ((TextView) findViewById(buttonIDs[0][0])).setText(coinCountry + "");
        //table = new TableItem[][]{{X, ONE, TWO, FIVE},{A, COLLECTED, COLLECTED, COLLECTED},{D, COLLECTED, COLLECTED, COLLECTED},{F, COLLECTED, COLLECTED, COLLECTED}};
        table = new TableItem[][]{{coinCountry, ONE, TWO, FIVE, TEN, TWENTY, FIFTY, I, II},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED},
                {X, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED, COLLECTED}
        };
        //TODO hier erweitern

        // Daten aus Datenbank verwenden:
        coinDatabase = DatabaseClient.getInstance(this);
        collectionDao = coinDatabase.collectionDao();
    /*    Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                collectionDao.deleteAllInternationalCoins();
            }
        });
     */
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Integer> coinYears = collectionDao.getDifferentYearsInternational(coinCountry);
            Collections.sort(coinYears);
            tableYears = new String[coinYears.size()];
            int pointer = 0;
            for (int year : coinYears) { // eventuell bereits hier in der Tabelle das Jahr setzen
                List<InternCoinEntity> tableOfYear = collectionDao.getMissingInternationalCoinsOfYearAndCountry(coinCountry, year);
                for (InternCoinEntity coinEntity : tableOfYear) {
                    TableItem coinValue = coinEntity.getCoinValue();
                    int valuePlace = -100;
                    switch (coinValue) {
                        case ONE: {
                            valuePlace = 1;
                            break;
                        }
                        case TWO: {
                            valuePlace = 2;
                            break;
                        }
                        case FIVE: {
                            valuePlace = 3;
                            break;
                        }
                        case TEN: {
                            valuePlace = 4;
                            break;
                        }
                        case TWENTY: {
                            valuePlace = 5;
                            break;
                        }
                        case FIFTY: {
                            valuePlace = 6;
                            break;
                        }
                        case I: {
                            valuePlace = 7;
                            break;
                        }
                        case II: {
                            valuePlace = 8;
                            break;
                        }
                    } // setzt valuePlace
                    if (pointer + 1 == 26) break; //TODO hier erweitern
                    table[pointer + 1][valuePlace] = MISSING;
                    findViewById(buttonIDs[pointer][valuePlace]).setBackground(getDrawable(table_border));
                }
                tableYears[pointer++] = year < 10 ? "0"+ year : year + "";
            }
            //        for (TableItem[] tableItems : table) {
            //            System.out.println(Arrays.toString(tableItems));
            //        }
            // Unwichtige Zeilen und Spalten entfernen
            Set<Integer> rowWithMissing = new HashSet<>();
            Set<Integer> columnWithMissing = new HashSet<>();
            for (int m = 1; m < table.length; m++) {
                for (int n = 1; n < table[1].length; n++) {
                    if (table[m][n] == MISSING) {
                        rowWithMissing.add(m);
                        columnWithMissing.add(n);
                    }
                }
            }
            rowWithMissing = new HashSet<>(rowWithMissing);
            columnWithMissing = new HashSet<>(columnWithMissing);

            //        System.out.println("Reihe mit Missing: "+ Arrays.toString(rowWithMissing.toArray()));
            //        System.out.println("Zeile mit Missing: "+ Arrays.toString(columnWithMissing.toArray()));
            int pointerRow = 1;
            int pointerColumn = 1;
            for (int row : rowWithMissing) {
                ((TextView)findViewById(buttonIDs[pointerRow][0])).setText(tableYears[row - 1] + "");
                //            System.out.println("Buchstabe zu Platzieren: " + table[row][0]+" at Place in Array ["+pointerRow+"][0]");
                //        table[pointerRow][0] = table[row][0]; // TODO muss noch angepasst werden, da die Jahreszahl nicht als Item vorhanden ist Nein, da später nicht verewendet, nur mit tableYears[]
                for (int column : columnWithMissing) {
                    if (pointerRow == 1) {
                        ((TextView)findViewById(buttonIDs[0][pointerColumn])).setText(findValueString(table[0][column]));
                        //                    System.out.println("Zahl zu Platzieren: "+table[0][column]+" at Place in Array [0]["+pointerColumn+"]");
                        table[0][pointerColumn] = table[0][column];
                    }
                    if (table[row][column] == MISSING) {
                        findViewById(buttonIDs[pointerRow][pointerColumn]).setBackground(getDrawable(table_border));
                        table[pointerRow][pointerColumn] = MISSING;
                        //                    System.out.println("log table at [" + row +"]["+column+"] as" + " Missing and Place in Array ["+pointerRow+"]["+pointerColumn+"]");
                    } else {
                        findViewById(buttonIDs[pointerRow][pointerColumn]).setBackground(getDrawable(R.drawable.table_border_active));
                        table[pointerRow][pointerColumn] = COLLECTED;
                        //                    System.out.println("log table at [" + row +"]["+column+"] as Collected " + "and Place in Array ["+pointerRow+"]["+pointerColumn+"]");
                    }
                    pointerColumn++;
                }
                pointerRow++;
                pointerColumn = 1;
            }


            //    for (TableItem[] tableItems : table) { // for debugging
            //        System.out.println(Arrays.toString(tableItems));
            //        }

            // andere Buttons ausblenden
            int lastRow = table.length - 1;
            int lastColumn = table[1].length - 1;
            for (int row = 1; row < table.length; row++) {
                if (!rowWithMissing.contains(row)) {
                    for (int k = 0; k < table[1].length; k++) {
                        findViewById(buttonIDs[lastRow][k]).setVisibility(View.INVISIBLE);
                        //                    System.out.println("Zeile zu Entfernen: "+ table[lastRow][k]+" at Place in Array ["+lastRow+"]["+k+"]");
                    }
                    lastRow--;
                }
            }
            for (int column = 1; column < table[1].length; column++) {
                if (!columnWithMissing.contains(column)) {
                    for (int k = 0; k < table.length; k++) {
                        findViewById(buttonIDs[k][lastColumn]).setVisibility(View.INVISIBLE);
                        //                System.out.println("Spalte zu Entfernen: "+ table[k][lastColumn]+" at Place in Array ["+k+"]["+lastColumn+"]");
                    }
                    lastColumn--;
                }
            }
        });
        for (int i = 1; i < table.length; i++) {
            for (int j = 1; j < table[1].length; j++) {
                findViewById(buttonIDs[i][j]).setOnClickListener(this::click);
            }
        }
    }
    private int row = -100;
    private int column = -100;
    public void click(View view) {

        //    for (TableItem[] tableItems : table) {
        //        System.out.println(Arrays.toString(tableItems));
        //    }

        //Hintergrund ändern
        int id = view.getId();
        boolean found = false;
        for (int n = 1; n < table.length; n++) {
            for (int m = 1; m < table[1].length; m++) {
                if (buttonIDs[n][m] == id) {
                    found = true;
                    row = n;
                    column = m;
                    break;
                }
            }
            if (found) break;
        } // row und column zuweisen
        boolean isActive = (COLLECTED == table[row][column]);
        if (isActive) { // not in Database
            table[row][column] = MISSING;
            InternCoinEntity coinEntity = new InternCoinEntity();
            //         System.out.println("value: " + table[0][column]);
            coinEntity.setCoinValue(table[0][column]);
            //          System.out.println("Year: "+ tableYears[row - 1]);
            coinEntity.setCoinYear(Integer.parseInt(tableYears[row - 1]));
            Executors.newSingleThreadExecutor().execute(() -> {

                collectionDao.insertInternationalCoin(coinEntity);
            });
            view.setBackground(getDrawable(table_border)); // wieder normal
        } else {
            table[row][column] = COLLECTED;
            view.setBackground(getDrawable(R.drawable.table_border_active));
            Executors.newSingleThreadExecutor().execute(() -> {
                collectionDao.foundInternationalCoin(coinCountry, Integer.parseInt(tableYears[row - 1]), table[0][column]);
                //             System.out.println("Es wird entfernt: "+ tableYears[row - 1]+ " und "+ table[0][column]);
            });
        }

    }
}