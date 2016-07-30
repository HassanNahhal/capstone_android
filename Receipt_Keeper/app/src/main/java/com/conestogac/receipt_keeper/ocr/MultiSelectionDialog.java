package com.conestogac.receipt_keeper.ocr;

/**
 * Created by Nicholas on 2016-06-15.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.view.Gravity;
import android.widget.Toast;

import com.conestogac.receipt_keeper.R;

import java.util.ArrayList;

public class MultiSelectionDialog extends DialogFragment {
    public String result = "";
    ArrayList<String> choices = new ArrayList<String>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String[] items = getResources().getStringArray(R.array.save_items);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Field Choices").setMultiChoiceItems(R.array.save_items, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    choices.add(items[which]);

                } else if (choices.contains(items[which])) {
                    choices.remove(items[which]);
                }
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String storeName = getArguments().getString("Store Name");
                String amount = getArguments().getString("Amount");
                String selected = getArguments().getString("selected");
                //Toast.makeText(getActivity(), "You chose:" + choices.get(0), Toast.LENGTH_SHORT).show();
                result = choices.get(0);

                if (result.equals("Store Name")) {
                    storeName = selected;
                }
                if (result.equals("Amount")) {
                    amount = selected;
                }
                String toastString = "Store Name:" + storeName + "\n" + "Amount:" + amount;

                Toast toast = Toast.makeText(getActivity(), toastString, Toast.LENGTH_SHORT);

                toast.show();
            }
        });

        // return super.onCreateDialog(savedInstanceState);
        return builder.create();
    }

    public String getResult() {
        return result;
    }
}