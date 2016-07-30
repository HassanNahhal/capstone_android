package com.conestogac.receipt_keeper.uploader;

import com.conestogac.receipt_keeper.uploader.Receipt;
import com.strongloop.android.loopback.ModelRepository;

public class ReceiptRepository extends ModelRepository<Receipt> {
    public ReceiptRepository() {
        super("Receipt", Receipt.class);
    }
}