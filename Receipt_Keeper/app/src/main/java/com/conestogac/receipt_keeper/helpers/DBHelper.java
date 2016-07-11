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


    // Table ReceiptTag and columns
    public static final String TABLE_RECEIPT_TAG = "receiptTag";
    public static final String FK_RECEIPT_ID = "receipt_id";
    public static final String FK_TAG_ID = "tag_id";


    // Table Tag and columns
    public static final String TABLE_TAG = "tag";
    public static final String TAG_ID = "_id";
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
            + RECEIPT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + RECEIPT_FK_CUSTOMER_ID + " INTEGER ," + RECEIPT_FK_STORE_ID + " INTEGER ,"
            + RECEIPT_FK_CATEGORY_ID + " INTEGER ," + RECEIPT_COMMENT + " TEXT ,"
            + RECEIPT_DATE + " TEXT ," + RECEIPT_TOTAL + " REAL, "
            + RECEIPT_URL + " TEXT," + RECEIPT_CREATEDATE + " TEXT ,"
            + RECEIPT_PAYMENT_METHOD + " TEXT"
            + ")";

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
    private static final String CREATE_TABLE_STORE_CATEGORY = " CREATE TABLE " + TABLE_STORE_CATEGORY
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
        db.execSQL(CREATE_TABLE_STORE_CATEGORY);
        this.insertTagInDB(db);

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
        db.execSQL(" DROP TABLE IF EXISTS" + TABLE_CATEGORY);

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
