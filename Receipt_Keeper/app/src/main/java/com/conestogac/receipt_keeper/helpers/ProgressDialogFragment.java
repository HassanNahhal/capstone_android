package com.conestogac.receipt_keeper.helpers;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {
    private static final String KEY_TITLE = "title";

    public static ProgressDialogFragment newInstance(String text) {
        ProgressDialogFragment frag = new ProgressDialogFragment();
        frag.setCancelable(true);
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, text);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity(), getTheme());
        dialog.setMessage(getArguments().getString(KEY_TITLE));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
