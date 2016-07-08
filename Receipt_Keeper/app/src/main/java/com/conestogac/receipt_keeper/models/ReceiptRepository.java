package com.conestogac.receipt_keeper.models;

import com.strongloop.android.loopback.ModelRepository;

public class ReceiptRepository extends ModelRepository<Receipt> {
    public ReceiptRepository() {
        super("Receipt", Receipt.class);
    }
}