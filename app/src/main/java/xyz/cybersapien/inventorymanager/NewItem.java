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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

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
        ArrayList<Supplier> suppliers = new ArrayList<>();
        while (data.moveToNext()){
            int nameIndex = data.getColumnIndex(StockContract.SuppliersEntry.COLUMN_SUPPLIER_NAME);
            int idIndex = data.getColumnIndex(StockContract.SuppliersEntry._ID);
            suppliers.add(new Supplier(data.getString(nameIndex), data.getString(idIndex)));
        }
        SupplierSpinnerAdapter spinnerAdapter = new SupplierSpinnerAdapter(getBaseContext(), suppliers);
        supplierSpinner.setAdapter(spinnerAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.abandon();
        supplierSpinner.setAdapter(null);
    }


    private class Supplier{

        public String name;
        public String id;

        public Supplier(String name, String id) {
            this.id = id;
            this.name = name;
        }
    }

    private class SupplierSpinnerAdapter extends ArrayAdapter<Supplier>{

        public SupplierSpinnerAdapter(Context context, List<Supplier> objects) {
            super(context, android.R.layout.simple_spinner_dropdown_item, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getContext());
            textView.setText(getItem(position).name);
            return textView;
        }


    }
}
