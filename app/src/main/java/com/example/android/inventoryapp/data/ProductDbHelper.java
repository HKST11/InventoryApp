package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class ProductDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + ProductEntry.TABLE_NAME + "(" +
                ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL," +
                ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER DEFAULT(0)," +
                ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL," +
                ProductEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT DEFAULT('UNKNOWN'));";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
