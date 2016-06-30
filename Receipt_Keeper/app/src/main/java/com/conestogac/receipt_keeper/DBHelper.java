package com.conestogac.receipt_keeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Database name and version
    private static final String DB_NAME = "receipt_keeper.db";
    private static final int DB_VERSION = 1;

    // Logcat tag
    private static final String LOG_DATABASE = "DatabaseHelper";

    // Table receipts and columns
    public static final String TABLE_RECEIPT = "receipt";
    public static final String RECEIPT_ID = "_id";
    public static final String RECEIPT_FK_CUSTOMER_ID = "customer_id";
    public static final String RECEIPT_FK_STORE_ID = "store_id";
    public static final String RECEIPT_FK_CATEGORY_ID = "category_id";
    public static final String RECEIPT_COMMENT = "comment";
    public static final String RECEIPT_DATE = "date";
    public static final String RECEIPT_TOTAL = "total";


    // Table ReceiptTag and columns
    public static final String TABLE_RECEIPT_TAG = "receiptTag";
    public static final String FK_RECEIPT_ID = "receipt_id";
    public static final String FK_TAG_ID = "tag_id";


    // Table Tag and columns
    public static final String TABLE_TAG = "tag";
    public static final String TAG_ID = "id";
    public static final String TAG_NAME = "tag_name";


    // Table Store and columns
    public static final String TABLE_STORE = "store";
    public static final String STORE_ID = "id";
    public static final String STORE_NAME = "store_name";


    // Table Category and columns
    public static final String TABLE_CATEGORY = "category";
    public static final String CATEGORY_ID = "id";
    public static final String CATEGORY_NAME = "category_name";


    // Table StoreGategory and columns
    public static final String TABLE_STORE_CATEGORY = "storeCategory";
    public static final String STORECATEGORY_FK_CATEGORY_ID = "category_id";
    public static final String STORE_CATEGORY_FK_STORE_ID = "store_id";

    // Receipt table create statement
    private static final String CREATE_TABLE_RECEIPT = " CREATE TABLE " + TABLE_RECEIPT + "( "
            + RECEIPT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + RECEIPT_FK_CUSTOMER_ID
            + " INTEGER ," + RECEIPT_FK_STORE_ID + " INTEGER ," + RECEIPT_FK_CATEGORY_ID +
            " INTEGER ," + RECEIPT_COMMENT + " TEXT ," + RECEIPT_DATE + " TEXT ," + RECEIPT_TOTAL + " REAL " + ")";

    // Receipt_Tag table create statement
    private static final String CREATE_TABLE_RECEIPT_TAG = " CREATE TABLE " + TABLE_RECEIPT_TAG + "( "
            + FK_RECEIPT_ID + " INTEGER ," + FK_TAG_ID + " INTEGER " + ")";

    // Tag table create statement
    private static final String CREATE_TABLE_TAG = " CREATE TABLE " + TABLE_TAG + "( " + TAG_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + TAG_NAME + " TEXT " + ")";


    // Store table create statement
    private static final String CREATE_TABLE_STORE = " CREATE TABLE " + TABLE_STORE + "( " + STORE_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + STORE_NAME + " TEXT " + ")";


    // Category table create statement
    private static final String CREATE_TABLE_CATEGORY = " CREATE TABLE " + TABLE_CATEGORY + "( " + CATEGORY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + CATEGORY_NAME + " TEXT " + ")";

    // Category table create statement
    private static final String CREATE_TABLE_STORR_CATEGORY = " CREATE TABLE " + TABLE_STORE_CATEGORY
            + "( " + STORECATEGORY_FK_CATEGORY_ID + " INTEGER ," + STORE_CATEGORY_FK_STORE_ID
            + " INTEGER " + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL(CREATE_TABLE_RECEIPT);
        db.execSQL(CREATE_TABLE_RECEIPT_TAG);
        db.execSQL(CREATE_TABLE_TAG);
        db.execSQL(CREATE_TABLE_STORE);
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_STORR_CATEGORY);

        Log.d(LOG_DATABASE, "CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL(" DROP TABLE IF EXISTS" + TABLE_RECEIPT);
        db.execSQL(" DROP TABLE IF EXISTS" + TABLE_RECEIPT_TAG);
        db.execSQL(" DROP TABLE IF EXISTS" + TABLE_TAG);
        db.execSQL(" DROP TABLE IF EXISTS" + TABLE_STORE);
        db.execSQL(" DROP TABLE IF EXISTS" + TABLE_CATEGORY);
        db.execSQL(" DROP TABLE IF EXISTS" + TABLE_CATEGORY);

        onCreate(db);

    }

}
