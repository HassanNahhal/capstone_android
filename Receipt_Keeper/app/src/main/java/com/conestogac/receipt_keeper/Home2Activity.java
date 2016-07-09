package com.conestogac.receipt_keeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;

import com.conestogac.receipt_keeper.authenticate.UserProfileActivity;
import com.conestogac.receipt_keeper.helpers.DBHelper;
import com.conestogac.receipt_keeper.uploader.Receipt;
import com.conestogac.receipt_keeper.uploader.TestUploadActivity;

public class Home2Activity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private ListView receiptListView;
    private ReceiptCursorAdapter receiptAdapter;
    private SQLController dbContoller;
    SharedPreferences loginPreferences;
    SharedPreferences.Editor loginPrefsEditor;
    Switch swAutoLogin;

    private SimpleCursorAdapter adapter;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dbContoller = new SQLController(this);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        receiptListView = (ListView) findViewById(R.id.receiptListView);
        receiptListView.setEmptyView(findViewById(R.id.empty_list_item));
        readAllDataFromDatabase();

        receiptListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> listView, View view,
                                       int position, long id) {
                   //Todo Goto Detail View, Need to define Parcelable interface for sending Extra
                   Log.d(TAG, "Goto Detail View!  position: "+position);

                   // Get the cursor, positioned to the corresponding row in the result set
                   Cursor cursor = (Cursor) listView.getItemAtPosition(position);

               }
           });

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
        dbContoller.open();
        // Database query can be a time consuming task ..
        // so its safe to call database query in another thread
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                //get cursor and load data into adapter
                receiptAdapter = new ReceiptCursorAdapter(Home2Activity.this, dbContoller.readAllReceipts());

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

        dbContoller.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem myMenu = menu.findItem(R.id.action_auto_login);
        View actionView = myMenu.getActionView();
        // Get the action view used in your toggleservice item
        swAutoLogin = (Switch) actionView.findViewById(R.id.sw_autoLogin);
        swAutoLogin.setChecked(loginPreferences.getBoolean(UserProfileActivity.SHAREDPREF_KEY_AUTOLOGIN,false));
        swAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loginPrefsEditor.putBoolean(UserProfileActivity.SHAREDPREF_KEY_AUTOLOGIN, isChecked);
                loginPrefsEditor.commit();
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
            case R.id.action_settings:
                startActivity(new Intent(this, TestUploadActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
