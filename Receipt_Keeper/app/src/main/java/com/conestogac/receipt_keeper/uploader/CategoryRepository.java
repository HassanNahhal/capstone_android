package com.conestogac.receipt_keeper.uploader;

import com.strongloop.android.loopback.ModelRepository;

/**
 * Created by infomat on 16-07-11.
 */
public class CategoryRepository extends ModelRepository<Category> {
    public CategoryRepository() {
        super("Category", Category.class);
    }
}