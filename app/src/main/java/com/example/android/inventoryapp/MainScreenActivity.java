package com.example.android.inventoryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainScreenActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LIST_LOADER = 1;   //Constant for initLoader method
    ProductCursorAdapter mCursorAdapter;    //Adapter for the listView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);  //Displays the layout defined by the xml file
        setTitle(R.string.main_screen_title);   //Sets the activity title

        ListView productListView = findViewById(R.id.list_view);    //Finds the listView to be populated with products data

        /*
         Finds and sets the empty view on the listView
         */
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        /*
         Initialize and attach the adapter to the listView
         */
        mCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

        /*
         Setup the list items to open the ItemDetailActivity
         */
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainScreenActivity.this, ItemDetailActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        /*
        Setup the floating action button to open the ItemDetailActivity
         */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreenActivity.this, ItemDetailActivity.class);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(PRODUCT_LIST_LOADER, null, this);  //Starts the loader
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        /*
        Make a query to the database to get the cursor with columns as defined int the projection
         */
        String[] projection = {ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_MODEL_NO,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_IMAGE};

        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);    //change the value of adapter to the new cursor

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);    //sets null cursor value to the adapter

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);   //Creates menu options on main screen
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Gets the selected menu item id
        switch (item.getItemId()) {
            case R.id.action_delete_all_products:
                showDeleteConfirmationDialog(); //calls this function if delete all option is selected
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        /*
        Sets up the Dialog box for Delete all confirmation
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_message);
        builder.setPositiveButton(R.string.delete_all_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAllProducts();
            }
        });
        builder.setNegativeButton(R.string.delete_all_negative, new DialogInterface.OnClickListener() {
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

    public void deleteAllProducts() {
        int noOfRowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);    //Request to the content provider to delete all products
        if (noOfRowsDeleted == 0) {
            Toast.makeText(this, R.string.delete_all_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.delete_all_success, Toast.LENGTH_SHORT).show();
        }

    }
}