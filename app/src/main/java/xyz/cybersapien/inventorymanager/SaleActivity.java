package xyz.cybersapien.inventorymanager;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import xyz.cybersapien.inventorymanager.data.StockContract;

public class SaleActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /*Spinner for selecting the supplier*/
    private Spinner itemsSpinner;

    /*List of Items*/
    private ArrayList<Long> itemsList;

    /*ListView to display Items*/
    private ListView itemsView;

    /*currently selected item's ID*/
    private Long currentSelected;

    private Cursor itemsCursor;
    private ItemCursorAdapter saleItemsAdapter;

    private static final int ITEM_LOADER = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        itemsList = new ArrayList<>();
        itemsView = (ListView) findViewById(R.id.sale_items_list);

        saleItemsAdapter = new ItemCursorAdapter(this, itemsCursor);
        itemsView.setAdapter(saleItemsAdapter);

        //Set up spinner
        itemsSpinner = (Spinner) findViewById(R.id.items_spinner);
        getLoaderManager().initLoader(ITEM_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projections = new String[] {
                StockContract.ItemEntry._ID,
                StockContract.ItemEntry.COLUMN_ITEM_NAME,
                StockContract.ItemEntry.COLUMN_ITEM_QUANTITY
        };
        return new CursorLoader(this, StockContract.ItemEntry.ITEMS_CONTENT_URI, projections, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
        String[] fromCursor = new String[] {StockContract.ItemEntry.COLUMN_ITEM_NAME};
        int[] toResource = new int[] {android.R.id.text1};
        itemsCursor = cursor;
        SimpleCursorAdapter itemsAdapter = new SimpleCursorAdapter(
                this, android.R.layout.simple_spinner_item,
                cursor, fromCursor, toResource, 0);
        itemsSpinner.setAdapter(itemsAdapter);
        itemsSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        currentSelected = l;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        currentSelected = 1L;
                    }
                }
        );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        itemsSpinner.setAdapter(null);
    }

    public void addItem(View v){
        itemsList.add(currentSelected);

    }
}
