package edu.sfsu.cs.orange.ocr;

import android.widget.Toast;

/**
 * Created by Nicholas on 2016-06-15.
 */

public class GatheredData {
    private String _storeName;
    private String _amount;

    public void setStoreName(String enteredStoreName) {
        _storeName = enteredStoreName;
    }

    public String getStoreName() {
        return _storeName;
    }

    public void setAmount(String enteredAmount) {
        _storeName = enteredAmount;

    }

    public String getAmount() {
        return _amount;
    }
}