package com.example.muenzapp.Database;

import androidx.room.*;

import com.example.muenzapp.TableItem;

import java.util.List;

@Dao
public interface CollectionDao {

    //GERMANY COINS:
    @Insert
    void insertCoin(CoinEntity coinEntity);
    @Query("SELECT * FROM CoinEntity WHERE CoinYear = :coinYear")
    List<CoinEntity> getMissingCoinsOfYear(int coinYear); // alle Münzen bekommen des Jahres (alle nicht gefundenen)
    @Query("DELETE FROM CoinEntity")
    void deleteAllCoins();
    @Query("DELETE FROM CoinEntity WHERE CoinYear = :coinYear AND CoinValue = :coinValue AND CoinLetter = :coinLetter")
    void foundCoin(int coinYear, TableItem coinValue, TableItem coinLetter);
    @Query("SELECT DISTINCT CoinYear FROM CoinEntity")
    List<Integer> getDifferentYears();

    //INTERNATIONAL COINS:

    @Insert
    void insertInternationalCoin(InternCoinEntity internationalCoinEntity);
    @Query("SELECT * FROM InternCoinEntity WHERE CoinCountry = :coinCountry AND CoinYear = :coinYear")
    List<InternCoinEntity> getMissingInternationalCoinsOfYearAndCountry(TableItem coinCountry, int coinYear);
    @Query("DELETE FROM InternCoinEntity")
    void deleteAllInternationalCoins();
    @Query("DELETE FROM InternCoinEntity WHERE CoinCountry = :coinCountry AND CoinYear = :coinYear AND CoinValue = :coinValue")
    void foundInternationalCoin(TableItem coinCountry, int coinYear, TableItem coinValue);
    @Query("SELECT DISTINCT CoinYear FROM InternCoinEntity WHERE CoinCountry = :coinCountry")
    List<Integer> getDifferentYearsInternational(TableItem coinCountry);
}
