package com.example.android.inventoryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class ItemDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER = 1;

    private Uri mCurrentProductUri;
    private int mQuantity = 0;

    private EditText mProductName;
    private EditText mProductQuantity;
    private EditText mProductPrice;
    private EditText mProductSupplier;

    private boolean mProductHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        Button orderButton = findViewById(R.id.order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderProduct();
            }
        });


        Button minusButton = findViewById(R.id.decrease_quantity_button);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseQuantity();
            }
        });

        Button plusButton = findViewById(R.id.increase_quantity_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseQuantity();
            }
        });

        mProductName = findViewById(R.id.product_name_view);
        mProductQuantity = findViewById(R.id.product_quantity_view);
        mProductPrice = findViewById(R.id.product_price_view);
        mProductSupplier = findViewById(R.id.product_supplier_view);

        mProductName.setOnTouchListener(mTouchListener);
        mProductQuantity.setOnTouchListener(mTouchListener);
        mProductPrice.setOnTouchListener(mTouchListener);
        mProductSupplier.setOnTouchListener(mTouchListener);

        Intent intent = getIntent();
        Uri receivedUri = intent.getData();

        if (receivedUri == null) {
            setTitle(R.string.add_screen_title);
            mProductName.setHint(R.string.product_name_hint);
            mProductPrice.setHint(R.string.price_hint);
            mProductSupplier.setHint(R.string.supplier_hint);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.edit_screen_title);
            mCurrentProductUri = receivedUri;
            getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }

    }

    private void orderProduct() {

        String name = mProductName.getText().toString().trim();

        String quantityString = mProductQuantity.getText().toString().trim();

        String supplier = mProductSupplier.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.empty_product_name, Toast.LENGTH_SHORT).show();
        } else if ((TextUtils.isEmpty(quantityString)) || (quantityString.equals("0"))) {
            Toast.makeText(this, R.string.invalid_quantity, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(supplier)) {
            Toast.makeText(this, R.string.empty_supplier, Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Order Request for " + supplier);
            String msg = "Product Name: " + name + "\nQuantity: " + quantityString;
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    private void decreaseQuantity() {
        String quantityString = mProductQuantity.getText().toString().trim();
        if (TextUtils.isEmpty(quantityString)) mProductQuantity.setText(R.string.quantity);
        else {
            mQuantity = Integer.parseInt(quantityString);
            if (mQuantity == 0) {
                Toast.makeText(this, R.string.quantity_less_than_zero, Toast.LENGTH_SHORT).show();
            } else {
                mQuantity--;
                mProductQuantity.setText(String.valueOf(mQuantity));
            }
        }
    }

    private void increaseQuantity() {
        String quantityString = mProductQuantity.getText().toString().trim();
        if (TextUtils.isEmpty(quantityString)) mProductQuantity.setText(R.string.quantity);
        else {
            mQuantity = Integer.parseInt(quantityString);
            mQuantity++;
            mProductQuantity.setText(String.valueOf(mQuantity));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_detail_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_save_product:
                saveProduct();
                return true;
            case R.id.action_delete_product:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(ItemDetailActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(ItemDetailActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void saveProduct() {
        String name = mProductName.getText().toString().trim();

        String quantityString = mProductQuantity.getText().toString().trim();

        String priceString = mProductPrice.getText().toString().trim();

        String supplier = mProductSupplier.getText().toString().trim();

        if ((mCurrentProductUri == null) && (TextUtils.isEmpty(name)) &&
                ((TextUtils.isEmpty(quantityString)) || (quantityString.equals("0"))) && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(supplier)) {
            finish();
            return;
        }
        ContentValues values = new ContentValues();
        values.putNull(ProductEntry.COLUMN_PRODUCT_NAME);
        values.putNull(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        values.putNull(ProductEntry.COLUMN_PRODUCT_PRICE);
        values.putNull(ProductEntry.COLUMN_PRODUCT_SUPPLIER);

        if (!TextUtils.isEmpty(name)) {
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        }
        try {
            int quantity = 0;
            if (!TextUtils.isEmpty(quantityString)) {
                quantity = Integer.parseInt(quantityString);
            }
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            if (!TextUtils.isEmpty(priceString)) {
                int price = Integer.parseInt(priceString);
                values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
            }
            if (!TextUtils.isEmpty(supplier)) {
                values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplier);
            }


            if (mCurrentProductUri == null) {

                Uri returnedUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
                if (returnedUri == null) {
                    Toast.makeText(this, R.string.add_product_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.add_product_success, Toast.LENGTH_SHORT).show();
                }
            } else {
                int noOfRowsUpdated = getContentResolver().update(mCurrentProductUri, values, null, null);
                if (noOfRowsUpdated == 0) {
                    Toast.makeText(this, R.string.edit_product_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.edit_product_success, Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        } catch (NumberFormatException n) {
            Toast.makeText(this, R.string.invalid_format_message, Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException i) {
            Toast.makeText(this, i.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int noOfRowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (noOfRowsDeleted == 0) {
                Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER};


        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int supplierColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);

            String name = data.getString(nameColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            String quantityString = String.valueOf(quantity);
            int price = data.getInt(priceColumnIndex);
            String priceString = String.valueOf(price);
            String supplier = data.getString(supplierColumnIndex);


            mProductName.setText(name);
            mProductQuantity.setText(quantityString);
            mProductPrice.setText(priceString);
            mProductSupplier.setText(supplier);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mProductName.getText().clear();
        mProductQuantity.getText().clear();
        mProductPrice.getText().clear();
        mProductSupplier.getText().clear();

    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_message);
        builder.setPositiveButton(R.string.unsaved_changes_positive, discardButtonClickListener);
        builder.setNegativeButton(R.string.unsaved_changes_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_product);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
