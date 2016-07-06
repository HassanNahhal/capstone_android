package com.conestogac.receipt_keeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.conestogac.receipt_keeper.helpers.DBHelper;
import com.conestogac.receipt_keeper.models.Receipt;
import com.conestogac.receipt_keeper.models.Tag;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

public class SQLController {


    // Table receipts and columns
    private static final String TABLE_RECEIPT = "receipt";
    private static final String RECEIPT_ID = "_id";
    private static final String RECEIPT_FK_CUSTOMER_ID = "customer_id";
    private static final String RECEIPT_FK_STORE_ID = "store_id";
    private static final String RECEIPT_FK_CATEGORY_ID = "category_id";
    private static final String RECEIPT_COMMENT = "comment";
    private static final String RECEIPT_DATE = "date";
    public static final String RECEIPT_TOTAL = "total";

    private DBHelper dbhelper;
    private Context ourcontext;
    private SQLiteDatabase database;

    // Table Tag and columns
    private static final String TABLE_TAG = "tag";
    private static final String TAG_ID = "_id";
    private static final String TAG_NAME = "tag_name";

    // Logcat tag
    private static final String DATABASE_LOG = "DatabaseHelper";


    // Table Store and columns
    private static final String TABLE_STORE = "store";
    private static final String STORE_ID = "id";
    private static final String STORE_NAME = "store_name";


    // Table Category and columns
    private static final String TABLE_CATEGORY = "category";
    private static final String CATEGORY_ID = "id";
    private static final String CATEGORY_NAME = "category_name";


    // Table StoreGategory and columns
    private static final String TABLE_STORE_CATEGORY = "storeCategory";
    private static final String STORECATEGORY_FK_CATEGORY_ID = "category_id";
    private static final String STORE_CATEGORY_FK_STORE_ID = "store_id";

    // Table ReceiptTag and columns
    private static final String TABLE_RECEIPT_TAG = "receiptTag";
    private static final String FK_RECEIPT_ID = "receipt_id";
    private static final String FK_TAG_ID = "tag_id";


    public SQLController(Context C) {
        ourcontext = C;
    }

    public SQLController open() throws SQLException {
        dbhelper = new DBHelper(ourcontext);
        database = dbhelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbhelper.close();
    }


    public Cursor readAllReceipts() {

        String sqlQuery = "SELECT * FROM receipt, tag " +
                "WHERE receipt._id= tag._id " +
                "GROUP BY receipt._id";
        Cursor localCursor = this.database.rawQuery(sqlQuery, null);
        if (localCursor != null)
            localCursor.moveToFirst();
        return localCursor;

    }

    /*public Cursor readAllReceipts() {

        Cursor localCursor = this.database.query(DBHelper.TABLE_RECEIPT,
                new String[]{
                        DBHelper.RECEIPT_ID,
                        DBHelper.RECEIPT_DATE,
                        DBHelper.RECEIPT_TOTAL
                }
                , null,
                null, null, null, null);
        if (localCursor != null)
            localCursor.moveToFirst();
        return localCursor;

    }*/


    public long insertReceipt(Receipt receipt, LinkedList<Tag> tags) {
        ContentValues values = new ContentValues();
        //values.put(RECEIPT_FK_CUSTOMER_ID, receipt.getCustomerId());
        //values.put(RECEIPT_FK_STORE_ID, receipt.getStoreId());
        //values.put(RECEIPT_FK_CATEGORY_ID, receipt.getCategroyId());
        //values.put(RECEIPT_COMMENT, receipt.getComment());
        values.put(RECEIPT_DATE, getDateTime());
        values.put(RECEIPT_TOTAL, receipt.getTotal());


        // Insert row
        long receiptId = database.insert(TABLE_RECEIPT, null, values);

        // Assigning tags to
        for (Tag tag : tags) {
            insertTag(tag);
            //insertReceiptTag(receiptId, tag.getTagId());
        }

        return receiptId;
    }

    public long insertTag(Tag tag) {
        ContentValues values = new ContentValues();
        //values.put(TAG_ID, tag.getTagId());
        values.put(TAG_NAME, tag.getTagName());

        return database.insert(TABLE_TAG, null, values);
    }

    public Cursor readAllTags() {

        Cursor localCursor = this.database.query(DBHelper.TABLE_TAG,
                new String[]{
                        DBHelper.TAG_ID,
                        DBHelper.TAG_NAME,
                }
                , null,
                null, null, null, null);
        if (localCursor != null)
            localCursor.moveToFirst();
        return localCursor;

    }


    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    /*public String convertDateToString() {
        String datetime;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = new Date();
        datetime = dateFormat.format(date);
        System.out.println("Current Date Time : " + datetime);


        return datetime;
    }*/

