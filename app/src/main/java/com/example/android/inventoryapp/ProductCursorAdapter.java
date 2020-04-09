package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView nameDisplay = (TextView) view.findViewById(R.id.name_display_view);
        TextView quantityDisplay = (TextView) view.findViewById(R.id.quantity_display_view);
        TextView priceDisplay = (TextView) view.findViewById(R.id.price_display_view);

        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

        String name = cursor.getString(nameColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        String quantityString = String.valueOf(quantity);
        int price = cursor.getInt(priceColumnIndex);
        String priceString = String.valueOf(price);

        nameDisplay.setText(name);
        quantityDisplay.setText(quantityString);
        priceDisplay.setText(priceString);

    }
}
