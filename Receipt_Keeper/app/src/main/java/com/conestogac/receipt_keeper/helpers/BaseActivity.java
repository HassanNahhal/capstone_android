package com.conestogac.receipt_keeper.helpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/*
    This is wrapper class which will be used call the progress dialog
    ProgressDialogFragment shoudl be used together
 */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG_DIALOG_FRAGMENT = "tagDialogFragment";

    protected void showProgressDialog(String message) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getExistingDialogFragment();
        if (prev == null) {
            ProgressDialogFragment fragment = ProgressDialogFragment.newInstance(message);
            ft.addToBackStack(TAG_DIALOG_FRAGMENT);
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