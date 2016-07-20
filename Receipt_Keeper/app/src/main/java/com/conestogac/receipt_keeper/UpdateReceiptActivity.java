package com.conestogac.receipt_keeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.conestogac.receipt_keeper.helpers.GlideUtil;
import com.conestogac.receipt_keeper.helpers.KeyPairBoolData;
import com.conestogac.receipt_keeper.models.Receipt;
import com.conestogac.receipt_keeper.models.Tag;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.File;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

public class UpdateReceiptActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText updateStoreNamEditText;
    private EditText updateTotalEditText;
    private EditText updateDateEditText;
    private MultiSpinnerSearch updateTagSearchMultiSpinner;
    private SearchableSpinner updateCategorySearchMultiSpinner;
    private EditText updateCommentEditText;
    private EditText updatePaymentEditText;
    private Button updateEditReceiptButton;
    private ImageButton updateReceiptImageButton;


    // [ Intent values to send to UpdateReceiptActivity and receive from Home2Activity]
    String storeName;
    int total;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_receipt);

        dbController = new SQLController(this);

        // [ Setting IDs to Views ]
        updateStoreNamEditText = (EditText) findViewById(R.id.updateStoreNamEditText);
        updateTotalEditText = (EditText) findViewById(R.id.updateTotalEditText);
        updateDateEditText = (EditText) findViewById(R.id.updateDateEditText);
        updateTagSearchMultiSpinner = (MultiSpinnerSearch) findViewById(R.id.updateTagSearchMultiSpinner);
        updateCategorySearchMultiSpinner = (SearchableSpinner) findViewById(R.id.updateCategorySearchMultiSpinner);
        updateCommentEditText = (EditText) findViewById(R.id.updateCommentEditText);
        updatePaymentEditText = (EditText) findViewById(R.id.updatePaymentEditText);
        findViewById(R.id.updateEditReceiptButton).setOnClickListener(this);
        updateReceiptImageButton = (ImageButton) findViewById(R.id.updateReceiptImageButton);

        //TODO delete when XMLParser added
        categoryList = Arrays.asList(getResources().getStringArray(R.array.categories));
        TreeMap<String, Boolean> categoryItems = new TreeMap<>();
        for (String item : categoryList) {
            categoryItems.put(item, Boolean.FALSE);
        }
        final List<String> tagList = Arrays.asList(getResources().getStringArray(R.array.tags));
        TreeMap<String, Boolean> tagItems = new TreeMap<>();
        for (String item : tagList) {
            tagItems.put(item, Boolean.FALSE);
        }


        for (int i = 0; i < tagList.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(tagList.get(i));
            h.setSelected(false);
            tagsListArray.add(h);
        }


        setAllDataFromIntent();

    }

    private void setAllDataFromIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            receiptId = extras.getInt("receiptId");
            Log.d(LOG_NAME, "receipt id :" + receiptId);

            storeName = extras.getString("storeName");
            if (storeName != null) {
                updateStoreNamEditText.setText(storeName);
            }
            total = extras.getInt("total");
            if (total != 0) {
                updateTotalEditText.setText("$" + String.valueOf(total));
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

        //String tagName = extras.getString("tagName");
        int tagId = extras.getInt("tagId");
        if (tagId != 0) {
            updateTagSearchMultiSpinner.setItems(tagsListArray, "Tag search", tagId - 1, new MultiSpinnerSearch.MultiSpinnerSearchListener() {

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
        final String image = "/storage/emulated/0/ReceiptKeeperFolder/2016_07_05_20_00_04.Receipt.bmp";
        String customerId = null;
        try {
            //customerId = app.getCurrentUser().getId().toString();
        } catch (NullPointerException e) {
            e.printStackTrace();
            customerId = null;
        } finally {
            receipt.setCustomerId(customerId);
            Log.d(LOG_NAME, "receipt id :" + receiptId);
            receipt.setId(receiptId);
            receipt.setStoreId(dbController.insertStoreByName(updateStoreNamEditText.getText().toString()));
            String total = updateTotalEditText.getText().toString();
            // [ Remove $ from total]
            String totalNormalized = Normalizer.normalize(total, Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9]+", "");
            receipt.setTotal(Float.parseFloat(totalNormalized));
            receipt.setDate(updateDateEditText.getText().toString());
            receipt.setComment(updateCommentEditText.getText().toString());
            receipt.setPaymentMethod(updatePaymentEditText.getText().toString());
            receipt.setUrl(image);
            String receiptCategory = updateCategorySearchMultiSpinner.getSelectedItem().toString();
            if (!Objects.equals(receiptCategory, "Select Category")) {
                receipt.setCategoryId(dbController.getCategoryIdByName(receiptCategory));
            } else {
                //// TODO: 2016-07-13  make category spinner focused when on SELECT CATEGORY
                Toast.makeText(getApplicationContext(),
                        "Please choose a category", Toast.LENGTH_SHORT).show();
            }

            tags = updateTagSearchMultiSpinner.getAllTags();
        }


        dbController.updateReceipt(receipt, tags);
        dbController.close();

        Intent goToHomePage = new Intent(this, Home2Activity.class);
        startActivity(goToHomePage);
    }
}
