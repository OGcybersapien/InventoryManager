package xyz.cybersapien.inventorymanager;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import xyz.cybersapien.inventorymanager.data.StockContract;

/**
 * Created by cybersapien on 20/10/16.
 * This Class defines a custom cursor Adapter for the Items in the Stock Inventory.
 */

public class ItemCursorAdapter extends CursorAdapter {


    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /*flags*/);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //TODO: Find a better way to add images

        /*Get all the views inside the list_item*/
        TextView item_name_view = (TextView) view.findViewById(R.id.item_name_text_view);
        TextView item_price_view = (TextView) view.findViewById(R.id.price_text_view);
        TextView item_quantity_view = (TextView) view.findViewById(R.id.current_quantity_text_view);

        //Find the indices of name, price and quantity in the cursor
        int nameIndex = cursor.getColumnIndex(StockContract.ItemEntry.COLUMN_ITEM_NAME);
        int priceIndex = cursor.getColumnIndex(StockContract.ItemEntry.COLUMN_ITEM_PRICE);
        int quantityIndex = cursor.getColumnIndex(StockContract.ItemEntry.COLUMN_ITEM_QUANTITY);

        //Get values for the fields
        String itemName = cursor.getString(nameIndex);
        Float price = cursor.getFloat(priceIndex);
        Integer quantity = cursor.getInt(quantityIndex);

        //Set values to the respective TextViews
        item_name_view.setText(itemName);
        item_price_view.setText(NumberFormat.getCurrencyInstance().format(price));
        item_quantity_view.setText(NumberFormat.getInstance().format(quantity));

    }
}
