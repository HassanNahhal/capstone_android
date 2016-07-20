package com.conestogac.receipt_keeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.conestogac.receipt_keeper.authenticate.UserProfileActivity;
import com.conestogac.receipt_keeper.helpers.DBHelper;
import com.conestogac.receipt_keeper.ocr.CaptureActivity;
import com.conestogac.receipt_keeper.uploader.TestUploadActivity;

public class Home2Activity extends AppCompatActivity {

    private static final String TAG = Home2Activity.class.getSimpleName();
    private ListView receiptListView;
    private ReceiptCursorAdapter receiptAdapter;
    private SQLController dbController;
    SharedPreferences loginPreferences;
    SharedPreferences.Editor loginPrefsEditor;

    private SimpleCursorAdapter adapter;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dbController = new SQLController(this);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        receiptListView = (ListView) findViewById(R.id.receiptListView);
        receiptListView.setEmptyView(findViewById(R.id.empty_list_item));


        dbController.open();
        final Cursor cursor = dbController.readAllReceipts();
        dbController.close();
        Log.v("readAllReceipts Cursor", DatabaseUtils.dumpCursorToString(cursor));


        dbController.open();
        final Cursor cursor1 = dbController.readAllReceiptTag();
        dbController.close();
        Log.v("readAllReceiptsTags", DatabaseUtils.dumpCursorToString(cursor1));


        receiptListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                //Todo Goto Detail View, Need to define Parcelable interface for sending Extra
                Log.d(TAG, "Goto Detail View!  position: " + position);

                // [Get cursor items based on position in the ListView]
                Cursor cursorItem = (Cursor) receiptAdapter.getItem(position);
                if (cursorItem != null) {
                    cursorItem.moveToFirst();

                    Log.v("Receipt Cursor", DatabaseUtils.dumpCursorToString(cursorItem));
                    int receiptId = cursorItem.getInt(cursorItem.getColumnIndexOrThrow(DBHelper.RECEIPT_ID));
                    Log.d(TAG, "receiptId :" + receiptId);
                    String storeName = cursorItem.getString(cursorItem.getColumnIndex(DBHelper.STORE_NAME));
                    int total = cursorItem.getInt(cursorItem.getColumnIndexOrThrow(DBHelper.RECEIPT_TOTAL));
                    String date = cursorItem.getString(cursorItem.getColumnIndexOrThrow(DBHelper.RECEIPT_DATE));
                    String comment = cursorItem.getString(cursorItem.getColumnIndexOrThrow(DBHelper.RECEIPT_COMMENT));
                    String paymentMethod = cursorItem.getString(cursorItem.getColumnIndexOrThrow(DBHelper.RECEIPT_PAYMENT_METHOD));
                    int categoryId = cursorItem.getInt(cursorItem.getColumnIndexOrThrow(DBHelper.RECEIPT_FK_CATEGORY_ID));
                    int tagId = cursorItem.getInt(cursorItem.getColumnIndexOrThrow(DBHelper.TAG_ID));
                    Log.d(TAG, "tag id :" + tagId);
                    String imagePath = cursorItem.getString(cursorItem.getColumnIndexOrThrow(DBHelper.RECEIPT_URL));

                    Intent goToSingleView = new Intent(receiptListView.getContext(), ViewReceiptActivity.class);
                    goToSingleView.putExtra("receiptId", receiptId);
                    goToSingleView.putExtra("storeName", storeName);
                    goToSingleView.putExtra("total", total);
                    goToSingleView.putExtra("date", date);
                    goToSingleView.putExtra("comment", comment);
                    goToSingleView.putExtra("paymentMethod", paymentMethod);
                    goToSingleView.putExtra("categoryId", categoryId);
                    goToSingleView.putExtra("tagId", tagId);
                    goToSingleView.putExtra("imagePath", imagePath);


                    startActivity(goToSingleView);
                }
/*
                Log.d(TAG, storeName);
                Log.d(TAG, total + "");
                Log.d(TAG, date);
                Log.d(TAG, comment);
                Log.d(TAG, paymentMethod);
                Log.d(TAG, categoryId + "");
                Log.d(TAG, tagId + "");
*/


                // Get the cursor, positioned to the corresponding row in the result set
                //Cursor cursor = (Cursor) listView.getItemAtPosition(position);

            }
        });


        // [ Go to AddReceiptActivity when clicked]
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_circle_black_24dp));

        //fab.setImageResource(R.drawable.ic_add);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent goToOcrIntent = new Intent(fab.getContext(), CaptureActivity.class);
                    startActivity(goToOcrIntent);
                }
            });

        }
    }

    // [ Retrieve data from database and set to ListView]
    private void readAllDataFromDatabase() {
        dbController.open();
        // Database query can be a time consuming task ..
        // so its safe to call database query in another thread
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                //get cursor and load data into adapter
                receiptAdapter = new ReceiptCursorAdapter(Home2Activity.this, dbController.readAllReceipts());

                //set cursor adapter to listview
                receiptListView.setAdapter(receiptAdapter);
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();
        readAllDataFromDatabase();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Ondestory()");

        dbController.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem myMenu = menu.findItem(R.id.action_auto_login);
        myMenu.setChecked(loginPreferences.getBoolean(UserProfileActivity.SHAREDPREF_KEY_AUTOLOGIN, false));

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryFor) {
                // perform query here
                dbController.open();
                final Cursor cursor = dbController.getAllReceiptsWithValue(queryFor);
                dbController.close();
                if (cursor != null) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            //get cursor and load data into adapter
                            receiptAdapter = new ReceiptCursorAdapter(Home2Activity.this, cursor);

                            //set cursor adapter to listview
                            receiptListView.setAdapter(receiptAdapter);
                        }
                    });
                } else {
                    receiptListView.setEmptyView(findViewById(R.id.empty_list_item));
                }


                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                readAllDataFromDatabase();
                return false;

            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_auto_login:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);

                }
                return true;

            case R.id.action_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.action_upload_test:
                startActivity(new Intent(this, TestUploadActivity.class));
                return true;

            case R.id.action_test_ocr:
                Intent ocrIntent = new Intent(this, CaptureActivity.class);
                startActivity(ocrIntent);
                return true;

            case R.id.action_insert_receipt:
                Intent goInsert = new Intent(this, AddReceiptActivity.class);
                startActivity(goInsert);
                return true;


            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
