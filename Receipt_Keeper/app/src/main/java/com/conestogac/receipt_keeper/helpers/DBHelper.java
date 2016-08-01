package com.conestogac.receipt_keeper.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.conestogac.receipt_keeper.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DBHelper extends SQLiteOpenHelper {


    private Context context;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    // Database name and version
    private static final String DB_NAME = "receipt_keeper.db";
    private static final int DB_VERSION = 1;

    // Logcat tag
    private static final String LOG_NAME = "DBHelper";

    // Table receipts and columns
    public static final String TABLE_RECEIPT = "receipt";
    public static final String RECEIPT_ID = "_id";
    public static final String RECEIPT_FK_CUSTOMER_ID = "customer_id";
    public static final String RECEIPT_FK_STORE_ID = "store_id";
    public static final String RECEIPT_FK_CATEGORY_ID = "category_id";
    public static final String RECEIPT_COMMENT = "comment";
    public static final String RECEIPT_DATE = "date";
    public static final String RECEIPT_CREATEDATE = "createDate";
    public static final String RECEIPT_TOTAL = "total";
    public static final String RECEIPT_URL = "url";
    public static final String RECEIPT_PAYMENT_METHOD = "payment_method";
    public static final String RECEIPT_REMOTE_URL= "remote_url";
    public static final String RECEIPT_REMOTE_ID = "remote_id";
    public static final String RECEIPT_IS_SYNCED = "isSynced";


    // Table ReceiptTag and columns
    public static final String TABLE_RECEIPT_TAG = "receiptTag";
    public static final String RECEIPT_TAG_FK_RECEIPT_ID = "receipt_id";
    public static final String RECEIPT_TAG_FK_TAG_ID = "tag_id";
    public static final String RECEIPT_TAG_REMOTE_ID = "remote_id";
    public static final String RECEIPT_TAG_IS_SYNCED = "isSynced";


    // Table Tag and columns
    public static final String TABLE_TAG = "tag";
    public static final String TAG_ID = "tag_id";
    public static final String TAG_NAME = "tag_name";
    public static final String TAG_REMOTE_ID = "remote_id";
    public static final String TAG_IS_SYNCED = "isSynced";

    // Table Store and columns
    public static final String TABLE_STORE = "store";
    public static final String STORE_ID = "id";
    public static final String STORE_NAME = "store_name";
    public static final String STORE_REMOTE_ID = "remote_id";
    public static final String STORE_IS_SYNCED = "isSynced";


    // Table Category and columns
    public static final String TABLE_CATEGORY = "category";
    public static final String CATEGORY_ID = "id";
    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_REMOTE_ID = "remote_id";
    public static final String CATEGORY_IS_SYNCED = "isSynced";

    // Table StoreCategory and columns
    public static final String TABLE_STORE_CATEGORY = "storeCategory";
    public static final String STORE_CATEGORY_FK_CATEGORY_ID = "category_id";
    public static final String STORE_CATEGORY_FK_STORE_ID = "store_id";
    public static final String STORE_CATEGORY_REMOTE_ID = "remote_id";
    public static final String STORE_CATEGORY_IS_SYNCED = "isSynced";

    // Receipt table create statement
    private static final String CREATE_TABLE_RECEIPT = " CREATE TABLE " + TABLE_RECEIPT
            + "( "
            + RECEIPT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + RECEIPT_FK_CUSTOMER_ID + " INTEGER ,"
            + RECEIPT_FK_STORE_ID + " INTEGER ,"
            + RECEIPT_FK_CATEGORY_ID + " INTEGER DEFAULT 1,"
            + RECEIPT_COMMENT + " TEXT ,"
            + RECEIPT_DATE + " TEXT ," + RECEIPT_TOTAL + " REAL, "
            + RECEIPT_URL + " TEXT," + RECEIPT_CREATEDATE + " TEXT ,"
            + RECEIPT_PAYMENT_METHOD + " TEXT DEFAULT \'CASH\',"
            + RECEIPT_REMOTE_ID + " TEXT, "
            + RECEIPT_IS_SYNCED + " INTEGER DEFAULT 0, "
            + RECEIPT_REMOTE_URL + " TEXT"
            + ")";

    // Receipt_Tag table create statement
    private static final String CREATE_TABLE_RECEIPT_TAG = " CREATE TABLE " + TABLE_RECEIPT_TAG
                    + "( "
                    + RECEIPT_TAG_FK_RECEIPT_ID + " INTEGER ,"
                    + RECEIPT_TAG_FK_TAG_ID + " INTEGER ,"
                    + RECEIPT_TAG_REMOTE_ID + " TEXT, "
                    + RECEIPT_TAG_IS_SYNCED + " INTEGER DEFAULT 0"
                    + ")";

    // Tag table create statement
    private static final String CREATE_TABLE_TAG = " CREATE TABLE " + TABLE_TAG
                    + "( "
                    + TAG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + TAG_NAME + " TEXT ,"
                    + TAG_REMOTE_ID + " TEXT,"
                    + TAG_IS_SYNCED + " INTEGER DEFAULT 0"
                    + ")";


    // Store table create statement
    private static final String CREATE_TABLE_STORE = " CREATE TABLE " + TABLE_STORE
                    + "( "
                    + STORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + STORE_NAME + " TEXT ,"
                    + STORE_REMOTE_ID + " TEXT,"
                    + STORE_IS_SYNCED + " INTEGER DEFAULT 0"
                    + ")";


    // Category table create statement
    private static final String CREATE_TABLE_CATEGORY = " CREATE TABLE " + TABLE_CATEGORY
                    + "( "
                    + CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + CATEGORY_NAME + " TEXT, "
                    + CATEGORY_REMOTE_ID + " TEXT, "
                    + CATEGORY_IS_SYNCED + " INTEGER DEFAULT 0"
                    + ")";

    // Category table create statement
    private static final String CREATE_TABLE_STORE_CATEGORY = " CREATE TABLE " + TABLE_STORE_CATEGORY
                    + "( "
                    + STORE_CATEGORY_FK_CATEGORY_ID + " INTEGER ,"
                    + STORE_CATEGORY_FK_STORE_ID + " INTEGER ,"
                    + STORE_CATEGORY_REMOTE_ID + " TEXT,"
                    + STORE_CATEGORY_IS_SYNCED + " INTEGER DEFAULT 0"
                    + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL(CREATE_TABLE_RECEIPT);
        db.execSQL(CREATE_TABLE_RECEIPT_TAG);
        db.execSQL(CREATE_TABLE_TAG);
        db.execSQL(CREATE_TABLE_STORE);
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_STORE_CATEGORY);
        this.insertTagInDB(db);
        this.insertCategoryInDB(db);
        this.insertStoreInDB(db);
        this.insertStoreCategoryInDB(db);

        Log.d(LOG_NAME, "Database CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL(" DROP TABLE IF EXISTS" + TABLE_RECEIPT);
        db.execSQL(" DROP TABLE IF EXISTS" + TABLE_RECEIPT_TAG);
        db.execSQL(" DROP TABLE IF EXISTS" + TABLE_TAG);
        db.execSQL(" DROP TABLE IF EXISTS" + TABLE_STORE);
        db.execSQL(" DROP TABLE IF EXISTS" + TABLE_CATEGORY);
        db.execSQL(" DROP TABLE IF EXISTS" + TABLE_STORE_CATEGORY);

        onCreate(db);

    }


    private void insertTagInDB(SQLiteDatabase db) {

        XmlResourceParser xmlParser = context.getResources().getXml(R.xml.tags);
        String tagName;
        boolean flag;
        //database

        try {
            while (xmlParser.next() != XmlPullParser.END_DOCUMENT) {
                if (xmlParser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = xmlParser.getName();
                if (name.equals("tags")) {
                    while (xmlParser.next() != XmlPullParser.END_DOCUMENT) {
                        if (xmlParser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        tagName = readText(xmlParser);
                        ContentValues values = new ContentValues();
                        values.put(DBHelper.TAG_NAME, tagName);
                        long id = db.insert(TABLE_TAG, null, values);
                        Log.d(LOG_NAME, "tag id" + id + "\n" + tagName);
                    }
                    
                }

            }

            flag = true;
        } catch (XmlPullParserException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            flag = false;
        }
        //return flag;
    }


    private void insertCategoryInDB(SQLiteDatabase db) {

        XmlResourceParser xmlParser = context.getResources().getXml(R.xml.categories);
        String categoryName;
        boolean flag;
        //database

        try {
            while (xmlParser.next() != XmlPullParser.END_DOCUMENT) {
                if (xmlParser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = xmlParser.getName();
                if (name.equals("categories")) {
                    while (xmlParser.next() != XmlPullParser.END_DOCUMENT) {
                        if (xmlParser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        categoryName = readText(xmlParser);
                        if (!Objects.equals(categoryName, "Select Category")) {
                            ContentValues values = new ContentValues();
                            values.put(DBHelper.CATEGORY_NAME, categoryName);
                            long id = db.insert(TABLE_CATEGORY, null, values);
                            Log.d(LOG_NAME, "category id" + id + "\n" + categoryName);
                        }
                    }

                }

            }

            flag = true;
        } catch (XmlPullParserException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            flag = false;
        }
        //return flag;
    }

    private void insertStoreInDB(SQLiteDatabase db) {

        List<String> storeCollection = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.storename)));

        ContentValues values = new ContentValues();

        for (int index=0; index < storeCollection.size(); index++) {
            values.put(DBHelper.STORE_NAME, storeCollection.get(index));
            db.insert(TABLE_STORE, null, values);
        }

    }

    //for testing
    //todo managing list at one place
    private void insertStoreCategoryInDB(SQLiteDatabase db) {
        List<String> storeCollection = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.storename)));
        ContentValues values = new ContentValues();

        for (int index=0; index < storeCollection.size(); index++) {
            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,1);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,5);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,2);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,2);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,3);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,26);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,4);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,4);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,5);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,2);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,6);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,3);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,7);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,3);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,8);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,19);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,9);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,11);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,10);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,8);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,11);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,10);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,12);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,4);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,13);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,3);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,14);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,18);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,15);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,2);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,16);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,19);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,17);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,3);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,18);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,15);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,19);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,14);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,20);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,26);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,21);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,14);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,22);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,3);

            values.put(DBHelper.STORE_CATEGORY_FK_STORE_ID,23);
            values.put(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID,3);

            db.insert(TABLE_STORE_CATEGORY, null, values);
        }

    }

    /*private void insertCategoryInDB(SQLiteDatabase db) {

        XmlResourceParser xmlParser = context.getResources().getXml(R.xml.categories);
        String categoryName;
        boolean flag;
        //database

        try {
            while (xmlParser.next() != XmlPullParser.END_DOCUMENT) {
                if (xmlParser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = xmlParser.getName();
                Log.d(LOG_NAME, "name:" + name);
                if (name.equals("categories")) {
                    Log.d(LOG_NAME, " in if ");
                    while (xmlParser.next() != XmlPullParser.END_DOCUMENT) {
                        Log.d(LOG_NAME, "in while ");
                        if (xmlParser.getEventType() != XmlPullParser.START_TAG) {
                            Log.d(LOG_NAME, "in frist if ");
                            continue;
                        }
                        categoryName = readText(xmlParser);
                        Log.d(LOG_NAME, "categoryName: " + categoryName);

                        if (!Objects.equals(categoryName, "[Select Category]")) {
//                            Log.d(LOG_NAME, "name in else :" + name);
                            ContentValues values = new ContentValues();
                            values.put(DBHelper.CATEGORY_NAME, categoryName);
                            long id = db.insert(TABLE_CATEGORY, null, values);
                            Log.d(LOG_NAME, "category id" + id + "\n" + categoryName);
                        } else {
                            break;
                        }
                    }
                }

            }

            flag = true;
        } catch (XmlPullParserException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            flag = false;
        }
        //return flag;
    }*/


    private String readText(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

}
