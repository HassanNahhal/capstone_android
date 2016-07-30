package com.conestogac.receipt_keeper.uploader;

/*
       Inherit Loopback UserRepository model
*/
public class CustomerRepository
        extends com.strongloop.android.loopback.UserRepository<Customer> {

    public interface LoginCallback
            extends com.strongloop.android.loopback.UserRepository.LoginCallback<Customer> {
    }

    public CustomerRepository() {
        super("Customer", null, Customer.class);
    }
}