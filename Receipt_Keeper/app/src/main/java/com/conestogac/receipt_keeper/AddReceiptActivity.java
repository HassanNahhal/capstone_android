package com.conestogac.receipt_keeper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.conestogac.receipt_keeper.models.Receipt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddReceiptActivity extends Activity {

    private EditText storeNamEditText;
    private EditText totalEditText;
    private EditText dateEditText;
    private Button saveReceiptButton;
    private SQLController dbController;
    private Calendar dateAndTime = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_receipt);

        dbController = new SQLController(this);
        totalEditText = (EditText) findViewById(R.id.totalEditText);
        dateEditText = (EditText) findViewById(R.id.dateEditText);

        storeNamEditText = (EditText) findViewById(R.id.storeNamEditText);

        saveReceiptButton = (Button) findViewById(R.id.saveReceiptButton);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String storeName = extras.getString("StoreName");
            if (storeName != null) {
                storeNamEditText.setText(storeName);
            }
            String amount = extras.getString("Amount");
            if (amount != null) {
                totalEditText.setText(amount);
            }
            int yearToSet = 0;
            String year = extras.getString("Year");
            if (year != null) {
                yearToSet = Integer.parseInt(year);
            }
            int monthToSet = 0;
            int month = extras.getInt("Month");
            if (month != 0) {
                monthToSet = month;
            }
            int dayToSet = 0;
            String day = extras.getString("Day");
            if (day != null) {
                dayToSet = Integer.parseInt(day);
            }
            dateAndTime.set(yearToSet, monthToSet, dayToSet);
            dateEditText.setText(dateAndTime.toString());
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            if (dateAndTime != null) {
                dateEditText.setText(sdf.format(dateAndTime.getTime()));
            }
        }


        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(dateEditText.getContext(), d, dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        // [ onClick will get data from views and insert them into database]
        saveReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Receipt receipt = new Receipt();
                String dateString = dateEditText.getText().toString();
                Date date = convertStringToDate(dateString);
                receipt.setDate(date);

                String strTotal = totalEditText.getText().toString();
                strTotal = strTotal.replaceAll("[^\\d.]", "");
                receipt.setTotal(Float.parseFloat(strTotal));

                saveReceiptInDB(receipt);

                Intent goToHomePage = new Intent(AddReceiptActivity.this, HomeActivity.class);
                startActivity(goToHomePage);
            }
        });


    }

    // [Convert string we got from the EditText to Date ]
    private Date convertStringToDate(String dateString) {
        Date date = new Date();
        Log.d("dateString", dateString + "");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(dateString);
            Log.d("date", date + "");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    // []
    private void saveReceiptInDB(Receipt receipt) {
        dbController.open();
        dbController.insertReceipt(receipt);
        dbController.close();
    }

    // [DatePickerDialog population]
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }
    };

    // [Show date on TextView]
    private void updateDate() {
        dateEditText.setText(DateUtils
                .formatDateTime(this,
                        dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }
}
