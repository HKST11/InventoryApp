package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {
    ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        int productNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int modelNoColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_MODEL_NO);
        int productPriceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int productImageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);

        ImageView imageDisplay = view.findViewById(R.id.image_display_view);
        TextView nameDisplay = view.findViewById(R.id.name_display_view);
        TextView modelNoDisplay = view.findViewById(R.id.model_no_display_view);
        TextView priceDisplay = view.findViewById(R.id.price_display_view);
        TextView quantityDisplay = view.findViewById(R.id.quantity_display_view);
        Button saleButton = view.findViewById(R.id.sale_button);

        String imageUriString = cursor.getString(productImageColumnIndex);
        String name = cursor.getString(productNameColumnIndex);
        String modelNo = cursor.getString(modelNoColumnIndex);
        int quantity = cursor.getInt(productQuantityColumnIndex);
        String quantityString = String.valueOf(quantity);
        int price = cursor.getInt(productPriceColumnIndex);
        String priceString = String.valueOf(price);

        if ((imageUriString != null) && (imageUriString.length() != 0)) {
            Uri imageUri = Uri.parse(imageUriString);
            imageDisplay.setImageURI(imageUri);
        } else {
            imageDisplay.setImageResource(R.mipmap.no_image_available);
        }
        nameDisplay.setText(name);
        modelNoDisplay.setText(modelNo);
        String displayQuantity = quantityString + " in stock";
        quantityDisplay.setText(displayQuantity);
        String displayPrice = "Rs. " + priceString;
        priceDisplay.setText(displayPrice);
        saleButton.setText(R.string.sale_label);

    }
}
