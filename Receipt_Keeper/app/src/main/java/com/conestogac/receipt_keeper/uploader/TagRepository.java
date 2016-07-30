package com.conestogac.receipt_keeper.uploader;

import com.strongloop.android.loopback.ModelRepository;
import com.conestogac.receipt_keeper.uploader.Tag;

/**
 * Created by infomat on 16-07-11.
 */
public class TagRepository extends ModelRepository<Tag> {
    public TagRepository() {
        super("Tag", Tag.class);
    }
}