package xyz.cybersapien.inventorymanager;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import xyz.cybersapien.inventorymanager.data.StockContract;

public class NewItem extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private Spinner supplierSpinner;

    private int SUPPLIER_LOADER = 100;

    private SpinnerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        supplierSpinner = (Spinner) findViewById(R.id.suppliers_spinner);
        getLoaderManager().initLoader(SUPPLIER_LOADER,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projections = new String[] {
                StockContract.SuppliersEntry._ID,
                StockContract.SuppliersEntry.COLUMN_SUPPLIER_NAME
        };

        return  new CursorLoader(this, StockContract.SuppliersEntry.SUPPLIERS_CONTENT_URI, projections,
                null, null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String[] fromCursor = new String[] {StockContract.SuppliersEntry.COLUMN_SUPPLIER_NAME};
        int[] toResource = new int[] {android.R.id.text1};
        SimpleCursorAdapter supplierAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, data,fromCursor, toResource, 0);
        supplierSpinner.setAdapter(supplierAdapter);

        supplierSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        Toast.makeText(NewItem.this, "Id of the Item Selected: " + id, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        //Do Nothing!
                    }
                }
        );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.abandon();
        supplierSpinner.setAdapter(null);
    }

}
