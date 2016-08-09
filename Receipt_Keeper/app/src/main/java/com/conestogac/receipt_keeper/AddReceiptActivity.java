package com.conestogac.receipt_keeper;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.conestogac.receipt_keeper.helpers.DBHelper;
import com.conestogac.receipt_keeper.helpers.KeyPairBoolData;
import com.conestogac.receipt_keeper.helpers.PublicHelper;
import com.conestogac.receipt_keeper.models.Receipt;
import com.conestogac.receipt_keeper.models.Tag;
import com.conestogac.receipt_keeper.ocr.CaptureActivity;
import com.conestogac.receipt_keeper.uploader.CustomerRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;


public class AddReceiptActivity extends Activity {

    // [Layout views]
    private AutoCompleteTextView storeNamEditText;
    private EditText totalEditText;
    private EditText dateEditText;
    private EditText commentEditText;
    private AutoCompleteTextView paymentEditText;
    private SearchableSpinner categorySearchMultiSpinner;
    private MultiSpinnerSearch tagSearchSpinner;
    private FloatingActionButton saveReceiptButton;
    private ImageButton receiptImageButton;


    private SQLController dbController;
    private Calendar dateAndTime = Calendar.getInstance();
    private LinkedList<Tag> tags = new LinkedList<>();
    private ReceiptKeeperApplication app;
    private static final String LOG_NAME = "AddReceiptActivity";

