package xyz.cybersapien.inventorymanager;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by cybersapien on 20/10/16.
 */

public class SupplierCursorAdapter extends CursorAdapter {


    public SupplierCursorAdapter(Context context, Cursor c) {
        super(context, c, 0/*flags*/);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
