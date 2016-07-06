package com.conestogac.receipt_keeper;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.conestogac.receipt_keeper.helpers.DBHelper;
import com.conestogac.receipt_keeper.uploader.TestUploadActivity;


public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private ListView receiptListView;
    private SQLController dbContoller;

    private SimpleCursorAdapter adapter;
    private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dbContoller = new SQLController(this);

        receiptListView = (ListView) findViewById(R.id.receiptListView);
        readAllDataFromDatabase();


        /*dbContoller.open();
        Log.d("here", "here");
        Cursor cursor = dbContoller.readAllTags();
        Log.d("here", "here");

        if (cursor != null) {
            while (cursor.moveToFirst()) {
                Log.d("here", "here");

                Log.d("id cursor", cursor.getColumnName(0));
            }
        }
        dbContoller.close();*/

        // [ Go to AddReceiptActivity when clicked]
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

        // [ Cursor that include data]
        //LinkedList<Receipt> receipts = dbContoller.readAllReceipts();
        cursor = dbContoller.readAllReceipts();
        dbContoller.close();
        SimpleCursorAdapter adapter;

        // [ Get context of user table]
        String[] from = new String[]{DBHelper.RECEIPT_ID, DBHelper.RECEIPT_DATE, DBHelper.RECEIPT_TOTAL,
                DBHelper.TAG_NAME};

        // [ Bind context to it's view]
        int[] to = new int[]{R.id.idTextView, R.id.dateTextView, R.id.totalTextView,R.id.tagsTextView};

        /*adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                receipts);

        receiptListView.setAdapter(adapter);*/

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, TestUploadActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
