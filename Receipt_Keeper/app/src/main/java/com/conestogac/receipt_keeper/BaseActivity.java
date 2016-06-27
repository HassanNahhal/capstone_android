package com.conestogac.receipt_keeper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * BaseActivity to show progress bar which uses ProgressDialogFragment
 * Just inherit this activity and call showProgressDialog with message as argument
 * To dismiss just call dismissProgressDialog()
 */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG_DIALOG_FRAGMENT = "tagDialogFragment";

    protected void showProgressDialog(String message) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getExistingDialogFragment();
        if (prev == null) {
            ProgressDialogFragment fragment = ProgressDialogFragment.newInstance(message);
            fragment.show(ft, TAG_DIALOG_FRAGMENT);
        }
    }

    protected void dismissProgressDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getExistingDialogFragment();
        if (prev != null) {
            ft.remove(prev).commit();
        }
    }

    private Fragment getExistingDialogFragment() {
        return getSupportFragmentManager().findFragmentByTag(TAG_DIALOG_FRAGMENT);
    }
}