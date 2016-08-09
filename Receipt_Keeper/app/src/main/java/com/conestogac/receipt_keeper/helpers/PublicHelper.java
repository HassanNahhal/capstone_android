package com.conestogac.receipt_keeper.helpers;

import com.conestogac.receipt_keeper.ReceiptCursorAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hassannahhal on 2016-07-30.
 * <p/>
 * <p/>
 * This class will include all public methods that have been used frequiently in the app
 * to lower the number of code and make use of
 **/

public class PublicHelper {

    // [ Method that takes a string and format it to another Date
    //   then returns a String]
    public static String formatDateToString(String initialDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);


        Date newDateDate = null;
        try {
            newDateDate = sdf.parse(initialDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        String newDateString = sdf.format(newDateDate);

        return newDateString;
    }

    // [ Method that takes a string and format it to another Date
    //   then returns a String]
    public static String formatUserToformatDB(String initialDate) {

        Date newDateDate = null;
        try {
            newDateDate = ReceiptCursorAdapter.sdf_user.parse(initialDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String newDateString = ReceiptCursorAdapter.sdf_db.format(newDateDate);

        return newDateString;
    }

    public static String formatDBToformatUser(String initialDate) {

        Date newDateDate = null;
        try {
            newDateDate = ReceiptCursorAdapter.sdf_db.parse(initialDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String newDateString = ReceiptCursorAdapter.sdf_user.format(newDateDate);

        return newDateString;
    }
}
