package xyz.cybersapien.inventorymanager;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import xyz.cybersapien.inventorymanager.data.StockContract;

public class ItemListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = ItemListActivity.class.getName();

    private static final int STOCK_LOADER = 0;

    //CursorAdapter for the Items and Suppliers
    private ItemCursorAdapter customCursorAdapter;

    //ListView for the Adapter to display data
    private ListView itemsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        //FAB to open sales activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ItemEditorActivity.class);
                startActivity(intent);
            }
        });

        setTitle("Items in Stock");
        //get the empty ListView
        itemsListView = (ListView) findViewById(R.id.main_list);
        TextView hintView = (TextView) findViewById(R.id.empty_list_hint);
        hintView.setText("Nothing to show.\nStart by adding a supplier and then add Items.");
        itemsListView.setEmptyView(hintView);
        customCursorAdapter = new ItemCursorAdapter(this, null);
        itemsListView.setAdapter(customCursorAdapter);

        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri itemUri = ContentUris.withAppendedId(StockContract.ItemEntry.ITEMS_CONTENT_URI,id);
                Intent intent = new Intent(getBaseContext(), ItemEditorActivity.class);
                intent.setData(itemUri);
                startActivity(intent);
            }
        });

        //Start the Loader
        getLoaderManager().initLoader(STOCK_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        menu.findItem(R.id.action_menu_list_toggle).setTitle("Supplier List");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_data_insert_dummy_data:
                insertItems();
                break;
            case R.id.action_menu_list_toggle:
                Intent goToSupplier = new Intent(this,SupplierListActivity.class);
                startActivity(goToSupplier);
                break;
            case R.id.delete_all:
                getContentResolver().delete(StockContract.ItemEntry.ITEMS_CONTENT_URI, null,null);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void insertItems(){
        ContentValues values = new ContentValues();
        values.put(StockContract.ItemEntry.COLUMN_ITEM_NAME, "Item");
        values.put(StockContract.ItemEntry.COLUMN_ITEM_PRICE, 25.5);
        values.put(StockContract.ItemEntry.COLUMN_ITEM_SUPPLIER_ID, 1);
        Uri itemsUri = StockContract.ItemEntry.ITEMS_CONTENT_URI;
        getContentResolver().insert(itemsUri, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                StockContract.ItemEntry._ID,
                StockContract.ItemEntry.COLUMN_ITEM_NAME,
                StockContract.ItemEntry.COLUMN_ITEM_QUANTITY,
                StockContract.ItemEntry.COLUMN_ITEM_PRICE,
                StockContract.ItemEntry.COLUMN_ITEM_PICTURE
        };

        return new CursorLoader(this, StockContract.ItemEntry.ITEMS_CONTENT_URI,
                projection, null, null, null);
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
