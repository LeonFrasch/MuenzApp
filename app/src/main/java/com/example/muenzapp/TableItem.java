package com.example.muenzapp;

public enum TableItem {
    A, D, F, G, J, // Buchstaben-Werte deutsche Münzen
    COLLECTED, MISSING, // Boolsche Werte: gefundene vs nicht gefundene (Standard) Münzen
    ONE, TWO, FIVE, TEN, TWENTY, FIFTY, I, II, // Zahlen-Werte deutsche Münzen
    X, // Platzhalter (z.B. für Jahreszahl)
    E, L, GR, FIN, IR, NL, B, P, //Länder + F, I, A
    TYP, CC1, CC2, CC3 // Sonder II
}
