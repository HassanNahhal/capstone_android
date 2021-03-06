package com.conestogac.receipt_keeper;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.conestogac.receipt_keeper.helpers.DBHelper;
import com.conestogac.receipt_keeper.helpers.GlideUtil;
import com.conestogac.receipt_keeper.helpers.KeyPairBoolData;
import com.conestogac.receipt_keeper.helpers.PublicHelper;
import com.conestogac.receipt_keeper.models.Receipt;
import com.conestogac.receipt_keeper.models.Tag;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

public class UpdateReceiptActivity extends AppCompatActivity implements View.OnClickListener {

    private AutoCompleteTextView updateStoreNamEditText;
    private EditText updateTotalEditText;
    private EditText updateDateEditText;
    private MultiSpinnerSearch updateTagSearchMultiSpinner;
    private SearchableSpinner updateCategorySearchMultiSpinner;
    private EditText updateCommentEditText;
    private AutoCompleteTextView updatePaymentEditText;
    private Button updateEditReceiptButton;
    private ImageButton updateReceiptImageButton;
    private Calendar dateAndTime = Calendar.getInstance();
    private List<String> tagArraySelected = new ArrayList<String>();

    // [ Intent values to send to UpdateReceiptActivity and receive from Home2Activity]
    String storeName;
    float total;
    String date;
    String comment;
    String paymentMethod;
    String categoryName;
    String tagName;
    String imagePath;
    int receiptId;


    private File file;
    private SQLController dbController;
    List<String> categoryList = new LinkedList<>();
    final LinkedList<KeyPairBoolData> tagsListArray = new LinkedList<>();

