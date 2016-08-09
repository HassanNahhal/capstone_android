package com.conestogac.receipt_keeper;

import com.conestogac.receipt_keeper.authenticate.UserProfileActivity;
import com.conestogac.receipt_keeper.ocr.PreferencesActivity;
import com.strongloop.android.loopback.RestAdapter;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.conestogac.receipt_keeper.uploader.Customer;

/*
  To be accessible from all activity
 */
public class ReceiptKeeperApplication extends Application {
    RestAdapter adapter;
    static Customer user;

    public RestAdapter getLoopBackAdapter() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String hostAddress = prefs.getString("preference_host_address", "http://receipt-keeper.herokuapp.com");

        if (adapter == null) {
            // Instantiate the shared RestAdapter. In most circumstances,
            // you'll do this only once; putting that reference in a singleton
            // is recommended for the sake of simplicity.
            // However, some applications will need to talk to more than one
            // server - create as many Adapters as you need.

            final String restAddreess = hostAddress.concat("/api");
            adapter = new RestAdapter(
                    //getApplicationContext(), "http://192.168.123.117:3000/api");
                    getApplicationContext(), restAddreess);

        }
        return adapter;
    }

    public void setCurrentUser(Customer user) {
        this.user = user;
    }

    public Customer getCurrentUser() {
        return this.user;
    }
}
