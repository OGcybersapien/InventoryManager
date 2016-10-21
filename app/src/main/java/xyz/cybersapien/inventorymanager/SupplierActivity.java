package xyz.cybersapien.inventorymanager;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import xyz.cybersapien.inventorymanager.data.StockContract;

public class SupplierActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = SupplierActivity.class.getName();

    private static final int STOCK_LOADER = 0;

    //CursorAdapter for the Suppliers
    private SupplierCursorAdapter customCursorAdapter;

    //ListView for the Adapter to display data
    private ListView itemsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        //fab for creating new Supplier
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Implement add new supplier intent
            }
        });

        setTitle("Suppliers");
        itemsListView = (ListView) findViewById(R.id.main_list);
        customCursorAdapter = new SupplierCursorAdapter(this, null);
        itemsListView.setAdapter(customCursorAdapter);

        //start the Loader
        getLoaderManager().initLoader(STOCK_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem changeActivityMenuOption = menu.findItem(R.id.action_menu_list_toggle);
        changeActivityMenuOption.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_data_insert_dummy_data:
                insertSuppliers();
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(SupplierActivity.this);
                break;
        }
        return true;
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
        String[] projection = {
                StockContract.SuppliersEntry._ID,
                StockContract.SuppliersEntry.COLUMN_SUPPLIER_NAME,
                StockContract.SuppliersEntry.COLUMN_SUPPLIER_PHONE,
                StockContract.SuppliersEntry.COLUMN_SUPPLIER_EMAIL
        };
        return new CursorLoader(this, StockContract.SuppliersEntry.SUPPLIERS_CONTENT_URI,
                projection, null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        customCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        customCursorAdapter.swapCursor(null);
    }
}
