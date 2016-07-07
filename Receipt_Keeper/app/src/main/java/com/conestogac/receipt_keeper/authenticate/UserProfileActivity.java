package com.conestogac.receipt_keeper.authenticate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.TaskStackBuilder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.conestogac.receipt_keeper.helpers.BaseActivity;
import com.conestogac.receipt_keeper.HomeActivity;
import com.conestogac.receipt_keeper.R;
import com.conestogac.receipt_keeper.ReceiptKeeperApplication;
import com.conestogac.receipt_keeper.ocr.CaptureActivity;
import com.strongloop.android.loopback.AccessToken;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends BaseActivity {
    private static final String TAG = UserProfileActivity.class.getSimpleName();
    public static final String PROFILE_MODE_EXTRA_NAME = "profile_mode";
    public static final String SHAREDPREF_KEY_EMAIL = "user_email";
    public static final String SHAREDPREF_KEY_PASSWORD = "user_password";
    public static final String SHAREDPREF_KEY_USERNAME = "user_name";
    public static final String SHAREDPREF_KEY_AUTOLOGIN = "auto_login";
    public static final Integer MODE_SIGNIN = 1;
    public static final Integer MODE_SIGNUP = 2;

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mUsernameView;
    private CheckBox mAutoLogin;
    private int profile_mode;
    private ReceiptKeeperApplication app;
    private RestAdapter adapter;
    private CustomerRepository userRepo;
    private Customer user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mUsernameView = (EditText) findViewById(R.id.username);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mAutoLogin = (CheckBox) findViewById(R.id.auto_login);
        profile_mode = getIntent().getExtras().getInt(PROFILE_MODE_EXTRA_NAME);
        //Todo Encrypt user info
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        //Init loopback objects
        // 1. Grab the shared RestAdapter instance.
        app = (ReceiptKeeperApplication)this.getApplication();
        RestAdapter adapter = app.getLoopBackAdapter();
        // 2. Instantiate User Repository
        userRepo = adapter.createRepository(CustomerRepository.class);

        if (profile_mode == MODE_SIGNUP) {
            mUsernameView.setVisibility(View.VISIBLE);
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
        mUsernameView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String username = mUsernameView.getText().toString();

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

        // Check username valid
        if (profile_mode == MODE_SIGNUP && !isUserNameValid(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
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

    private boolean isUserNameValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() > 0;
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
        params.put("firstName", mUsernameView.getText().toString());

        //Create User
        user = userRepo.createUser(mEmailView.getText().toString(),
                mPasswordView.getText().toString(), params);

        showProgressDialog(getString(R.string.signup_progress_message));
        user.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                showResult("Welcome ! " + mUsernameView.getText().toString());
                loginPrefsEditor.putString(SHAREDPREF_KEY_USERNAME, mUsernameView.getText().toString());
                loginPrefsEditor.putString(SHAREDPREF_KEY_EMAIL, mEmailView.getText().toString());
                loginPrefsEditor.putString(SHAREDPREF_KEY_PASSWORD, mPasswordView.getText().toString());
                loginPrefsEditor.putBoolean(SHAREDPREF_KEY_AUTOLOGIN, mAutoLogin.isChecked());
                loginPrefsEditor.commit();
                dismissProgressDialog();
                startActivity(new Intent(UserProfileActivity.this, HomeActivity.class));
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "Can not signup.", t);
                dismissProgressDialog();
                showResult(getString(R.string.signup_fail_message));
            }
        });
    }
    /*
        User has to sign in to use server
        Todo: This should be moved to Sync Package
    */
    private void attemptSignin() {
        String userEmail = loginPreferences.getString(UserProfileActivity.SHAREDPREF_KEY_EMAIL,"");
        String userPassword = loginPreferences.getString(UserProfileActivity.SHAREDPREF_KEY_PASSWORD,"");
        String username = loginPreferences.getString(UserProfileActivity.SHAREDPREF_KEY_USERNAME,"");

        if ((mEmailView.getText().toString().equals(userEmail)) &&
                (mPasswordView.getText().toString().equals(userPassword))) {
            loginPrefsEditor.putBoolean(SHAREDPREF_KEY_AUTOLOGIN, mAutoLogin.isChecked());
            loginPrefsEditor.commit();
            showResult("Welcome ! " + username);
            Intent homeIntent = new Intent(UserProfileActivity.this, HomeActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(homeIntent);
        } else {
            showResult("Please check email or password");
            //todo if user forgot password, there should reset password or send a newpassword to email
        }


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


    /**TODO****************/
    private void attemptSignin_() {

        showProgressDialog(getString(R.string.signin_progress_message));

        //Login
        userRepo.loginUser(mEmailView.getText().toString() , mPasswordView.getText().toString()
                , new CustomerRepository.LoginCallback() {
                    @Override
                    public void onSuccess(AccessToken token, Customer currentUser) {
                        dismissProgressDialog();
                        app.setCurrentUser(currentUser);

                        showResult(getString(R.string.signin_success_message) +" "+ currentUser.username);

                /* Todo Goto OCR*/
                        TaskStackBuilder.create(getApplicationContext())
                                .addParentStack(WelcomeActivity.class)
                                .addNextIntent(new Intent(getApplicationContext(), HomeActivity.class))
                                .addNextIntent(new Intent(getApplicationContext(), CaptureActivity.class))
                                .startActivities();

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


}
