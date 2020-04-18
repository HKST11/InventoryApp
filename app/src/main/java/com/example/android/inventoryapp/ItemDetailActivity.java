package com.example.android.inventoryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class ItemDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE = 0;

    private Uri mCurrentProductUri;
    private int mQuantity = 0;

    private EditText mProductName;
    private EditText mProductModelNo;
    private EditText mProductPrice;
    private ImageView mProductImage;
    private EditText mSupplierName;
    private EditText mSupplierEmailId;
    private EditText mProductQuantity;

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

        mProductName = findViewById(R.id.product_name_view);
        mProductModelNo = findViewById(R.id.product_model_no_view);
        mProductPrice = findViewById(R.id.product_price_view);
        mProductQuantity = findViewById(R.id.product_quantity_view);
        mProductImage = findViewById(R.id.product_image_view);
        mProductImage.setFocusableInTouchMode(true);
        mProductImage.requestFocus();
        mSupplierName = findViewById(R.id.supplier_name_view);
        mSupplierEmailId = findViewById(R.id.supplier_email_id_view);
        Button plusButton = findViewById(R.id.increase_quantity_button);
        Button minusButton = findViewById(R.id.decrease_quantity_button);
        Button orderButton = findViewById(R.id.order_button);
        Button imageButton = findViewById(R.id.image_button);

        mProductName.setOnTouchListener(mTouchListener);
        mProductModelNo.setOnTouchListener(mTouchListener);
        mProductPrice.setOnTouchListener(mTouchListener);
        mProductQuantity.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierEmailId.setOnTouchListener(mTouchListener);

        if (ProductEntry.savedUri != null) mProductImage.setImageURI(ProductEntry.savedUri);
        else {
            mProductImage.setImageResource(R.mipmap.no_image_available);
        }

        Intent intent = getIntent();
        Uri receivedUri = intent.getData();
        if (receivedUri == null) {
            setTitle(R.string.add_screen_title);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.edit_screen_title);
            mCurrentProductUri = receivedUri;
            getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductHasChanged = true;
                increaseQuantity();
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductHasChanged = true;
                decreaseQuantity();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductHasChanged = true;
                tryOpeningGallery();
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductHasChanged = true;
                orderProduct();
            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_MODEL_NO,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_EMAIL};

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
            int productNameColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int productModelNoColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_MODEL_NO);
            int productPriceColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int productImageColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);
            int supplierNameColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierEmailColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_EMAIL);

            String productName = data.getString(productNameColumnIndex);
            String productModelNo = data.getString(productModelNoColumnIndex);
            int price = data.getInt(productPriceColumnIndex);
            String priceString = String.valueOf(price);
            int quantity = data.getInt(productQuantityColumnIndex);
            String quantityString = String.valueOf(quantity);
            String imageUriString = data.getString(productImageColumnIndex);
            String supplierName = data.getString(supplierNameColumnIndex);
            String supplierEmail = data.getString(supplierEmailColumnIndex);


            mProductName.setText(productName);
            mProductModelNo.setText(productModelNo);
            mProductPrice.setText(priceString);
            mProductQuantity.setText(quantityString);
            if ((imageUriString != null) && (imageUriString.length() != 0)) {
                Uri imageUri = Uri.parse(imageUriString);
                mProductImage.setImageURI(imageUri);
            } else {
                mProductImage.setImageResource(R.mipmap.no_image_available);
            }
            mSupplierName.setText(supplierName);
            mSupplierEmailId.setText(supplierEmail);

            getSupportLoaderManager().destroyLoader(PRODUCT_LOADER);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mProductName.getText().clear();
        mProductModelNo.getText().clear();
        mProductPrice.getText().clear();
        mProductQuantity.getText().clear();
        mProductImage.setImageResource(R.mipmap.no_image_available);
        mSupplierName.getText().clear();
        mSupplierEmailId.getText().clear();

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

    private void tryOpeningGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            openGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                }
        }
    }

    private void openGallery() {
        Intent galleryIntent;
        if (Build.VERSION.SDK_INT < 19) {
            galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        galleryIntent.setType(getString(R.string.intent_type));
        startActivityForResult(Intent.createChooser(galleryIntent, getString(R.string.choose_image)), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                Uri mImageUri = intent.getData();
                mProductImage.setImageURI(mImageUri);
                ProductEntry.savedUri = mImageUri;
            }
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

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int noOfRowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (noOfRowsDeleted == 0) {
                Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show();
            }
            ProductEntry.savedUri = null;
            finish();
        }
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
                ProductEntry.savedUri = null;
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

    private void orderProduct() {

        String productName = mProductName.getText().toString().trim();
        String productModelNo = mProductModelNo.getText().toString().trim();
        String supplierName = mSupplierName.getText().toString().trim();
        String supplierEmailId = mSupplierEmailId.getText().toString().trim();

        if (TextUtils.isEmpty(productName)) {
            Toast.makeText(this, R.string.empty_product_name, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(supplierName)) {
            Toast.makeText(this, R.string.empty_supplier_name, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(supplierEmailId)) {
            Toast.makeText(this, R.string.empty_supplier_email_id, Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + supplierEmailId));
            String mailSubject = "Order Request: " + productName + " " + productModelNo;
            intent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);

            String mailBody = "Hello <Mr./Mrs.> " + supplierName + ",\n" +
                    "\nWe need to order: <number> pieces of: " + productName + " " + productModelNo + " for our inventory.\n" +
                    "\nKindly confirm the order and also let us know the estimated time of delivery.\n" +
                    "\nBest Regards," +
                    "\n<Your Name>";
            intent.putExtra(Intent.EXTRA_TEXT, mailBody);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    private void saveProduct() {
        if (!mProductHasChanged) {
            Toast.makeText(this, R.string.no_change_msg, Toast.LENGTH_SHORT).show();
            return;
        }

        String productName = mProductName.getText().toString().trim();
        String productModelNo = mProductModelNo.getText().toString().trim();
        String productPriceString = mProductPrice.getText().toString().trim();
        String productQuantityString = mProductQuantity.getText().toString().trim();
        String supplierName = mSupplierName.getText().toString().trim();
        String supplierEmailId = mSupplierEmailId.getText().toString().trim();

        ContentValues values = new ContentValues();

        if (!TextUtils.isEmpty(productName)) {
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
        } else {
            values.putNull(ProductEntry.COLUMN_PRODUCT_NAME);
        }
        if (!TextUtils.isEmpty(productModelNo)) {
            values.put(ProductEntry.COLUMN_PRODUCT_MODEL_NO, productModelNo);
        }
        if (!TextUtils.isEmpty(productPriceString)) {
            int productPrice = Integer.parseInt(productPriceString);
            values.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        } else {
            values.putNull(ProductEntry.COLUMN_PRODUCT_PRICE);
        }
        if (!TextUtils.isEmpty(productQuantityString)) {
            int productQuantity = Integer.parseInt(productQuantityString);
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        }
        if (ProductEntry.savedUri != null) {
            String productImageUriString = ProductEntry.savedUri.toString();
            values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, productImageUriString);
        }
        if (!TextUtils.isEmpty(supplierName)) {
            values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
        }
        if (!TextUtils.isEmpty(supplierEmailId)) {
            values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, supplierEmailId);
        }

        try {
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
            ProductEntry.savedUri = null;
            finish();
        } catch (IllegalArgumentException i) {
            Toast.makeText(this, i.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}