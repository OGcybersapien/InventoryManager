package xyz.cybersapien.inventorymanager.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by ogcybersapien on 19/10/16.
 * The StockProvider class is the main driving force here.
 * It enables us to use Content providers and work with the databases Efficiently.
 */
public class StockProvider extends ContentProvider {

    /*LOG TAG*/
    private static final String LOG_TAG = StockProvider.class.getName();

    /*Database Helper Object*/
    private StockDbHelper stockDbHelper;

    /*codes for different URI matches from Items table*/
    private static final int ITEMS = 100;
    private static final int ITEMS_ID = 101;

    /*Codes for different URI matches from Suppliers table*/
    private static final int SUPPLIERS = 200;
    private static final int SUPPLIERS_ID = 201;

    //URI matcher object for matching the URIs to appropriate tasks.
    private static final UriMatcher stockUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        stockUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_ITEMS, ITEMS);
        stockUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_ITEMS + "/#", ITEMS_ID);
        stockUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_SUPPLIERS, SUPPLIERS);
        stockUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_SUPPLIERS + "/#", SUPPLIERS_ID);
    }

    @Override
    public boolean onCreate() {
        stockDbHelper = new StockDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
