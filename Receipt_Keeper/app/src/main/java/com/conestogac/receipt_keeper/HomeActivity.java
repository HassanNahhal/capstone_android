package com.conestogac.receipt_keeper;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class HomeActivity extends AppCompatActivity {

    private ListView receiptListView;
    private SQLController dbContoller;

    private SimpleCursorAdapter adapter;
    private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        receiptListView = (ListView) findViewById(R.id.receiptListView);
        readAllDataFromDatabase();


        // [ Go to AddUserActivity when clicked]
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addUserIntent = new Intent(fab.getContext(), AddReceiptActivity.class);
                    startActivity(addUserIntent);
                }
            });

        }
    }

    // [ Retrieve data from database and set to ListView]

    private void readAllDataFromDatabase() {
        dbContoller = new SQLController(this);
        dbContoller.open();

        // [ Cursor that incdulde data]
        cursor = dbContoller.readAllReceipts();

        // [ Get context of user table]
        String[] from = new String[]{DBHelper.RECEIPT_ID, DBHelper.RECEIPT_DATE};

        // [ Bind context to it's view]
        int[] to = new int[]{R.id.idTextView, R.id.dateTextView};

        // [ Loop and add all the data in Cursor to the ListView using Adapter]
        adapter = new SimpleCursorAdapter(
                HomeActivity.this, R.layout.receipt_layout, cursor, from, to, 0) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                cursor.moveToPosition(position);
                final View row = super.getView(position, convertView, parent);
                return row;
            }
        };

        adapter.notifyDataSetChanged();
        receiptListView.setAdapter(adapter);
        receiptListView.invalidateViews();
        dbContoller.close();


    }


}
