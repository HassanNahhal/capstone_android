package com.conestogac.receipt_keeper.authenticate;

/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.conestogac.receipt_keeper.R;
import com.conestogac.receipt_keeper.ocr.CaptureActivity;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private static final String[] PERMS_TAKE_PICTURE = {
            CAMERA,
            WRITE_EXTERNAL_STORAGE
    };
    private static final int RESULT_PERMS_INITIAL = 1339;
    private static final String PREF_IS_FIRST_RUN = "firstRun";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_up_button).setOnClickListener(this);

        //todo load customer infomation saved

        //Load camera related shared preference
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (isFirstRun()) {
            ActivityCompat.requestPermissions(this, PERMS_TAKE_PICTURE,
                    RESULT_PERMS_INITIAL);
        }

        //todo incase of user already login, goto OCR directly
        if (false){//.getCurrentUserId() != null) {
            Intent captureIntent = new Intent(this, CaptureActivity.class);
            //to prevent user back to this activity
            captureIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(captureIntent);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.sign_up_button:
                Intent signUpIntent = new Intent(this, UserProfileActivity.class);
                signUpIntent.putExtra(UserProfileActivity.PROFILE_MODE_EXTRA_NAME,UserProfileActivity.MODE_SIGNUP);
                startActivity(signUpIntent);
                break;
            case R.id.sign_in_button:
                Intent signInIntent = new Intent(this, UserProfileActivity.class);
                signInIntent.putExtra(UserProfileActivity.PROFILE_MODE_EXTRA_NAME,UserProfileActivity.MODE_SIGNIN);
                startActivity(signInIntent);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO
    }

    private boolean isFirstRun() {
        boolean result = prefs.getBoolean(PREF_IS_FIRST_RUN, true);
        if (result) {
            prefs.edit().putBoolean(PREF_IS_FIRST_RUN, false).apply();
        }
        return (result);
    }
}
