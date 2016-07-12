package com.conestogac.receipt_keeper.uploader;

/**
 * Created by infomat on 16-07-11.
 */
public class Store extends com.strongloop.android.loopback.Model {
    private String id;
    private String name;

    public Store() { }

        public String getId() {
            return this.id;
        }
        public String getName() {
            return name;
        }

        public void setId(String id) {
            this.id = id;
        }
        public void setName(String name) {
            this.name = name;
        }
}
