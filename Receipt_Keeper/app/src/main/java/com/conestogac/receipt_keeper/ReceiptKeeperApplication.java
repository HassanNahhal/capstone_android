package com.conestogac.receipt_keeper;

import com.strongloop.android.loopback.RestAdapter;
import android.app.Application;

import com.strongloop.android.loopback.User;
import com.strongloop.android.loopback.UserRepository;
import com.strongloop.android.remoting.adapters.RestContractItem;


/*
  To be accessible from all activity
 */
public class ReceiptKeeperApplication extends Application {
    RestAdapter adapter;
    UserProfileActivity.CustomerRepository currentUserRepo;
    UserProfileActivity.Customer user;

    public RestAdapter getLoopBackAdapter() {
        if (adapter == null) {
            // Instantiate the shared RestAdapter. In most circumstances,
            // you'll do this only once; putting that reference in a singleton
            // is recommended for the sake of simplicity.
            // However, some applications will need to talk to more than one
            // server - create as many Adapters as you need.

            adapter = new RestAdapter(
                    getApplicationContext(), "http://receipt-keeper.herokuapp.com/api");

        }
        return adapter;
    }

    public void setCurrentUser(UserProfileActivity.Customer user) {
        this.user = user;
    }

    public UserProfileActivity.Customer getCurrentUser() {
        return this.user;
    }
}
