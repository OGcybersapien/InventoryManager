package xyz.cybersapien.inventorymanager;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Locale;

import xyz.cybersapien.inventorymanager.data.StockContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = MainActivity.class.getName();

    private static final int STOCK_LOADER = 0;

    //CursorAdapter for the Items and Suppliers
    private CursorAdapter customCursorAdapter;

    //ListView for the Adapter to display data
    private ListView itemsListView;

    private static final int ITEMS_LIST_ID = 100;
    private static final int SUPPLIERS_LIST_ID = 200;
    //Current List
    private int list_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list_ID = SUPPLIERS_LIST_ID;

        //FAB to open sales activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Implement onClick method.
                switch (list_ID){
                    case ITEMS_LIST_ID:
                        Toast.makeText(MainActivity.this, "ITEMS", Toast.LENGTH_SHORT).show();
                        break;
                    case SUPPLIERS_LIST_ID:
                        Toast.makeText(MainActivity.this, "SUPPLIERS", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.floatingActionButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list_ID == ITEMS_LIST_ID)
                    list_ID = SUPPLIERS_LIST_ID;
                else
                    list_ID = ITEMS_LIST_ID;
            }
        });


        //get the empty ListView
        itemsListView = (ListView) findViewById(R.id.main_list);
        customCursorAdapter = new ItemCursorAdapter(this, null);
        itemsListView.setAdapter(customCursorAdapter);

        //Start the Loader
        getLoaderManager().initLoader(STOCK_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        switch (list_ID){
            case ITEMS_LIST_ID:
                menu.findItem(R.id.action_menu_list_toggle).setTitle("Open Suppliers List");
                break;
            case SUPPLIERS_LIST_ID:
                menu.findItem(R.id.action_menu_list_toggle).setTitle("Open Items List");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_menu_list_toggle:
                if (list_ID == ITEMS_LIST_ID){
                    customCursorAdapter = new SupplierCursorAdapter(this, null);
                    item.setTitle("Open Items List");
                    list_ID = SUPPLIERS_LIST_ID;
                    getLoaderManager().restartLoader(STOCK_LOADER, null,this);
                } else {
                    customCursorAdapter = new ItemCursorAdapter(this, null);
                    item.setTitle("Open Suppliers List");
                    list_ID = ITEMS_LIST_ID;
                    getLoaderManager().restartLoader(STOCK_LOADER, null,this);
                }
                break;
            case R.id.action_data_insert_dummy_data:
                switch (list_ID){
                    case ITEMS_LIST_ID:
                        insertItems();
                        break;
                    case SUPPLIERS_LIST_ID:
                        insertSuppliers();
                        break;
                }
                break;
        }
        return true;
    }

    private void insertItems(){
        ContentValues values = new ContentValues();
        values.put(StockContract.ItemEntry.COLUMN_ITEM_NAME, "Item");
        values.put(StockContract.ItemEntry.COLUMN_ITEM_PRICE, 25.5);
        values.put(StockContract.ItemEntry.COLUMN_ITEM_SUPPLIER_ID, 2);
        Uri itemsUri = StockContract.ItemEntry.ITEMS_CONTENT_URI;
        getContentResolver().insert(itemsUri, values);
    }

    private void insertSuppliers(){
        ContentValues values = new ContentValues();
        values.put(StockContract.SuppliersEntry.COLUMN_SUPPLIER_NAME, "Charlie Harper");
        values.put(StockContract.SuppliersEntry.COLUMN_SUPPLIER_PHONE, "7696497298");
        values.put(StockContract.SuppliersEntry.COLUMN_SUPPLIER_EMAIL, "aditya@cybersapien.xyz");
        Uri suppliersUri = StockContract.SuppliersEntry.SUPPLIERS_CONTENT_URI;
        getContentResolver().insert(suppliersUri, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (list_ID){
            case ITEMS_LIST_ID:
                return getItems();
            case SUPPLIERS_LIST_ID:
                return getSuppliers();
            default:
                throw new NullPointerException("Error!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        customCursorAdapter.swapCursor(data);
        itemsListView.setAdapter(customCursorAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        customCursorAdapter.swapCursor(null);
    }

    private CursorLoader getItems(){
        String[] projection = {
                StockContract.ItemEntry._ID,
                StockContract.ItemEntry.COLUMN_ITEM_NAME,
                StockContract.ItemEntry.COLUMN_ITEM_QUANTITY,
                StockContract.ItemEntry.COLUMN_ITEM_PRICE
        };

        return new CursorLoader(this, StockContract.ItemEntry.ITEMS_CONTENT_URI,
                projection, null, null, null);
    }

    private CursorLoader getSuppliers(){
        String[] projection = {
                StockContract.SuppliersEntry._ID,
                StockContract.SuppliersEntry.COLUMN_SUPPLIER_NAME,
                StockContract.SuppliersEntry.COLUMN_SUPPLIER_PHONE,
                StockContract.SuppliersEntry.COLUMN_SUPPLIER_EMAIL
        };

        return new CursorLoader(this, StockContract.SuppliersEntry.SUPPLIERS_CONTENT_URI,
                projection, null,null,null);
    }
}
