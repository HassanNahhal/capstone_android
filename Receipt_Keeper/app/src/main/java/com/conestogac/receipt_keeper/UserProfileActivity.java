package com.conestogac.receipt_keeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class UserProfileActivity extends AppCompatActivity {
    public static final String PROFILE_MODE_EXTRA_NAME = "profile_mode";
    public static final Integer MODE_SIGNIN = 1;
    public static final Integer MODE_SIGNUP = 2;

    private int profile_mode;
    private EditText mUsernameEdiText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mUsernameEdiText  = (EditText) findViewById(R.id.username);

        profile_mode = getIntent().getExtras().getInt(PROFILE_MODE_EXTRA_NAME);

        if (profile_mode == MODE_SIGNUP) {
            mUsernameEdiText.setVisibility(View.VISIBLE);
        }

    }
}
