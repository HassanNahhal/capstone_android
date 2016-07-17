package com.conestogac.receipt_keeper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.conestogac.receipt_keeper.helpers.GlideUtil;

import java.io.File;

public class ViewReceiptActivity extends AppCompatActivity {

    private TextView viewStoreNamTextView;
    private TextView viewTotalTextView;
    private TextView viewDateTextView;
    private TextView viewTagTextView;
    private TextView viewCategoryTextView;
    private TextView viewCommentTextView;
    private TextView viewPaymentTextView;
    private Button viewEditReceiptButton;
    private ImageButton viewReceiptImageButton;
    private String imagePath;


    private File file;
    private SQLController dbContoller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_receipt);


        dbContoller = new SQLController(this);


        // [ Setting IDs to Views ]
        viewStoreNamTextView = (TextView) findViewById(R.id.viewStoreNamTextView);
        viewTotalTextView = (TextView) findViewById(R.id.viewTotalTextView);
        viewDateTextView = (TextView) findViewById(R.id.viewDateTextView);
        viewTagTextView = (TextView) findViewById(R.id.viewTagTextView);
        viewCategoryTextView = (TextView) findViewById(R.id.viewCategoryTextView);
        viewCommentTextView = (TextView) findViewById(R.id.viewCommentTextView);
        viewPaymentTextView = (TextView) findViewById(R.id.viewPaymentTextView);
        viewEditReceiptButton = (Button) findViewById(R.id.viewEditReceiptButton);
        viewReceiptImageButton = (ImageButton) findViewById(R.id.viewReceiptImageButton);

        setAllDataFromIntent();

    }

    private void setAllDataFromIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String storeName = extras.getString("storeName");
            if (storeName != null) {
                viewStoreNamTextView.setText(storeName);
            }
            int total = extras.getInt("total");
            if (total != 0) {
                viewTotalTextView.setText("$" + String.valueOf(total));
            }

            String date = extras.getString("date");
            if (date != null) {
                viewDateTextView.setText(date);
            }

            String comment = extras.getString("comment");
            if (comment != null) {
                viewCommentTextView.setText(comment);
            }

            String paymentMethod = extras.getString("paymentMethod");
            if (paymentMethod != null) {
                viewPaymentTextView.setText(paymentMethod);
            }

            int categoryId = extras.getInt("categoryId");
            if (categoryId != 0) {
                dbContoller.open();
                String categoryName = dbContoller.getCategoryNameById(categoryId);
                if (categoryName != null) {
                    viewCategoryTextView.setText(categoryName);
                }
                dbContoller.close();
            }

            int tagId = extras.getInt("tagId");
            if (tagId != 0) {
                dbContoller.open();
                String tagName = dbContoller.getTagNameById(tagId);
                if (tagName != null) {
                    viewTagTextView.setText(tagName);
                }
                dbContoller.close();
            }

            String imagePath = extras.getString("imagePath");
            if (imagePath != null) {
                file = new File(imagePath);
                GlideUtil.loadImage(file, viewReceiptImageButton);
            }

        }
    }
}
