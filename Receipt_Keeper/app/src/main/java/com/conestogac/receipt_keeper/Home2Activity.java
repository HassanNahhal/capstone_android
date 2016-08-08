package com.conestogac.receipt_keeper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.conestogac.receipt_keeper.authenticate.UserProfileActivity;
import com.conestogac.receipt_keeper.helpers.BaseActivity;
import com.conestogac.receipt_keeper.helpers.DBHelper;
import com.conestogac.receipt_keeper.ocr.CaptureActivity;
import com.conestogac.receipt_keeper.uploader.ItemUploadTaskFragment;
import com.conestogac.receipt_keeper.webview.WebViewActivity;

public class Home2Activity extends BaseActivity
        implements ItemUploadTaskFragment.TaskCallbacks,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = Home2Activity.class.getSimpleName();
    private ListView receiptListView;
    private TextView homeTotalTextView;
    private ReceiptCursorAdapter receiptAdapter;
    private SQLController dbController;
    SharedPreferences loginPreferences;
    SharedPreferences.Editor loginPrefsEditor;
    int receiptId;
    SharedPreferences filterPreferences;
    public static final String TAG_TASK_FRAGMENT = "ItemUploadTaskFragment";
    private ItemUploadTaskFragment mTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dbController = new SQLController(this);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        filterPreferences = getSharedPreferences(FilterActivity.FILTER_PREF, Context.MODE_PRIVATE);


        homeTotalTextView = (TextView) findViewById(R.id.homeTotalTextView);
        receiptListView = (ListView) findViewById(R.id.receiptListView);
        receiptListView.setEmptyView(findViewById(R.id.empty_list_item));

        FragmentManager fm = getSupportFragmentManager();
        mTaskFragment = (ItemUploadTaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        // create the fragment and data the first time
        if (mTaskFragment == null) {
            // add the fragment
            mTaskFragment = new ItemUploadTaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }

        dbController.open();
        final Cursor cursor = dbController.getAllTags();
        Log.v("getAllTags", DatabaseUtils.dumpCursorToString(cursor));
        dbController.close();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get Drwaer pointer and settup  - listener, toolbar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        dbController.open();
        final Cursor cursor1 = dbController.getAllReceipts();
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
                    int receiptId = cursorItem.getInt(cursorItem.getColumnIndexOrThrow(DBHelper.RECEIPT_ID));
                    String storeName = cursorItem.getString(cursorItem.getColumnIndex(DBHelper.STORE_NAME));
                    float total = cursorItem.getFloat(cursorItem.getColumnIndexOrThrow(DBHelper.RECEIPT_TOTAL));
                    String date = cursorItem.getString(cursorItem.getColumnIndexOrThrow(DBHelper.RECEIPT_DATE));
                    String comment = cursorItem.getString(cursorItem.getColumnIndexOrThrow(DBHelper.RECEIPT_COMMENT));
                    String paymentMethod = cursorItem.getString(cursorItem.getColumnIndexOrThrow(DBHelper.RECEIPT_PAYMENT_METHOD));
                    int categoryId = cursorItem.getInt(cursorItem.getColumnIndexOrThrow(DBHelper.RECEIPT_FK_CATEGORY_ID));
                    String imagePath = cursorItem.getString(cursorItem.getColumnIndexOrThrow(DBHelper.RECEIPT_URL));

                    Intent goToSingleView = new Intent(receiptListView.getContext(), ViewReceiptActivity.class);
                    goToSingleView.putExtra("receiptId", receiptId);
                    goToSingleView.putExtra("storeName", storeName);
                    goToSingleView.putExtra("total", total);
                    goToSingleView.putExtra("date", date);
                    goToSingleView.putExtra("comment", comment);
                    goToSingleView.putExtra("paymentMethod", paymentMethod);
                    goToSingleView.putExtra("categoryId", categoryId);
                    goToSingleView.putExtra("imagePath", imagePath);

                    startActivity(goToSingleView);
                }

            }
        });


        receiptListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                Cursor cursorItem = (Cursor) receiptAdapter.getItem(position);
                if (cursorItem != null) {
                    cursorItem.moveToFirst();
                    receiptId = cursorItem.getInt(cursorItem.getColumnIndexOrThrow(DBHelper.RECEIPT_ID));

                } else {
                }
                // [Alert the user of the action of deletion of an item]
                AlertDialog.Builder adb = new AlertDialog.Builder(Home2Activity.this);
                adb.setTitle("Delete Receipt");
                adb.setMessage("Are you sure you want to Delete?");
                adb.setIcon(android.R.drawable.ic_dialog_alert);
                adb.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dbController.open();
                                dbController.deleteReceipt(receiptId);
                                dbController.close();

                                Toast.makeText(getApplicationContext(),
                                        "Deleted", Toast.LENGTH_SHORT).show();
                                readAllDataFromDatabase();
                            }
                        });

                adb.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Toast.makeText(getApplicationContext(),
                                        "Canceled", Toast.LENGTH_SHORT).show();
                            }
                        });

                adb.show();

                return true;
            }
        });


        //  Go to AddReceiptActivity when clicked
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

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
        final String fromDate = filterPreferences.getString(FilterActivity.FROM_DATE, "");
        final String toDate = filterPreferences.getString(FilterActivity.TO_DATE, "");
        // Database query can be a time consuming task ..
        // so its safe to call database query in another thread
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                //get cursor and load data into adapter
                Cursor cursor = null;
                if (filterPreferences.contains(FilterActivity.FILTER_PREF) &&
                        !(fromDate.equals("") && toDate.equals(""))) {
                    cursor = dbController.getAllReceiptsBetweenDate(fromDate, toDate);
                } else {
                    cursor = dbController.getAllReceipts();

                }

               /* if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();*/
                receiptAdapter = new ReceiptCursorAdapter(Home2Activity.this, cursor);
                String total = String.format("%.2f", getTotalOfReceipts(cursor));
                homeTotalTextView.setText("$ " + total);
                dbController.close();

                //set cursor adapter to listview
                receiptListView.setAdapter(receiptAdapter);

                //}
            }
        });
    }

    private float getTotalOfReceipts(Cursor cursor) {
        float total = 0.0f;
        if (cursor != null && cursor.getCount() > 0)
            do {
                total += cursor.getFloat(cursor.getColumnIndexOrThrow(DBHelper.RECEIPT_TOTAL));
            } while (cursor.moveToNext());
        return total;
    }

    @Override
    protected void onResume() {

        super.onResume();
        readAllDataFromDatabase();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle("Exit the receipt keeper")
                    .setMessage(getString(R.string.message_to_confirm_exit))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Ondestory()");

        dbController.close();
        super.onDestroy();
    }


    /**
     * For selecting each item at drawer, proper fragement will be called
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Todo Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent goWebView;
        switch (id) {
            case R.id.nav_dashboard:
                goWebView = new Intent(this, WebViewActivity.class);
                goWebView.putExtra(WebViewActivity.EXTRA_URL, "http://receipt-keeper.herokuapp.com/#/Dashboard");
                startActivity(goWebView);
                break;

            case R.id.nav_chart:
                goWebView = new Intent(this, WebViewActivity.class);
                goWebView.putExtra(WebViewActivity.EXTRA_URL, "http://receipt-keeper.herokuapp.com/#/chart");
                startActivity(goWebView);
                break;

            case R.id.nav_about:
                goWebView = new Intent(this, WebViewActivity.class);
                goWebView.putExtra(WebViewActivity.EXTRA_URL, "http://receipt-keeper.herokuapp.com");
                startActivity(goWebView);
                break;
            default:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem myMenu = menu.findItem(R.id.action_auto_login);
        myMenu.setChecked(loginPreferences.getBoolean(UserProfileActivity.SHAREDPREF_KEY_AUTOLOGIN, false));

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryFor) {
                // perform query here
                dbController.open();
                final Cursor cursor = dbController.getAllReceiptsWithValue(queryFor);
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

    public void viewReceiptImageButton(View view) {

        int index;
        String imagePath;
        ViewGroup parent = (ViewGroup) view.getParent();

        //Get index of checked task to delete
        Log.d(TAG, "Checked _ID: " + parent.getChildAt(2).getTag());
        index = (Integer) parent.getChildAt(2).getTag();

        Intent popIntent = new Intent(Home2Activity.this, Pop.class);
        imagePath = dbController.getUrlReceipt(index);

        if (imagePath != null) {
            popIntent.putExtra("imagePath", imagePath);
            popIntent.putExtra("POP_INFO", "Receit ID: " + String.valueOf(index));
            startActivity(popIntent);
        }
    }

    /*
        After uploading, this method will be called
     */
    @Override
    public void onItemUploaded(final String error) {
        dismissProgressDialog();
        Home2Activity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog();
                Toast.makeText(Home2Activity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
        readAllDataFromDatabase();
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
                    loginPrefsEditor.putBoolean(UserProfileActivity.SHAREDPREF_KEY_AUTOLOGIN, false);
                } else {
                    item.setChecked(true);
                    loginPrefsEditor.putBoolean(UserProfileActivity.SHAREDPREF_KEY_AUTOLOGIN, true);
                }
                loginPrefsEditor.commit();
                return true;

            case R.id.action_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.action_filter:
                Intent goToFilter = new Intent(this, FilterActivity.class);
                startActivity(goToFilter);
                return true;

            case R.id.action_sync_now:
                if (dbController.numberOfItemsToSync() == 0) {
                    Toast.makeText(Home2Activity.this, getString(R.string.nothing_to_save_message), Toast.LENGTH_SHORT).show();
                } else {
                    showProgressDialog(getString(R.string.upload_progress_message));
                    mTaskFragment.uploadItem();
                }
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