    /*  public long insertReceiptTag(long receiptId, long tagId) {
        ContentValues values = new ContentValues();
        values.put(FK_RECEIPT_ID, receiptId);
        //values.put(FK_TAG_ID, tagId);

        return database.insert(TABLE_RECEIPT_TAG, null, values);
    }*/


    /*public LinkedList<Receipt> readAllReceipts() {
        String query = "Select * FROM " + DBHelper.TABLE_RECEIPT; *//*+ "," + DBHelper.TABLE_TAG;*//*+ " rec INNER JOIN " + DBHelper.TABLE_TAG +
                " tag ON rec._id=tag.id";*//*

        Log.d("query", query + "");
        LinkedList<Receipt> receipts = new LinkedList<>();
        Cursor localCursor = database.rawQuery(query, null);

        if (localCursor.moveToFirst())
            do {
                Receipt receipt = new Receipt();
                receipt.setId(localCursor.getColumnIndex(RECEIPT_ID));
                receipt.setTotal(localCursor.getColumnIndex(RECEIPT_TOTAL));
                receipt.setDate(localCursor.getString(localCursor.getColumnIndex(RECEIPT_DATE)));
                //receipt.setTagId(localCursor.getColumnIndex(TAG_ID));


                receipts.add(receipt);
            } while (localCursor.moveToFirst());
        return receipts;

    }*/

    /*public long insertReceipt(Receipt receipt) {
        ContentValues values = new ContentValues();
        //values.put(RECEIPT_FK_CUSTOMER_ID, receipt.getCustomerId());
        //values.put(RECEIPT_FK_STORE_ID, receipt.getStoreId());
        //values.put(RECEIPT_FK_CATEGORY_ID, receipt.getCategroyId());
        //values.put(RECEIPT_COMMENT, receipt.getComment());
        values.put(RECEIPT_DATE, getDateTime());
        values.put(RECEIPT_TOTAL, receipt.getTotal());


        // Insert row
        long receiptId = database.insert(TABLE_RECEIPT, null, values);

        // Assigning tags to
        *//*for (long tag_id : tag_ids) {
            insertReceiptTag(receiptId, tag_id);
        }*//*

        return receiptId;
    }*/




   /* public void insertData(String firstName, String lastName, int marks) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.FIRST_NAME, firstName);
        cv.put(DBHelper.LAST_NAME, lastName);
        cv.put(DBHelper.MARKS, marks);
        database.insert(DBHelper.TABLE_USER, null, cv);
    }

    public Cursor readData() {
        String[] allColumns = new String[]{DBHelper.USER_ID,
                DBHelper.FIRST_NAME, DBHelper.LAST_NAME, DBHelper.MARKS};
        Cursor c = database.query(DBHelper.TABLE_USER, allColumns, null, null,
                null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor readAll() {

        Cursor localCursor = this.database.query(DBHelper.TABLE_USER,
                new String[]{
                        DBHelper.USER_ID,
                        DBHelper.FIRST_NAME + "|| '  ' ||"
                                + DBHelper.LAST_NAME, DBHelper.MARKS}
                , null,
                null, null, null, null);
        if (localCursor != null)
            localCursor.moveToFirst();
        return localCursor;

    }

    public int updateData(long memberID, String firstName, String lastName,
                          int marks) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.FIRST_NAME, firstName);
        cv.put(DBHelper.LAST_NAME, lastName);
        cv.put(DBHelper.MARKS, marks);
        int i = database.update(DBHelper.TABLE_USER, cv, DBHelper.USER_ID
                + " = " + memberID, null);
        return i;
    }

    //[ Delete record using id in the database]
    public void deleteData(long memberID) {
        database.delete(DBHelper.TABLE_USER, DBHelper.USER_ID + "=" + memberID,
                null);
    }

    // [ Delete record using its location in the list]
    public void delete(int orderInList) {
        List<Integer> database_ids = new ArrayList<Integer>();
        Cursor c = database.rawQuery("SELECT * FROM " + DBHelper.TABLE_USER, null);
        while (c.moveToNext()) {
            database_ids.add(Integer.parseInt(c.getString(0)));
        }
        database.delete(DBHelper.TABLE_USER, DBHelper.USER_ID + " =?",
                new String[]{String.valueOf(database_ids.get(orderInList))});
    }*/

}