    // [ from TestUplaodActivity]
    private CustomerRepository userRepo;
    private RestAdapter adapter;
    private String imagePath;
    private String imageFileName;
    private String paymentMethod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_receipt);

        app = (ReceiptKeeperApplication) this.getApplication();
        adapter = app.getLoopBackAdapter();
        userRepo = adapter.createRepository(CustomerRepository.class);
        dbController = new SQLController(this);


        final List<String> tagList = Arrays.asList(getResources().getStringArray(R.array.tags));
        TreeMap<String, Boolean> tagItems = new TreeMap<>();
        for (String item : tagList) {
            tagItems.put(item, Boolean.FALSE);
        }


        final List<String> categoryList = Arrays.asList(getResources().getStringArray(R.array.categories));
        TreeMap<String, Boolean> categoryItems = new TreeMap<>();
        for (String item : categoryList) {
            categoryItems.put(item, Boolean.FALSE);
        }

        totalEditText = (EditText) findViewById(R.id.totalEditText);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        storeNamEditText = (AutoCompleteTextView) findViewById(R.id.storeNamEditText);
        commentEditText = (EditText) findViewById(R.id.commentEditText);
        paymentEditText = (AutoCompleteTextView) findViewById(R.id.paymentEditText);
        //saveReceiptButton = (Button) findViewById(R.id.saveReceiptButton);
        categorySearchMultiSpinner = (SearchableSpinner) findViewById(R.id.categorySearchMultiSpinner);
        tagSearchSpinner = (MultiSpinnerSearch) findViewById(R.id.searchMultiSpinner);
        receiptImageButton = (ImageButton) findViewById(R.id.receiptImageButton);

        Log.d(LOG_NAME, "in on create");
        categorySearchMultiSpinner.setTitle("Select Item");
        categorySearchMultiSpinner.setPositiveButton("OK");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySearchMultiSpinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();

        //  Go to AddReceiptActivity when clicked
        final FloatingActionButton saveReceiptButton = (FloatingActionButton) findViewById(R.id.saveReceiptButton);
        saveReceiptButton.setImageResource(R.drawable.ic_save_white_24dp);


        //TODO check with Nick how to refactor this method
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

            Calendar cal = Calendar.getInstance();

            if (dayToSet == 0 || monthToSet == 0 || yearToSet == 0) {
                cal.getTime();
            } else {
                cal.set(Calendar.YEAR, yearToSet);
                cal.set(Calendar.DAY_OF_MONTH, dayToSet);
                cal.set(Calendar.MONTH, monthToSet);
            }

            String format = ReceiptCursorAdapter.sdf_user.format(cal.getTime());

            dateEditText.setText(dateAndTime.toString());
            if (dateAndTime != null) {

                dateEditText.setText(format);

            }

            imagePath = extras.getString("imagePath");
            imageFileName = extras.getString("imageFileName");

            File Dir = new File(imagePath);
            File file = new File(Dir, imageFileName);

            try {
                File f = new File(imagePath, imageFileName);
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                receiptImageButton.setImageBitmap(b);
            } catch (IOException e) {
                e.printStackTrace();
            }

            paymentMethod = extras.getString("paymentMethod");

            if (paymentMethod != "") {
                paymentEditText.setText(paymentMethod);
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

        saveReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long _id;
                String absolutePath;

                if (!validateForm()) return;

                if (imageFileName == null || imagePath == null) {
                    absolutePath = "";
                } else {
                    File f = new File(imagePath, imageFileName);
                    absolutePath = f.getAbsolutePath();
                }

                dbController.open();
                Receipt receipt = new Receipt();
                String customerId = null;
                try {
                    customerId = app.getCurrentUser().getId().toString();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    customerId = null;
                } finally {
                    receipt.setCustomerId(customerId);
                    receipt.setStoreId(dbController.insertStoreByName(storeNamEditText.getText().toString()));

                    //TODO check the amount, it should be in float
                    receipt.setTotal(Float.parseFloat(totalEditText.getText().toString()));

                    receipt.setDate(PublicHelper.formatUserToformatDB((dateEditText.getText().toString())));

                    receipt.setComment(commentEditText.getText().toString());
                    receipt.setPaymentMethod(paymentEditText.getText().toString());
                    receipt.setUrl(absolutePath);
                    String receiptCategory = categorySearchMultiSpinner.getSelectedItem().toString();
                    if (!Objects.equals(receiptCategory, "Select Category")) {
                        receipt.setCategoryId(dbController.getCategoryIdByName(receiptCategory));
                    } else {
                        receipt.setCategoryId(dbController.getCategoryIdByName("None"));  //None
                    }

                    tags = tagSearchSpinner.getAllTags();
                }


                dbController.insertReceipt(receipt, tags);
                dbController.close();
                Intent goToHomePage = new Intent(AddReceiptActivity.this, Home2Activity.class);
                ;
                goToHomePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goToHomePage);
                finish();
            }

        });

        receiptImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagePath != null && imageFileName != null) {
                    Intent popIntent = new Intent(AddReceiptActivity.this, Pop.class);
                    File f = new File(imagePath, imageFileName);
                    popIntent.putExtra("imagePath", f.getAbsolutePath());
                    startActivity(popIntent);
                } else {
                    Intent goToOcrIntent = new Intent(AddReceiptActivity.this, CaptureActivity.class);
                    startActivity(goToOcrIntent);
                }
            }
        });

        final LinkedList<KeyPairBoolData> tagsListArray = new LinkedList<>();

        for (int i = 0; i < tagList.size(); i++) {
            if (tagList.get(i).equals("")) {
                continue;
            } else {
                KeyPairBoolData h = new KeyPairBoolData();
                h.setId(i + 1);
                h.setName(tagList.get(i));
                h.setSelected(false);
                tagsListArray.add(h);
            }
        }


        /*
        *
        * @Params tagList = -1 is empty defualt selection
        *         A list of objects can be sent and set in the spinner
        * */
        tagSearchSpinner.setItems(tagsListArray, "[Select Tag]", tagList, new MultiSpinnerSearch.MultiSpinnerSearchListener() {

            @Override
            public void onItemsSelected(LinkedList<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i("TAG", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());

                    }
                }
            }
        });
        addPaymentToAutoComplete();
        addStoreToAutoComplete();
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

        dateEditText.setText(ReceiptCursorAdapter.sdf_user.format(dateAndTime.getTime()));

    }


    private void addStoreToAutoComplete() {
        List<String> storeCollection = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.storename)));
        Cursor cursor;

        dbController.open();
        cursor = dbController.getAllStore();
        if (cursor != null && cursor.getCount() != 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if (!storeCollection.contains(cursor.getString(cursor.getColumnIndex(DBHelper.STORE_NAME)))) {
                    storeCollection.add(cursor.getString(cursor.getColumnIndex(DBHelper.STORE_NAME)));
                }
            }
        }

        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddReceiptActivity.this,
                android.R.layout.simple_dropdown_item_1line, storeCollection);

        storeNamEditText.setAdapter(adapter);
        dbController.close();
    }


    private void addPaymentToAutoComplete() {
        List<String> paymentCollection = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.payment)));
        Cursor cursor;

        dbController.open();
        cursor = dbController.getAllPaymentMethod();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            if (!paymentCollection.contains(cursor.getString(cursor.getColumnIndex(DBHelper.RECEIPT_PAYMENT_METHOD)))) {
                paymentCollection.add(cursor.getString(cursor.getColumnIndex(DBHelper.RECEIPT_PAYMENT_METHOD)));
            }
        }

        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(AddReceiptActivity.this,
                        android.R.layout.simple_dropdown_item_1line, paymentCollection);

        paymentEditText.setAdapter(adapter);
        dbController.close();
    }


    /*
      Form validation
    */
    private boolean validateForm() {
        boolean isValid = true;
        // Reset errors.
        storeNamEditText.setError(null);
        totalEditText.setError(null);
        dateEditText.setError(null);

        // Store values at the time of the login attempt.
        String storeName = storeNamEditText.getText().toString();
        String total = totalEditText.getText().toString();
        String dateEdit = dateEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(storeName)) {
            storeNamEditText.setError(getString(R.string.error_field_required));
            focusView = storeNamEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(total)) {
            totalEditText.setError(getString(R.string.error_field_required));
            focusView = totalEditText;
            cancel = true;
        }


        if (TextUtils.isEmpty(dateEdit)) {
            dateEditText.setError(getString(R.string.error_field_required));
            focusView = dateEditText;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            isValid = false;
        }

        return isValid;
    }

}

