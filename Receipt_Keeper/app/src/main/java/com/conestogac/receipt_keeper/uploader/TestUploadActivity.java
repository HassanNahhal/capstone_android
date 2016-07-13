package com.conestogac.receipt_keeper.uploader;

import android.database.Cursor;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.conestogac.receipt_keeper.R;
import com.conestogac.receipt_keeper.ReceiptKeeperApplication;
import com.conestogac.receipt_keeper.SQLController;
import com.conestogac.receipt_keeper.helpers.DBHelper;

import com.google.common.collect.ImmutableMap;
import com.strongloop.android.loopback.AccessToken;
import com.strongloop.android.loopback.Container;
import com.strongloop.android.loopback.ContainerRepository;
import com.strongloop.android.loopback.File;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TestUploadActivity extends AppCompatActivity {
    private static final String TAG = "TestUploadActivity";
    private Store store;
    private StoreRepository repository;
    private ReceiptKeeperApplication app;
    private RestAdapter adapter;
    private CustomerRepository userRepo;
    private String remoteUrl;
    static private Container container;
    private SQLController dbController;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private static final int CALLBACK_LOGIN = 1;
    private static final int CALLBACK_UPLOAD_STORE = 2;
    private static final int CALLBACK_UPLOAD_TAG = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_upload);

        app = (ReceiptKeeperApplication)this.getApplication();
        adapter = app.getLoopBackAdapter();
        userRepo = adapter.createRepository(CustomerRepository.class);
        dbController = new SQLController(this);
        dbController.open();
        //todo close some where
    }

    public void uploadImage(View view){
        if (userRepo.getCurrentUserId() == null) {
            showMessage("Need to signin to upload!!!");
            return;
        } else {
            final ContainerRepository containerRepo = adapter.createRepository(ContainerRepository.class);
            containerRepo.get((String) userRepo.getCurrentUserId(),new ObjectCallback<Container>() {
                        @Override
                        public void onSuccess(Container container) {
                            // container was exist
                            TestUploadActivity.container = container;
                            uploadFile(container);
                        }

                        @Override
                        public void onError(Throwable error) {
                            // if Container does not exist
                            containerRepo.create((String) userRepo.getCurrentUserId(), new ObjectCallback<Container>() {
                                @Override
                                public void onSuccess(Container container) {
                                    // container was created
                                    TestUploadActivity.container = container;
                                    // same as container.getFileRepository().upload(fileName,...);
                                    uploadFile(container);
                                }

                                @Override
                                public void onError(Throwable error) {
                                    // create container request failed
                                }
                            });
                        }
                    });
        }
    }

    /*
        Upload file using container
     */
    private void uploadFile(Container container) {
        String state;
        java.io.File file = null;
        java.io.File Root;
        java.io.File Dir;
        state = Environment.getExternalStorageState();

        String fullPathAndFilename = "";
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Root = Environment.getExternalStorageDirectory();
            Dir = new java.io.File(Root.getAbsolutePath() + "/Expensify");
            file = new java.io.File(Dir, "1.jpg");
            Log.d(TAG, "file handler: "+file.getAbsolutePath());
            container.upload(file, new ObjectCallback<File>() {
                @Override
                public void onSuccess(File remoteFile) {
                    // localFile was uploaded
                    // call `remoteFile.getUrl()` to get its URL
                    //remoteUrl = remoteFile.getUrl();
                    //Log.d(TAG, remoteUrl);
                }

                @Override
                public void onError(Throwable error) {
                    // upload failed
                }
            });
        } else {
            Log.d(TAG, "ERROR at Mounting");
        }
    }

    /*
        upload receipt which is called by button click listener
     */
    public void uploadReceiptItem(View view) {
        if (isLogin(CALLBACK_LOGIN)) {
            saveReceipt();
        }
    }

    /*
      Save Receipt
     */
    private void testSaveReceipt() {
        Date currentDate = new Date();
        ReceiptRepository repository = adapter.createRepository(ReceiptRepository.class);


        Receipt receipt = repository.createObject(ImmutableMap.of(
                    "total", 120,
                    "numberOfItem", 3,
                    "imgaeFilePath", "http://localhost",
                    "date", dateFormat.format(currentDate),
                    "storeId", "577bc87cb1ac300300f6cfbe"
            )
        );

        Log.d(TAG, "Current User Id: "+userRepo.getCurrentUserId());
        receipt.setCustomerId((String) (userRepo.getCurrentUserId()));

        receipt.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                // Success
                showMessage("Success Save");
            }

            @Override
            public void onError(Throwable t) {
                // save failed, handle the error
                Log.e(TAG, "Saving E", t);
                showMessage(getString(R.string.save_fail_message));
            }
        });
    }

    /*
        Check Login
        If does not login, it will login background and will call save at the callback
        If it is return true to make caller can go proceed
     */
    private boolean isLogin(int callbacKId) {
        if (userRepo.getCurrentUserId() == null){
            silentLogin(app.getCurrentUser().getEmail(), app.getCurrentUser().getPassword(), callbacKId);
            return false;
        }
        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(TestUploadActivity.this, message,
                Toast.LENGTH_SHORT).show();
    }


    private void silentLogin(String email, String password, final int callbackId) {
        //Login
        userRepo.loginUser(email , password
                , new CustomerRepository.LoginCallback() {
                    @Override
                    public void onSuccess(AccessToken token, Customer currentUser) {
                        app.setCurrentUser(currentUser);
                        Log.d(TAG, "current user's token:Id "+token.getUserId() + ":" + currentUser.getId());
                        switch (callbackId) {
                            case CALLBACK_LOGIN:
                                saveReceipt();
                                break;
                            case CALLBACK_UPLOAD_STORE:
                                uploadStore();
                                break;
                            default:
                                break;
                        }
                    }
                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "Login E", t);
                        showMessage(getString(R.string.save_fail_message));
                    }
                });
    }


    public void createTestDB(View view) {
        long _id;

        dbController.open();
        com.conestogac.receipt_keeper.models.Receipt receipt = new com.conestogac.receipt_keeper.models.Receipt();
        final String image = "/storage/emulated/0/ReceiptKeeperFolder/2016_07_05_20_00_04.Receipt.bmp";

        for (int i = 0; i < 100; i++) {
            if (app.getCurrentUser().getId() != null)
                receipt.setCustomerId(app.getCurrentUser().getId().toString());
            receipt.setStoreId(dbController.insertStoreByName("Store"+ (i%4)));
            receipt.setCategoryId(i%5+1);
            receipt.setComment("Comment"+i);
            receipt.setDate("2016-07-08");
            receipt.setTotal((float)(10.0+i*10.0));
            receipt.setUrl(image);
            receipt.setPaymentMethod("Master");
            _id = dbController.insertReceipt(receipt, null);
            Log.d(TAG, "ID: "+_id);
        }

        dbController.close();
    }

    public void uploadSTore(View view) {
        uploadStore();
    }

    /*
     upload receipt which is called by button click listener
    */
    private void uploadStore() {
        String customerID;
        Cursor cursor;

        if (!isLogin(CALLBACK_UPLOAD_STORE))
            return;

        customerID = (String) userRepo.getCurrentUserId();
        cursor = dbController.getAllUnSyncStore();
        if (cursor==null) return;


        Log.d(TAG, "Number Store to upload: "+ cursor.getCount());
        repository = adapter.createRepository(StoreRepository.class);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            store = repository.createObject(ImmutableMap.of(
                    "name",cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.STORE_NAME))
            ));
            store.setCustomerId(customerID);
            store.setGroupId("");
            Log.d(TAG, "RestUrl: "+ repository.getNameForRestUrl());
            store.save(new VoidCallback() {
                @Override
                public void onSuccess() {
                    // Success
                    //todo set as synced and set rsync
                    //Todo How to set r_id and synced?
                    Log.d(TAG, "Success Save:"+ store.getId());
                }

                @Override
                public void onError(Throwable t) {
                    // save failed, handle the error
                    Log.e(TAG, "Saving E", t);
                }
            });
        }
    }

    /*
      Save Receipt
     */
    private void saveReceipt() {
        Date currentDate = new Date();
        ReceiptRepository repository = adapter.createRepository(ReceiptRepository.class);

        Receipt receipt = repository.createObject(ImmutableMap.of(
                "total", 120,
                "numberOfItem", 3,
                "imgaeFilePath", "http://localhost",
                "date", dateFormat.format(currentDate),
                "storeId", "577bc87cb1ac300300f6cfbe"
                )
        );

        Log.d(TAG, "Current User Id: "+userRepo.getCurrentUserId());
        receipt.setCustomerId((String) (userRepo.getCurrentUserId()));

        receipt.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                // Success
                showMessage("Success Save");
            }

            @Override
            public void onError(Throwable t) {
                // save failed, handle the error
                Log.e(TAG, "Saving E", t);
                showMessage(getString(R.string.save_fail_message));
            }
        });
    }

}
