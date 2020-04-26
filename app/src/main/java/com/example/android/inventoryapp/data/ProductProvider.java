package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class ProductProvider extends ContentProvider {
    private ProductDbHelper mDbHelper;

    private static final int PRODUCTS_URI_CODE = 100;
    private static final int PRODUCTS_ID_URI_CODE = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /*
    Adds the two types of expected URIs to the UriMatcher
     */
    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS_URI_CODE);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCTS_ID_URI_CODE);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortBy) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS_URI_CODE:
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortBy);   //returns the whole table
                break;
            case PRODUCTS_ID_URI_CODE:
                /*
                returns the cursor with the selected items
                 */
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortBy);
                break;
            default:
                throw new IllegalArgumentException(R.string.unknown_uri + uri.toString());
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS_URI_CODE:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCTS_ID_URI_CODE:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException(R.string.unknown_uri + uri.toString());
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        /*
        Checks for null value in Product name and price fields
         */
        String name = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if ((name == null) || (name.length() == 0)) {
            throw new IllegalArgumentException("Product requires a name");
        }
        Integer price = contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Product requires a price");
        }
        long returnedId;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS_URI_CODE:
                returnedId = db.insert(ProductEntry.TABLE_NAME, null, contentValues);   //inserts the new product
                break;
            default:
                throw new IllegalArgumentException(R.string.unknown_uri + uri.toString());
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);  //Notify the Loader if any insertion occurs in the database
        }
        if (returnedId == -1) return null;

        return (ContentUris.withAppendedId(uri, returnedId));
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int noOfRowsDeleted;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS_URI_CODE:
                noOfRowsDeleted = db.delete(ProductEntry.TABLE_NAME, null, null);   //deletes multiple rows
                break;
            case PRODUCTS_ID_URI_CODE:
                /*
                deletes the row with the given item id
                 */
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                noOfRowsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(R.string.unknown_uri + uri.toString());
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);  //Notify the Loader if any deletion occurs in the database
        }
        return noOfRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.size() == 0) {
            return 0;
        }
        /*
        Checks for null value in Product name and price fields
         */
        if (contentValues.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if ((name == null) || (name.length() == 0)) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }
        if (contentValues.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Product requires a price");
            }
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int noOfRowsUpdated;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS_URI_CODE:
                noOfRowsUpdated = db.update(ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);  //updates multiple rows
                break;
            case PRODUCTS_ID_URI_CODE:
                /*
                updates the row with the given item id
                 */
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                noOfRowsUpdated = db.update(ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(R.string.unknown_uri + uri.toString());
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);  //Notify the Loader if any update occurs in the database
        }
        return noOfRowsUpdated;
    }
}
