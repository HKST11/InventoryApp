package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    public void bindView(View view, final Context context, final Cursor cursor) {
        final int productIdColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
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
        final ImageButton saleButton = view.findViewById(R.id.sale_button);

        String imageUriString = cursor.getString(productImageColumnIndex);
        String name = cursor.getString(productNameColumnIndex);
        String modelNo = cursor.getString(modelNoColumnIndex);
        final int quantity = cursor.getInt(productQuantityColumnIndex);
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
        String displayQuantity = "In stock: " + quantityString;
        quantityDisplay.setText(displayQuantity);
        String displayPrice = context.getString(R.string.rupees_sign) + " " + priceString + "/-";

        priceDisplay.setText(displayPrice);

        saleButton.clearFocus();
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView productListView = (ListView) view.getParent().getParent();
                int position = productListView.getPositionForView(view);
                long id = productListView.getItemIdAtPosition(position);
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                saleProduct(currentProductUri, quantity, context);
            }
        });
    }

    private void saleProduct(Uri currentProductUri, int quantity, Context context) {
        if (quantity == 0) {
            Toast.makeText(context, R.string.out_of_stock_msg, Toast.LENGTH_SHORT).show();
        } else {
            quantity--;
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            int noOfRowsUpdated = context.getContentResolver().update(currentProductUri, values, null, null);
            if (noOfRowsUpdated <= 0) {
                Toast.makeText(context, R.string.sale_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
