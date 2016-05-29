package com.example.hassannahhal.receipt_keeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    // ========================================================================================
    // ============================   About this Class       ==================================

    /* This class will be the main class that runs after the app starts , all the initial
   deceleration such as the main page and its content will be implemented here.
   This class only includes where the user will move based on a specif choice*/
    // ========================================================================================


    // ========================================================================================
    // ========================    Declaring attributes     ===================================

    Button facebookLoginButton;
    Button googleLoginButton;


    // ========================================================================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    public void signUpUsingFacebook(View view) {
        Intent facebookIntent = new Intent(this, SignUpUsingFacebookActivity.class);
        facebookLoginButton = (Button) findViewById(R.id.facebookLoginButton);
        startActivity(facebookIntent);
    }


    public void signUpUsingGoogle(View view) {
        Intent googleIntent = new Intent(this, SignUpUsingGoogleActivity.class);
        googleLoginButton = (Button) findViewById(R.id.googleLoginButton);
        startActivity(googleIntent);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up googleSignUpbutton, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
