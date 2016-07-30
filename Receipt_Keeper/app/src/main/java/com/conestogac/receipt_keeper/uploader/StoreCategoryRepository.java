package com.conestogac.receipt_keeper.uploader;

import com.strongloop.android.loopback.ModelRepository;

/**
 * Created by infomat on 16-07-22.
 */
public class StoreCategoryRepository  extends ModelRepository<StoreCategory> {
    public StoreCategoryRepository() {
        super("StoreCategory", StoreCategory.class);
    }
}