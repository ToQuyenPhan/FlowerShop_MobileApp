package com.example.flowershop.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.flowershop.helpers.DatabaseHelper;

public class CartProvider extends ContentProvider {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    public static String AUTHORITY = "com.example.flowershop";
    public static String CONTENT_PATH = "cart";
    public static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY +"/"+ CONTENT_PATH);
    static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final int URI_ALL_ITEMS_CODE = 1;
    public static final int URI_ONE_ITEM_CODE = 2;
    static final String SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.com.example.flowershop.cart";
    static final String MULTIPLE_RECORDS_MIME_TYPE =  "vnd.android.cursor.dir/vnd.com.flowershop.cart";

    Cursor cursor;
    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH, URI_ALL_ITEMS_CODE);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH+"/*", URI_ONE_ITEM_CODE);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        db = dbHelper.getReadableDatabase();
        return db.query(dbHelper.CART_TABLE_NAME, projection, selection, selectionArgs, null,
                null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case URI_ALL_ITEMS_CODE:
                return MULTIPLE_RECORDS_MIME_TYPE;
            case URI_ONE_ITEM_CODE:
                return SINGLE_RECORD_MIME_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase d = dbHelper.getWritableDatabase();
        long rowId = d.insert(CONTENT_PATH, null, contentValues);
        if (rowId > 0) {
            Uri cartUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(cartUri, null);
            return cartUri;
        }
        throw new SQLiteException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        db = dbHelper.getWritableDatabase();
        int count = db.delete(CONTENT_PATH, selection, selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        db = dbHelper.getWritableDatabase();
        int count = db.update(CONTENT_PATH, contentValues, selection, selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }
}
