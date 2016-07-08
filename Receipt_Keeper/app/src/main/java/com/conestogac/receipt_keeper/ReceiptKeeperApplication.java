package com.conestogac.receipt_keeper;

import com.conestogac.receipt_keeper.authenticate.UserProfileActivity;
import com.strongloop.android.loopback.RestAdapter;
import android.app.Application;
import com.conestogac.receipt_keeper.authenticate.UserProfileActivity.Customer;

/*
  To be accessible from all activity
 */
public class ReceiptKeeperApplication extends Application {
    RestAdapter adapter;
    UserProfileActivity.Customer user;

    public RestAdapter getLoopBackAdapter() {
        if (adapter == null) {
            // Instantiate the shared RestAdapter. In most circumstances,
            // you'll do this only once; putting that reference in a singleton
            // is recommended for the sake of simplicity.
            // However, some applications will need to talk to more than one
            // server - create as many Adapters as you need.

            adapter = new RestAdapter(
                    //getApplicationContext(), "http://192.168.2.22:3000/api");
                    getApplicationContext(), "http://receipt-keeper.herokuapp.com/api");

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
