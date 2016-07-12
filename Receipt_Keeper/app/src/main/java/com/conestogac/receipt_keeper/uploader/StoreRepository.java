package com.conestogac.receipt_keeper.uploader;

import com.conestogac.receipt_keeper.uploader.Store;
import com.strongloop.android.loopback.ModelRepository;

/**
 * Created by infomat on 16-07-11.
 */
public class StoreRepository extends ModelRepository<Store> {
    public StoreRepository() {
        super("Store", Store.class);
    }
}