package com.conestogac.receipt_keeper.uploader;

/**
 * Created by infomat on 16-07-11.
 */
public class Store extends com.strongloop.android.loopback.Model {
    private String id;
    private String name;
    private String customerId;
    private String groupId;

    public Store() { }
        public String getName() {
            return name;
        }
        public String getCustomerId() {
            return this.customerId;
        }
        public String getGroupId() {
            return this.groupId;
        }

        public void setName(String name) {
            this.name = name;
        }
        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }
        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
}
