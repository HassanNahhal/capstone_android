package com.conestogac.receipt_keeper;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.conestogac.receipt_keeper.helpers.DBHelper;
import com.conestogac.receipt_keeper.helpers.GlideUtil;

import java.io.File;


public class ViewReceiptActivity extends AppCompatActivity implements View.OnClickListener {


    // [Views to set IDs to]
    private TextView viewStoreNamTextView;
    private TextView viewTotalTextView;
    private TextView viewDateTextView;
    private TextView viewTagTextView;
    private TextView viewCategoryTextView;
    private TextView viewCommentTextView;
    private TextView viewPaymentTextView;
    private ImageButton viewReceiptImageButton;
    private TextView viewReceiptIdTextView;

    // [ Intent values to send to UpdateReceiptActivity and receive from Home2Activity]
    String storeName;
    float total;
    String date;
    String comment;
    String paymentMethod;
    String categoryName;
    String tagName;
    String imagePath;
    int tagId;
    int categoryId;
    int receiptId;


    private File file;
    private SQLController dbController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_receipt);


        dbController = new SQLController(this);


        // [ Setting IDs to Views ]
        viewReceiptIdTextView = (TextView) findViewById(R.id.viewReceiptIdTextView);
        viewStoreNamTextView = (TextView) findViewById(R.id.viewStoreNamTextView);
        viewTotalTextView = (TextView) findViewById(R.id.viewTotalTextView);
        viewDateTextView = (TextView) findViewById(R.id.viewDateTextView);
        viewTagTextView = (TextView) findViewById(R.id.viewTagTextView);
        viewCategoryTextView = (TextView) findViewById(R.id.viewCategoryTextView);
        viewCommentTextView = (TextView) findViewById(R.id.viewCommentTextView);
        viewPaymentTextView = (TextView) findViewById(R.id.viewPaymentTextView);
        findViewById(R.id.viewEditReceiptButton).setOnClickListener(this);
        viewReceiptImageButton = (ImageButton) findViewById(R.id.viewReceiptImageButton);
        setAllDataFromIntent();

        //  Go to AddReceiptActivity when clicked
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.viewEditReceiptButton);
        fab.setImageResource(R.drawable.ic_mode_edit_white_24dp);
        findViewById(R.id.viewEditReceiptButton).setOnClickListener(this);


        viewReceiptImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent popIntent = new Intent(ViewReceiptActivity.this, Pop.class);

                if (imagePath != null) {
                    popIntent.putExtra("imagePath", imagePath);
                    popIntent.putExtra("POP_INFO", "Receit ID: " + String.valueOf(receiptId));
                }

                startActivity(popIntent);
            }
        });
    }

    private void setAllDataFromIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            receiptId = extras.getInt("receiptId");
            if (receiptId != 0) {
                viewReceiptIdTextView.setText("ID: " + String.valueOf(receiptId));
            }

            storeName = extras.getString("storeName");
            if (storeName != null) {
                viewStoreNamTextView.setText(storeName);
            }
            total = extras.getFloat("total");
            if (total != 0) {
                viewTotalTextView.setText(String.valueOf(total));
            }

            date = extras.getString("date");
            if (date != null) {
                viewDateTextView.setText(date);
            }

            comment = extras.getString("comment");
            if (comment != null) {
                viewCommentTextView.setText(comment);
            }

            paymentMethod = extras.getString("paymentMethod");
            if (paymentMethod != null) {
                viewPaymentTextView.setText(paymentMethod);
            }

            categoryId = extras.getInt("categoryId");
            if (categoryId != 0) {
                dbController.open();
                categoryName = dbController.getCategoryNameById(categoryId);
                if (categoryName != null) {
                    viewCategoryTextView.setText(categoryName);
                }
                dbController.close();
            }

            viewTagTextView.setText(getTagListAsString(receiptId));

            imagePath = extras.getString("imagePath");
            if (imagePath != null) {
                file = new File(imagePath);
                GlideUtil.loadImage(file, viewReceiptImageButton);
            }

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.viewEditReceiptButton:
                updateReceipt();
                break;
            default:
                break;

        }
    }

    private void updateReceipt() {

        Intent goToUpdateIntent = new Intent(this, UpdateReceiptActivity.class);
        goToUpdateIntent.putExtra("receiptId", receiptId);
        goToUpdateIntent.putExtra("storeName", storeName);
        goToUpdateIntent.putExtra("total", total);
        goToUpdateIntent.putExtra("date", date);
        goToUpdateIntent.putExtra("comment", comment);
        goToUpdateIntent.putExtra("paymentMethod", paymentMethod);
        goToUpdateIntent.putExtra("categoryId", categoryId);
        goToUpdateIntent.putExtra("imagePath", imagePath);

        startActivity(goToUpdateIntent);
    }

    private String getTagListAsString(int receiptId) {
        Cursor cursor;
        dbController.open();
        StringBuilder builder = new StringBuilder();
        cursor = dbController.getReceiptTagIds(receiptId);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            builder.append(cursor.getString(cursor.getColumnIndex(DBHelper.TAG_NAME)) + ",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        dbController.close();
        return builder.toString();
    }
}
