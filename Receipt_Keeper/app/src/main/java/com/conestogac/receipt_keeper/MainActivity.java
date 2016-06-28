package com.conestogac.receipt_keeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.conestogac.receipt_keeper.ocr.CaptureActivity;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PREF_IS_FIRST_RUN = "firstRun";
    private SharedPreferences prefs;


    private static final String[] PERMS_TAKE_PICTURE = {
            CAMERA,
            WRITE_EXTERNAL_STORAGE
    };

    private static final int RESULT_PERMS_INITIAL = 1339;

    private Button goToOcrButton;
    private Button goToLoginPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        if (isFirstRun()) {
            ActivityCompat.requestPermissions(this, PERMS_TAKE_PICTURE,
                    RESULT_PERMS_INITIAL);
        }

        goToOcrButton = (Button) findViewById(R.id.goToOcrButton);
        goToOcrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOcrCaptureActivity();
            }
        });


        goToLoginPage = (Button) findViewById(R.id.goToLoginPage);
        goToLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHomeActivity();
            }
        });

    }

    private void goToHomeActivity() {
        Intent goToHomeActivity = new Intent(this, HomeActivity.class);
        startActivity(goToHomeActivity);
    }

    private void goToOcrCaptureActivity() {
        Intent ocrIntent = new Intent(this, CaptureActivity.class);
        startActivity(ocrIntent);
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
