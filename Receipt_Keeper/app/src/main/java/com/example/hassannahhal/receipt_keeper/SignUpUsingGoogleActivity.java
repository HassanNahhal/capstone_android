package com.example.hassannahhal.receipt_keeper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class SignUpUsingGoogleActivity extends AppCompatActivity {

    // ========================================================================================
    // ============================   About this Class       ==================================

    /* This class will be the class that runs after the user press Continue using Google button.
    */
    // ========================================================================================


    // ========================================================================================
    // ========================    Declaring attributes     ===================================


    // ========================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_using_google);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

}
