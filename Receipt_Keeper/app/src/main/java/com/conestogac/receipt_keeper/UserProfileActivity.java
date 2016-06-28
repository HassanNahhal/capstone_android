package com.conestogac.receipt_keeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.conestogac.receipt_keeper.ocr.CaptureActivity;
import com.google.common.collect.ImmutableBiMap;
import com.strongloop.android.loopback.AccessToken;
import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.User;
import com.strongloop.android.loopback.UserRepository;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends BaseActivity {
    private static final String TAG = UserProfileActivity.class.getSimpleName();
    public static final String PROFILE_MODE_EXTRA_NAME = "profile_mode";
    public static final Integer MODE_SIGNIN = 1;
    public static final Integer MODE_SIGNUP = 2;

    private EditText mEmailView;
    private EditText mPasswordView;

    private int profile_mode;
    private EditText mUsernameEdiText;

    private ReceiptKeeperApplication app;
    private RestAdapter adapter;
    private CustomerRepository userRepo;
    private Customer user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mUsernameEdiText  = (EditText) findViewById(R.id.username);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        profile_mode = getIntent().getExtras().getInt(PROFILE_MODE_EXTRA_NAME);

        //Init loopback objects
        // 1. Grab the shared RestAdapter instance.
        app = (ReceiptKeeperApplication)this.getApplication();
        RestAdapter adapter = app.getLoopBackAdapter();
        // 2. Instantiate User Repository
        userRepo = adapter.createRepository(CustomerRepository.class);

        if (profile_mode == MODE_SIGNUP) {
            mUsernameEdiText.setVisibility(View.VISIBLE);
        }

        Button mSubmitButton = (Button) findViewById(R.id.submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profile_mode == MODE_SIGNUP) {
                    attemptSignup();
                } else {
                    attemptSignin();
                }
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
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

    private boolean isEmailValid(String email) {

        // For future reference
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }

    /*
    User has to sign in to use server
    */
    private void attemptSignin() {
        if (!validateForm()) {
            return;
        }

        showProgressDialog(getString(R.string.signin_progress_message));

        //Login
        userRepo.loginUser(mEmailView.getText().toString() , mPasswordView.getText().toString()
                , new CustomerRepository.LoginCallback() {
            @Override
            public void onSuccess(AccessToken token, Customer currentUser) {
                dismissProgressDialog();
                showResult(getString(R.string.signin_success_message) +" "+ mUsernameEdiText.getText().toString());
                Intent gotoOCR = new Intent(getApplicationContext(), CaptureActivity.class);
                startActivity(gotoOCR);
                finish();
                Log.d(TAG, "Goto OCR and current user's token:Id "+token.getUserId() + ":" + currentUser.getId());
            }
            @Override
            public void onError(Throwable t) {
                dismissProgressDialog();
                showResult(getString(R.string.sigin_fail_message));
                Log.e("Chatome", "Login E", t);
            }
        });
    }

    /*
        User has to sign up to use server
     */
    private void attemptSignup() {
        if (!validateForm()) {
            return;
        }

        //Setup Map to send additional user information
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", mUsernameEdiText.getText().toString());

        //Create User
        user = userRepo.createUser(mEmailView.getText().toString(),
                mPasswordView.getText().toString(), params);

        showProgressDialog(getString(R.string.signup_progress_message));
        user.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                showResult(getString(R.string.signup_success_message));
                attemptSignin();
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "Can not singup.", t);
                dismissProgressDialog();
                showResult(getString(R.string.signup_fail_message));
            }
        });
    }


    /*
        Toast Popup
     */
    void showResult(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /*
        Inherit Loopback User model
     */
    public static class Customer extends com.strongloop.android.loopback.User {
        private String username;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }

    /*
        Inherit Loopback UserRepository model
     */
    public static class CustomerRepository
            extends com.strongloop.android.loopback.UserRepository<Customer> {

        public interface LoginCallback
                extends com.strongloop.android.loopback.UserRepository.LoginCallback<Customer> {
        }

        public CustomerRepository() {
            super("Customer", null, Customer.class);
        }
    }



}
