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

import com.conestogac.receipt_keeper.helpers.KeyPairBoolData;
import com.conestogac.receipt_keeper.models.Receipt;
import com.conestogac.receipt_keeper.models.Tag;
import com.conestogac.receipt_keeper.MultiSpinnerSearch.MultiSpinnerSearchListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class AddReceiptActivity extends Activity {

    private EditText storeNamEditText;
    private EditText totalEditText;
    private EditText dateEditText;
    private Button saveReceiptButton;
    private SQLController dbController;
    private Calendar dateAndTime = Calendar.getInstance();
    private LinkedList<Tag> tags = new LinkedList<>();
    private MultiSpinnerSearch searchSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_receipt);

        final List<String> list = Arrays.asList(getResources().getStringArray(R.array.tags));
        TreeMap<String, Boolean> items = new TreeMap<>();
        for (String item : list) {
            items.put(item, Boolean.FALSE);
        }


        dbController = new SQLController(this);
        totalEditText = (EditText) findViewById(R.id.totalEditText);
        dateEditText = (EditText) findViewById(R.id.dateEditText);

        saveReceiptButton = (Button) findViewById(R.id.saveReceiptButton);

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
                //Date date = convertStringToDate(dateString);
                receipt.setDate(dateString);
                receipt.setTotal(Float.parseFloat(totalEditText.getText().toString()));
                tags = searchSpinner.getAllTags();
                saveReceiptDataInDB(receipt, tags);

                Intent goToHomePage = new Intent(AddReceiptActivity.this, HomeActivity.class);
                startActivity(goToHomePage);
            }
        });


        /**
         * Search MultiSelection Spinner (With Search/Filter Functionality)
         *
         *  Using MultiSpinnerSearch class
         */
        searchSpinner = (MultiSpinnerSearch) findViewById(R.id.searchMultiSpinner);
        final LinkedList<KeyPairBoolData> listArray = new LinkedList<>();

        for (int i = 0; i < list.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(list.get(i));
            h.setSelected(false);
            listArray.add(h);
        }

        /*LinkedList<Tag> tags = searchSpinner.saveAllTags();
        for (Tag tag : tags)
            Log.d("tag.getTagName()", tag.getTagName());*/
        /***
         * -1 is no by default selection
         * 0 to length will select corresponding values
         */
        searchSpinner.setItems(listArray, "Tag search", -1, new MultiSpinnerSearchListener() {

            @Override
            public void onItemsSelected(LinkedList<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i("TAG", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());

                    }
                }

            }
        });
    }


    // [Convert string we got from the EditText to Date ]
    /*private Date convertStringToDate(String dateString) {
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
    }*/

    // []
    private void saveReceiptDataInDB(Receipt receipt, LinkedList<Tag> tag) {
        dbController.open();
        dbController.insertReceipt(receipt, tag);
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
