package com.example.muenzapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.muenzapp.R;
import com.example.muenzapp.TableItem;
import com.example.muenzapp.activities.GermanCoins.GermanOverviewActivity;
import com.example.muenzapp.data.model.Coin;
import com.example.muenzapp.data.repository.AuthRepository;
import com.example.muenzapp.data.repository.CoinRepository;
import com.example.muenzapp.utils.FirestoreCallback;
import com.example.muenzapp.utils.FirestoreDataCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.muenzapp.R.drawable.*;
import static com.example.muenzapp.TableItem.COLLECTED;
import static com.example.muenzapp.TableItem.MISSING;

public abstract class BaseCoinTableActivity extends AppCompatActivity {

    protected TableItem[][] table;
    protected View[][] cellViews;
    protected List<Coin> missing;
    protected List<Coin> collect;
    protected CoinRepository repository;
    protected boolean isAdmin;
    protected int dimenWidth;
    protected int dimenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_coin_table);

        dimenWidth = (int) getResources().getDimension(R.dimen.text_width);
        dimenHeight = (int) getResources().getDimension(R.dimen.text_height);

        repository = CoinRepository.getInstance();
        missing = new ArrayList<>();
        collect = new ArrayList<>();

        findViewById(R.id.closeCoinTable).setOnClickListener((v) -> saveChangesAndExit());

        performAdminCheck();
        initTableLogic();
        buildDynamicTable();
        setupScrollSynchronization();
        loadDataFromRepository();
    }

    // --- Abstract Methods ---
    protected abstract void initTableLogic();
    protected abstract void loadDataFromRepository();
    protected abstract Coin createCoinFromSelection(int row, int col);
    protected abstract void addToRepo(Coin coin, FirestoreCallback callback);
    protected abstract void deleteFromRepo(Coin coin, FirestoreCallback callback);
    protected abstract void setupAdminClickListeners();

    protected boolean isCellText(int row, int col) {
        return row == 0 || col == 0;
    }

    protected String getTextForCell(int row, int col) {
        if (table[row][col] == null) return "";
        return table[row][col].toString();
    }

    private void setupScrollSynchronization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ScrollView scrollBodyVert = findViewById(R.id.scrollBodyVertical);
            ScrollView scrollSide = findViewById(R.id.scrollSide);
            HorizontalScrollView scrollBodyHorz = findViewById(R.id.scrollBodyHorizontal);
            HorizontalScrollView scrollHeader = findViewById(R.id.scrollHeader);

            // Sync Vertical
            scrollBodyVert.setOnScrollChangeListener((v, x, y, ox, oy) -> scrollSide.scrollTo(x, y));
            scrollSide.setOnScrollChangeListener((v, x, y, ox, oy) -> scrollBodyVert.scrollTo(x, y));

            // Sync Horizontal
            scrollBodyHorz.setOnScrollChangeListener((v, x, y, ox, oy) -> scrollHeader.scrollTo(x, y));
            scrollHeader.setOnScrollChangeListener((v, x, y, ox, oy) -> scrollBodyHorz.scrollTo(x, y));
        }
    }

    protected void buildDynamicTable() {
        FrameLayout containerCorner = findViewById(R.id.containerCorner);
        LinearLayout containerHeaderRow = findViewById(R.id.containerHeaderRow);
        LinearLayout containerSideCol = findViewById(R.id.containerSideCol);
        TableLayout containerBodyGrid = findViewById(R.id.containerBodyGrid);

        containerCorner.removeAllViews();
        containerHeaderRow.removeAllViews();
        containerSideCol.removeAllViews();
        containerBodyGrid.removeAllViews();

        int rows = table.length;
        int cols = table[0].length;
        cellViews = new View[rows][cols];

        for (int i = 0; i < rows; i++) {
            TableRow bodyRow = null;
            if (i > 0) bodyRow = new TableRow(this);

            for (int j = 0; j < cols; j++) {
                View cell;
                if (isCellText(i, j)) {
                    TextView tv = new TextView(this);
                    tv.setText(getTextForCell(i, j));
                    tv.setTextSize(25);
                    tv.setTextColor(Color.BLACK);
                    tv.setGravity(Gravity.CENTER);
                    if (i == 0 && j == 0) tv.setBackgroundResource(table_border_year);
                    else tv.setBackgroundResource(table_border);
                    cell = tv;
                } else {
                    ImageButton btn = new ImageButton(this);
                    btn.setBackgroundResource(table_border_active);
                    cell = btn;
                }

                // Layout Params
                ViewGroup.MarginLayoutParams params;
                // Use TableRow params for body row (i>0, j>0), otherwise standard Linear/Frame params
                if (i > 0 && j > 0) {
                    params = new TableRow.LayoutParams(dimenWidth, dimenHeight);
                } else {
                    params = new LinearLayout.LayoutParams(dimenWidth, dimenHeight);
                }
                cell.setLayoutParams(params);

                cell.setTag(new Point(i, j));
                cellViews[i][j] = cell;

                if (i == 0 && j == 0) containerCorner.addView(cell);
                else if (i == 0) containerHeaderRow.addView(cell);
                else if (j == 0) containerSideCol.addView(cell);
                else if (bodyRow != null) bodyRow.addView(cell);
            }
            if (i > 0 && bodyRow != null) containerBodyGrid.addView(bodyRow);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    protected void optimizeTableLayout() {
        int rows = table.length;
        int cols = table[0].length;

        Set<Integer> activeRows = new HashSet<>();
        Set<Integer> activeCols = new HashSet<>();

        activeRows.add(0);
        activeCols.add(0);

        for (int r = 1; r < rows; r++) {
            for (int c = 1; c < cols; c++) {
                if (table[r][c] == MISSING) {
                    activeRows.add(r);
                    activeCols.add(c);
                }
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                View view = cellViews[r][c];
                if (view == null) continue;

                boolean shouldShow = false;
                boolean isRowLabel = (c == 0) || (isCellText(r, c) && r > 0);
                boolean isMetadataColumn = (rows > 1) && isCellText(1, c);

                if (r == 0 && c == 0) shouldShow = true;
                else if (isRowLabel) { if (activeRows.contains(r)) shouldShow = true; }
                else if (r == 0) { if (activeCols.contains(c) || isMetadataColumn) shouldShow = true; }
                else { if (activeRows.contains(r) && activeCols.contains(c)) shouldShow = true; }

                if (shouldShow) {
                    view.setVisibility(View.VISIBLE);
                    if (!isCellText(r, c)) {
                        if (table[r][c] == MISSING) view.setBackground(getDrawable(table_border));
                        else view.setBackground(getDrawable(table_border_active));
                    }
                } else {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    // ... Rest of methods (save, click, admin) remain identical ...
    protected void performAdminCheck() {
        AuthRepository authRepository = AuthRepository.getInstance();
        if (authRepository.getCurrentUser() != null) {
            String uid = authRepository.getCurrentUser().getUid();
            repository.checkIsAdmin(uid, new FirestoreDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    isAdmin = result;
                    if (isAdmin) setupAdminClickListeners();
                    onAdminCheckFinished(result);
                }
                @Override
                public void onFailure(Exception e) {}
            });
        } else {
            onAdminCheckFinished(false);
        }
    }

    protected void onAdminCheckFinished(boolean isAdmin) {
        if (!isAdmin) {
            findViewById(R.id.openAddingYear).setVisibility(View.GONE);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void click(View view) {
        Point p = (Point) view.getTag();
        int row = p.x;
        int col = p.y;

        Coin coin = createCoinFromSelection(row, col);
        if (coin == null) return;

        boolean isCollectedInTable = (COLLECTED == table[row][col]);

        if (isCollectedInTable) {
            if (!missing.contains(coin)) {
                missing.add(coin);
                view.setBackground(getDrawable(table_border_red));
            } else {
                missing.remove(coin);
                view.setBackground(getDrawable(table_border_active));
            }
        } else {
            if (!collect.contains(coin)) {
                collect.add(coin);
                view.setBackground(getDrawable(table_border_active_red));
            } else {
                collect.remove(coin);
                view.setBackground(getDrawable(table_border));
            }
        }
        updateCloseButton();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    protected void updateCloseButton() {
        runOnUiThread(() -> {
            if (!missing.isEmpty() || !collect.isEmpty()) {
                findViewById(R.id.closeCoinTable).setBackground(getDrawable(table_border_red));
            } else {
                findViewById(R.id.closeCoinTable).setBackground(null);
            }
        });
    }

    protected void saveChangesAndExit() {
        int totalOps = missing.size() + collect.size();
        if (totalOps == 0) {
            goBack();
            return;
        }
        AtomicInteger completedCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        FirestoreCallback callback = new FirestoreCallback() {
            @Override
            public void onSuccess() { checkIfFinished(totalOps, completedCount.incrementAndGet(), errorCount.get()); }
            @Override
            public void onFailure(Exception e) {
                errorCount.incrementAndGet();
                checkIfFinished(totalOps, completedCount.incrementAndGet(), errorCount.get());
            }
        };

        for (Coin coin : missing) addToRepo(coin, callback);
        for (Coin coin : collect) deleteFromRepo(coin, callback);
    }

    private void checkIfFinished(int total, int current, int errors) {
        if (current >= total) {
            runOnUiThread(() -> {
                String msg = (errors > 0) ? "Errors: " + errors : "Saved!";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                goBack();
            });
        }
    }

    protected void goBack() {
        Intent intent = new Intent(this, GermanOverviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}