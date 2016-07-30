package com.conestogac.receipt_keeper;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.conestogac.receipt_keeper.helpers.PublicHelper;

import java.util.Calendar;


public class FilterActivity extends AppCompatActivity implements View.OnClickListener {


    private final static String LOG_NAME = "FilterActivity";


    Context context;
    private Button dateFromButton;
    private Button dateToButton;
    private Calendar dateAndTime = Calendar.getInstance();

    public static final String TO_DATE = "toDate";
    public static final String FROM_DATE = "fromDate";
    public static final String FILTER_PREF = "toDate";

    SharedPreferences filterPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        context = this;

        filterPreferences = getSharedPreferences(FILTER_PREF, Context.MODE_PRIVATE);
        editor = filterPreferences.edit();

        dateFromButton = (Button) findViewById(R.id.dateFromButton);
        dateToButton = (Button) findViewById(R.id.dateToButton);
        findViewById(R.id.filterButton).setOnClickListener(this);

        dateFromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(dateFromButton.getContext(), dateFrom, dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dateToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(dateFromButton.getContext(), dateTo, dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    DatePickerDialog.OnDateSetListener dateTo = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateTo();
        }
    };

    private void updateDateTo() {
        dateToButton.setText(PublicHelper.formatDateToString(DateUtils
                .formatDateTime(this,
                        dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR)));
    }

    DatePickerDialog.OnDateSetListener dateFrom = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateFrom();
        }
    };


    private void updateDateFrom() {
        dateFromButton.setText(PublicHelper.formatDateToString(DateUtils
                .formatDateTime(this,
                        dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR)));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.filterButton:
                filterReceipts();
                break;
        }
    }

    private void filterReceipts() {
        String fromDateString = PublicHelper.formatDateToString(dateFromButton.getText().toString());
        String toDateString = PublicHelper.formatDateToString(dateToButton.getText().toString());

        editor.putString(TO_DATE, toDateString);
        editor.putString(FROM_DATE, fromDateString);
        editor.commit();

        Intent goToHomeActivityIntent = new Intent(this, Home2Activity.class);
        startActivity(goToHomeActivityIntent);


    }
}
