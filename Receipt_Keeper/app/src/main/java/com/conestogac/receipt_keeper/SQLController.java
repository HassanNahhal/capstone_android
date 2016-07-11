package com.conestogac.receipt_keeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.conestogac.receipt_keeper.helpers.DBHelper;
import com.conestogac.receipt_keeper.models.Receipt;
import com.conestogac.receipt_keeper.models.Store;
import com.conestogac.receipt_keeper.models.Tag;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

public class SQLController {

    private DBHelper dbhelper;
    private Context ourcontext;
    private SQLiteDatabase database;

    // Logcat tag
    private static final String LOG_NAME = "DatabaseHelper";


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
        database.close();
    }


    /*public Cursor readAllReceipts() {

        String sqlQuery = "SELECT * FROM " + DBHelper.TABLE_RECEIPT + " re, "
                + DBHelper.TABLE_STORE + " st "
                + " WHERE re." + DBHelper.RECEIPT_FK_STORE_ID + "=st." + DBHelper.STORE_ID
                + " ORDER BY re." + DBHelper.RECEIPT_DATE;
        Cursor localCursor = this.database.rawQuery(sqlQuery, null);
        if (localCursor != null)
            localCursor.moveToFirst();
        return localCursor;

    }*/

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


    public Cursor readAllReceipts() {

        String sqlQuery = "SELECT * FROM " + DBHelper.TABLE_RECEIPT + " re, "
                + DBHelper.TABLE_STORE + " st " /*+ DBHelper.TABLE_STORE + " st " */
                + " WHERE re." + DBHelper.RECEIPT_FK_STORE_ID + "=st." + DBHelper.STORE_ID
                + " ORDER BY re." + DBHelper.RECEIPT_DATE;
        Cursor localCursor = this.database.rawQuery(sqlQuery, null);
        if (localCursor != null)
            localCursor.moveToFirst();
        return localCursor;

    }

    public long insertReceipt(Receipt receipt, LinkedList<Tag> tags) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.RECEIPT_FK_CUSTOMER_ID, receipt.getCustomerId());
        values.put(DBHelper.RECEIPT_FK_STORE_ID, receipt.getStoreId());
        values.put(DBHelper.RECEIPT_FK_CATEGORY_ID, receipt.getCategoryId());
        values.put(DBHelper.RECEIPT_COMMENT, receipt.getComment());
        values.put(DBHelper.RECEIPT_CREATEDATE, getDateTime());
        values.put(DBHelper.RECEIPT_DATE, receipt.getDate());
        values.put(DBHelper.RECEIPT_TOTAL, receipt.getTotal());
        values.put(DBHelper.RECEIPT_PAYMENT_METHOD, receipt.getPaymentMethod());


        if (receipt.getUrl() != null) {
            values.put(DBHelper.RECEIPT_URL, receipt.getUrl());
        }

        Log.d(LOG_NAME, "in insert receipt");
        // Insert row
        long receiptId = database.insert(DBHelper.TABLE_RECEIPT, null, values);

        if (tags != null) {
            // Assigning tags to
            for (Tag tag : tags) {
                insertTag(tag);
                //insertReceiptTag(receiptId, tag.getTagId());
            }
        }

        return receiptId;
    }


    public long insertTag(Tag tag) {
        ContentValues values = new ContentValues();
        //values.put(TAG_ID, tag.getTagId());
        values.put(DBHelper.TAG_NAME, tag.getTagName());

        return database.insert(DBHelper.TABLE_TAG, null, values);
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

    public int insertStoreByName(String name) {
        Long storeId;
        ContentValues cv = new ContentValues();

        storeId = findStore(name);

        if (storeId == 0) {
            cv.put(DBHelper.STORE_NAME, name);
            storeId = database.insert(DBHelper.TABLE_STORE, null, cv);
        }

        return storeId.intValue();
    }

    public long insertStore(Store store) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.STORE_NAME, store.getName());

        return database.insert(DBHelper.TABLE_STORE, null, values);
    }

    private long findStore(String name) {
        String sqlQuery = "SELECT * FROM " + DBHelper.TABLE_STORE + " WHERE " + DBHelper.STORE_NAME + "=\'" + name + "\'";
        Cursor localCursor = this.database.rawQuery(sqlQuery, null);

        if (localCursor.getCount() > 0) {
            localCursor.moveToFirst();
            return localCursor.getInt(localCursor.getColumnIndex(DBHelper.STORE_ID));
        } else {
            return 0;
        }
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
