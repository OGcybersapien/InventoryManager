package xyz.cybersapien.inventorymanager;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import xyz.cybersapien.inventorymanager.data.StockContract;

public class ItemEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private String LOG_TAG = ItemEditorActivity.class.getName();

    /*Spinner for selecting the Supplier*/
    private Spinner supplierSpinner;

    /*Name Text Field*/
    private EditText nameEditText;

    /*Quantity Text Field*/
    private EditText quantityEditText;

    /*Price Text Field*/
    private EditText priceEditText;

    /*Supplier ID*/
    private long supplierID;

    /*Boolean to see if this is a new Item entry or to update an existing one*/
    private boolean newItem;

    /*Uri for the data in case this instance is to update an entry*/
    private Uri data;

    private static final int ITEM_LOADER = 100;
    private static final int SUPPLIER_LOADER = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_editor);

        data = getIntent().getData();
        newItem = data==null;

        //Initialize Spinner
        supplierSpinner = (Spinner) findViewById(R.id.suppliers_spinner);
        getLoaderManager().initLoader(SUPPLIER_LOADER,null,this);

        //Initialize the TextFields
        nameEditText = (EditText) findViewById(R.id.item_name_edit_text);
        quantityEditText = (EditText) findViewById(R.id.item_quantity_edit_text);
        priceEditText = (EditText) findViewById(R.id.item_price_edit_text);

        Button submitButton = (Button) findViewById(R.id.submit_button);

        if (!newItem){
            getLoaderManager().initLoader(ITEM_LOADER,null,this);
            submitButton.setText("Update");
        } else {
            submitButton.setText("Add Item");
        }

        Log.d(LOG_TAG, "In OnCreate");

        findViewById(R.id.order_button).setVisibility(View.GONE);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newItem){
                    ContentValues values = new ContentValues();
                    values.put(StockContract.ItemEntry.COLUMN_ITEM_NAME, nameEditText.getText().toString());
                    values.put(StockContract.ItemEntry.COLUMN_ITEM_PRICE, Double.parseDouble(priceEditText.getText().toString()));
                    values.put(StockContract.ItemEntry.COLUMN_ITEM_QUANTITY, Integer.parseInt(quantityEditText.getText().toString()));
                    values.put(StockContract.ItemEntry.COLUMN_ITEM_SUPPLIER_ID, supplierID);
                    getContentResolver().insert(StockContract.ItemEntry.ITEMS_CONTENT_URI, values);
                    finish();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(StockContract.ItemEntry.COLUMN_ITEM_NAME, nameEditText.getText().toString());
                    values.put(StockContract.ItemEntry.COLUMN_ITEM_PRICE, Double.parseDouble(priceEditText.getText().toString()));
                    values.put(StockContract.ItemEntry.COLUMN_ITEM_QUANTITY, Integer.parseInt(quantityEditText.getText().toString()));

                    String where = StockContract.ItemEntry._ID + "=?";
                    String[] whereArgs = new String[] {String.valueOf(ContentUris.parseId(data))};
                    getContentResolver().update(StockContract.ItemEntry.ITEMS_CONTENT_URI, values, where, whereArgs);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editors, menu);
        if (newItem){
            menu.findItem(R.id.action_delete_this).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_delete_this:
                String where = StockContract.ItemEntry._ID + "=?";
                String[] selectionArgs = new String[] {String.valueOf(ContentUris.parseId(data))};
                getContentResolver().delete(StockContract.ItemEntry.ITEMS_CONTENT_URI, where,selectionArgs);
                finish();
                break;
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case SUPPLIER_LOADER:
                String[] supplierProjections = new String[] {
                        StockContract.SuppliersEntry._ID,
                        StockContract.SuppliersEntry.COLUMN_SUPPLIER_NAME,
                        StockContract.SuppliersEntry.COLUMN_SUPPLIER_EMAIL,
                        StockContract.SuppliersEntry.COLUMN_SUPPLIER_PHONE
                };
                Log.d(LOG_TAG, "In Supplier Loader onCreateLoader");
                return  new CursorLoader(this, StockContract.SuppliersEntry.SUPPLIERS_CONTENT_URI, supplierProjections,
                        null, null,null);

            case ITEM_LOADER:
                String[] itemProjections = new String[] {
                        StockContract.ItemEntry._ID,
                        StockContract.ItemEntry.COLUMN_ITEM_NAME,
                        StockContract.ItemEntry.COLUMN_ITEM_PRICE,
                        StockContract.ItemEntry.COLUMN_ITEM_QUANTITY,
                        StockContract.ItemEntry.COLUMN_ITEM_SUPPLIER_ID
                };
                Log.d(LOG_TAG, "In Item Loader onCreateLoader");
                String selection = StockContract.ItemEntry._ID + "=?";
                String[] selectionArgs = new String[] {String.valueOf(ContentUris.parseId(data))};
                return new CursorLoader(this, StockContract.ItemEntry.ITEMS_CONTENT_URI, itemProjections, selection, selectionArgs, null);
            default:
                throw new IllegalArgumentException("Error! Loader ID not found");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()){
            case SUPPLIER_LOADER:
                setupSpinner(cursor);
                setupSupplierData(cursor);
                break;
            case ITEM_LOADER:
                setupFields(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.abandon();
        supplierSpinner.setAdapter(null);
    }

    private void setupSpinner(final Cursor supplierCursor){
        String[] fromCursor = new String[] {StockContract.SuppliersEntry.COLUMN_SUPPLIER_NAME};
        int[] toResource = new int[] {android.R.id.text1};
        SimpleCursorAdapter supplierAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, supplierCursor,fromCursor, toResource, 0);
        supplierSpinner.setAdapter(supplierAdapter);

        supplierSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        supplierID = id;
                        setupSupplierData(supplierCursor);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        //Do Nothing!
                    }
                }
        );
    }

    private void setupSupplierData(Cursor cursor){
        if(cursor.getCount()==0){
            return;
        }else {
            int emailIndex = cursor.getColumnIndex(StockContract.SuppliersEntry.COLUMN_SUPPLIER_EMAIL);
            int phoneIndex = cursor.getColumnIndex(StockContract.SuppliersEntry.COLUMN_SUPPLIER_PHONE);

            String email = cursor.getString(emailIndex);
            String phone = cursor.getString(phoneIndex);
            Intent intent;
            if (!newItem){
                if (!TextUtils.isEmpty(email)){
                    intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + email.trim()));
                    initOrderButton(intent);
                } else if (!TextUtils.isEmpty(phone)){
                    intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.trim()));
                    initOrderButton(intent);
                } else {
                    Toast.makeText(this, "No Contact info found!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void setupFields(Cursor cursor) {
        if (cursor.moveToFirst()){
            String name = cursor.getString(cursor.getColumnIndex(StockContract.ItemEntry.COLUMN_ITEM_NAME));
            String quantity = cursor.getString(cursor.getColumnIndex(StockContract.ItemEntry.COLUMN_ITEM_QUANTITY));
            String price = cursor.getString(cursor.getColumnIndex(StockContract.ItemEntry.COLUMN_ITEM_PRICE));
            Integer supplierID = cursor.getInt(cursor.getColumnIndex(StockContract.ItemEntry.COLUMN_ITEM_SUPPLIER_ID));
            supplierSpinner.setSelection(supplierID);
            nameEditText.setText(name);
            quantityEditText.setText(quantity);
            priceEditText.setText(price);
        } else {
            Log.e(LOG_TAG, "setupFields: " + cursor.toString());
        }
    }

    private void initOrderButton(final Intent intent) {
        Button orderButton = (Button) findViewById(R.id.order_button);
        orderButton.setVisibility(View.VISIBLE);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
    }
}
