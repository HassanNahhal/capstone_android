package com.conestogac.receipt_keeper.uploader;

import com.strongloop.android.loopback.ModelRepository;

/**
 * Created by infomat on 16-07-22.
 */

public class ReceiptTagRepository extends ModelRepository<ReceiptTag> {
    public ReceiptTagRepository() {
        super("ReceiptTag", ReceiptTag.class);
    }
}