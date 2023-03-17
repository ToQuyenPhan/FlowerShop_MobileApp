package com.example.flowershop.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CartSQLite";
    public static final String CART_TABLE_NAME = "Cart";
    public static final String CART_COLUMN_ID = "id";
    public static final String CART_COLUMN_NAME = "name";
    public static final String CART_COLUMN_PRICE = "price";
    public static final String CART_COLUMN_IMAGE = "image";
    public static final String CART_COLUMN_QUANTITY = "quantity";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CART_TABLE_NAME + "(" + CART_COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
                + CART_COLUMN_NAME + " TEXT NOT NULL, " + CART_COLUMN_PRICE + " REAL NOT NULL, "
                + CART_COLUMN_IMAGE + " TEXT NOT NULL, " + CART_COLUMN_QUANTITY + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