    private static final String LOG_NAME = "UpdateReceiptActivity";
    private LinkedList<Tag> tags = new LinkedList<>();
    private ReceiptKeeperApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_receipt);

        dbController = new SQLController(this);
        app = (ReceiptKeeperApplication) this.getApplication();
        updateStoreNamEditText = (AutoCompleteTextView) findViewById(R.id.updateStoreNamEditText);
        updateTotalEditText = (EditText) findViewById(R.id.updateTotalEditText);
        updateDateEditText = (EditText) findViewById(R.id.updateDateEditText);
        updateTagSearchMultiSpinner = (MultiSpinnerSearch) findViewById(R.id.updateTagSearchMultiSpinner);
        updateCategorySearchMultiSpinner = (SearchableSpinner) findViewById(R.id.updateCategorySearchMultiSpinner);
        updateCommentEditText = (EditText) findViewById(R.id.updateCommentEditText);
        updatePaymentEditText = (AutoCompleteTextView) findViewById(R.id.updatePaymentEditText);
        findViewById(R.id.updateEditReceiptButton).setOnClickListener(this);
        updateReceiptImageButton = (ImageButton) findViewById(R.id.updateReceiptImageButton);


        //  Go to AddReceiptActivity when clicked
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.updateEditReceiptButton);
        fab.setImageResource(R.drawable.ic_done_white_24dp);
        findViewById(R.id.updateEditReceiptButton).setOnClickListener(this);


        categoryList = Arrays.asList(getResources().getStringArray(R.array.categories));
        TreeMap<String, Boolean> categoryItems = new TreeMap<>();
        for (String item : categoryList) {
            categoryItems.put(item, Boolean.FALSE);
        }

        setAllDataFromIntent();

        updateDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(updateDateEditText.getContext(), d, dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        addPaymentToAutoComplete();
        addStoreToAutoComplete();
    }

    private void setAllDataFromIntent() {
        Bundle extras = getIntent().getExtras();
        Cursor cursor;

        if (extras != null) {
            receiptId = extras.getInt("receiptId");
            Log.d(LOG_NAME, "receipt id :" + receiptId);

            storeName = extras.getString("storeName");
            if (storeName != null) {
                updateStoreNamEditText.setText(storeName);
            }
            total = extras.getFloat("total");
            if (total != 0) {
                updateTotalEditText.setText(String.valueOf(total));
            }

            date = extras.getString("date");
            if (date != null) {
                updateDateEditText.setText(date);
            }

            comment = extras.getString("comment");
            if (comment != null) {
                updateCommentEditText.setText(comment);
            }

            paymentMethod = extras.getString("paymentMethod");
            if (paymentMethod != null) {
                updatePaymentEditText.setText(paymentMethod);
            }

            //String categoryName = extras.getString("categoryName");
            int categoryId = extras.getInt("categoryId");
            if (categoryId != 0) {


                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                updateCategorySearchMultiSpinner.setAdapter(spinnerAdapter);
                updateCategorySearchMultiSpinner.setSelection(categoryId);


            }
        }

        buildTagList(); //read from db. receipt_id should be set

        //String tagName = extras.getString("tagName");

        updateTagSearchMultiSpinner.setItems(tagsListArray, "[Select Tag]", tagArraySelected, new MultiSpinnerSearch.MultiSpinnerSearchListener() {

            @Override
            public void onItemsSelected(LinkedList<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i("TAG", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                    }
                }
            }
        });


        imagePath = extras.getString("imagePath");
        if (imagePath != null) {
            file = new File(imagePath);
            GlideUtil.loadImage(file, updateReceiptImageButton);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.updateEditReceiptButton:
                if (!validateForm()) return;
                updateReceipt();
                break;
            default:
                break;
        }

    }

    private void updateReceipt() {

        //===============
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
            Log.d(LOG_NAME, "receipt id :" + receiptId);
            receipt.setId(receiptId);
            receipt.setStoreId(dbController.insertStoreByName(updateStoreNamEditText.getText().toString()));

            String total = updateTotalEditText.getText().toString();
            total.replace("$", "");
            receipt.setTotal(Float.parseFloat(total));

            receipt.setDate(PublicHelper.formatUserToformatDB(updateDateEditText.getText().toString()));
            receipt.setComment(updateCommentEditText.getText().toString());
            receipt.setPaymentMethod(updatePaymentEditText.getText().toString());
            receipt.setUrl(imagePath);
            String receiptCategory = updateCategorySearchMultiSpinner.getSelectedItem().toString();
            if (!Objects.equals(receiptCategory, "Select Category")) {
                receipt.setCategoryId(dbController.getCategoryIdByName(receiptCategory));
            } else {
                receipt.setCategoryId(dbController.getCategoryIdByName("None"));
            }

            tags = updateTagSearchMultiSpinner.getAllTags();

        }

        dbController.updateReceipt(receipt, tags);
        dbController.close();

        Intent goToHomePage = new Intent(this, Home2Activity.class);
        goToHomePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToHomePage);
        finish();
    }

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
//        updateDateEditText.setText(DateUtils
//                .formatDateTime(this,
//                        dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        updateDateEditText.setText(ReceiptCursorAdapter.sdf_db.format(dateAndTime.getTime()));
    }

    private void buildTagList() {
        Cursor cursor;
        dbController.open();
        String tag;

        cursor = dbController.getReceiptTagIds(receiptId);
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                tag = cursor.getString(cursor.getColumnIndex(DBHelper.TAG_NAME));
                if (!(tag == null || tag.equals("")))
                    tagArraySelected.add(tag);
            }
        }
        dbController.close();


        final List<String> tagList = Arrays.asList(getResources().getStringArray(R.array.tags));
        TreeMap<String, Boolean> tagItems = new TreeMap<>();
        for (String item : tagList) {
            tagItems.put(item, Boolean.FALSE);
        }

        for (int i = 0; i < tagList.size(); i++) {
            if (tagList.get(i).equals("")) {
                continue;
            } else {
                KeyPairBoolData h = new KeyPairBoolData();
                h.setId(i + 1);
                h.setName(tagList.get(i));
                if (tagArraySelected.contains(tagList.get(i))) {
                    h.setSelected(true);
                } else {
                    h.setSelected(false);
                }
                tagsListArray.add(h);
            }
        }
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(UpdateReceiptActivity.this,
                android.R.layout.simple_dropdown_item_1line, storeCollection);

        updateStoreNamEditText.setAdapter(adapter);
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
                new ArrayAdapter<>(UpdateReceiptActivity.this,
                        android.R.layout.simple_dropdown_item_1line, paymentCollection);

        updatePaymentEditText.setAdapter(adapter);
        dbController.close();
    }

    /*
      Form validation
    */
    private boolean validateForm() {
        boolean isValid = true;
        // Reset errors.
        updateStoreNamEditText.setError(null);
        updateTotalEditText.setError(null);
        updateDateEditText.setError(null);

        // Store values at the time of the login attempt.
        String storeName = updateStoreNamEditText.getText().toString();
        String total = updateTotalEditText.getText().toString();
        String dateEdit = updateDateEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(storeName)) {
            updateStoreNamEditText.setError(getString(R.string.error_field_required));
            focusView = updateStoreNamEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(total)) {
            updateTotalEditText.setError(getString(R.string.error_field_required));
            focusView = updateTotalEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(dateEdit)) {
            updateDateEditText.setError(getString(R.string.error_field_required));
            focusView = updateDateEditText;
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
