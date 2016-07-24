package com.conestogac.receipt_keeper.uploader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.conestogac.receipt_keeper.Home2Activity;
import com.conestogac.receipt_keeper.R;
import com.conestogac.receipt_keeper.ReceiptCursorAdapter;
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

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ItemUploadTaskFragment extends Fragment {
    private static final String TAG = "ItemUploadTaskFragment";
    private Store store;
    private Receipt receipt;
    private Category category;
    private StoreCategory storeCategory;
    private Tag tag;
    private ReceiptTag receiptTag;

    private TagRepository tagRepository;
    private ReceiptRepository receiptRepository;
    private ReceiptTagRepository receiptTagRepository;
    private StoreRepository storeRepository;
    private CategoryRepository categoryRepository;
    private StoreCategoryRepository storeCategoryRepository;
    private String customerId;

    private ReceiptKeeperApplication app;
    private RestAdapter adapter;
    private CustomerRepository userRepo;
    private String remoteUrl;
    private String remoteName;
    private SQLController dbController;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private static final String PREFIX_REMOTE_IMAGE_PATH="/api/containers/";
    private static final String POSTFIX_REMOTE_IMAGE_PATH="/download/";

    private static final int CALLBACK_LOGIN = 1;
    private static final int CALLBACK_UPLOAD_STORE = 2;
    private static final int CALLBACK_UPLOAD_CATEGORY = 3;
    private static final int CALLBACK_UPLOAD_STORE_CATEGORY = 4;
    private static final int CALLBACK_UPLOAD_TAG = 5;
    private static final int CALLBACK_UPLOAD_RECEIPT = 6;
    private static final int CALLBACK_UPLOAD_RECEIPT_TAG = 7;
    private static final int CALLBACK_UPLOAD_IMAGE = 9;

    //define callback interface, callback implemented at caller;NewPostActivity class
    public interface TaskCallbacks {
        //To check: void onBitmapResized(Bitmap resizedBitmap, int mMaxDimension);
        void onItemUploaded(String error);
    }

    private Context mApplicationContext;
    private TaskCallbacks mCallbacks;

    //default contructor
    public ItemUploadTaskFragment() {
        // Required empty public constructor
    }

    //create static to survive after exit
    public static ItemUploadTaskFragment newInstance() {
        return new ItemUploadTaskFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    //when fragement is called, set context
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TaskCallbacks) {
            mCallbacks = (TaskCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TaskCallbacks");
        }
        mApplicationContext = context.getApplicationContext();
        app = (ReceiptKeeperApplication)getActivity().getApplication();
        adapter = app.getLoopBackAdapter();
        userRepo = adapter.createRepository(CustomerRepository.class);
        dbController = new SQLController(context);
        dbController.open();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        dbController.close();
    }

    /*
           Called from ListPostActivity for uploading
           It will call async task, UploadPostTask for uploading picture and set database
    */
    public void uploadItem() {
        //todo upload should be occur in background if possivle implemnted as service by pended intent uisng broadcast receiver
        uploadAll();
    }


    //Todo Implement Guava NonSynchronous Blocking
    public void uploadAll() {
        uploadImages();
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
        customerId = userRepo.getCurrentUserId().toString();
        return true;
    }

    private void silentLogin(String email, String password, final int callbackId) {
        //Login
        userRepo.loginUser(email , password
                , new CustomerRepository.LoginCallback() {
                    @Override
                    public void onSuccess(AccessToken token, Customer currentUser) {
                        customerId = currentUser.getId().toString();
                        app.setCurrentUser(currentUser);
                        Log.d(TAG, "current user's token:Id "+token.getUserId() + ":" + currentUser.getId());
                        switch (callbackId) {
                            case CALLBACK_UPLOAD_STORE:
                                uploadStore();
                                break;
                            case CALLBACK_UPLOAD_RECEIPT:
                                uploadReceipt();
                                break;
                            case CALLBACK_UPLOAD_CATEGORY:
                                uploadCategory();
                                break;
                            case CALLBACK_UPLOAD_STORE_CATEGORY:
                                uploadStoreCategory();
                                break;
                            case CALLBACK_UPLOAD_TAG:
                                uploadTag();
                                break;
                            case CALLBACK_UPLOAD_RECEIPT_TAG:
                                uploadReceiptTag();
                                break;
                            case CALLBACK_UPLOAD_IMAGE:
                                uploadImages();
                            default:
                                break;
                        }
                    }
                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "Login E", t);
                        gotoHome(getString(R.string.save_fail_login_message));
                    }
                });
    }


    /*
            Uploading
            @input: Read picture file with reference to local DB's image file path
            @operation: uploading file with user ID container name
            @output: get uploaded file name and path -> set remote DB's image file path
    */
    public void uploadImages()
    {
        if (!isLogin(CALLBACK_UPLOAD_IMAGE))
            return;


        final Cursor cursor = dbController.getAllReceiptDontHaveRemoteImage();
        if ((cursor == null) || (cursor.getCount()==0)) {
            uploadTag();
            return; //already all uploaded
        }

        Log.d(TAG, "#of images to upload:"+cursor.getCount());
        cursor.moveToFirst();

        final ContainerRepository containerRepo = adapter.createRepository(ContainerRepository.class);
        containerRepo.get(userRepo.getCurrentUserId().toString(),new ObjectCallback<Container>() {
            @Override
            public void onSuccess(Container container) {
                // container was exist
                Log.d(TAG, "Container Getting Exist One Success");
                uploadFile(container, cursor);
            }

            @Override
            public void onError(Throwable error) {
                // request failed
                // if Container does not exist
                // Todo Remote Receipt ID should be used for Container
                containerRepo.create((String) userRepo.getCurrentUserId(), new ObjectCallback<Container>() {
                    @Override
                    public void onSuccess(Container container) {
                        Log.d(TAG, "Container Creation Success");
                        // container was created
                        // same as container.getFileRepository().upload(fileName,...);
                        uploadFile(container, cursor);
                    }

                    @Override
                    public void onError(Throwable error) {
                        // create container request failed
                        Log.d(TAG, "Container Creation Failed");
                        gotoHome(getString(R.string.save_fail_image_message));
                    }
                });
            }
        });

    }

    /*
        Upload file using container
     */
    private void uploadFile(final Container container, final Cursor cursor) {
        String state;
        java.io.File file = null;
        FileInputStream fileInputStream = null;
        byte[] bFile = null;
        String fullPathAndFilename = "";

        if (cursor.isAfterLast()) {
            uploadTag();
            return;  //last condition
        }

        state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            fullPathAndFilename = cursor.getString(cursor.getColumnIndex(DBHelper.RECEIPT_URL));
            file = new java.io.File(fullPathAndFilename);

            //create byte array stream
            //change into byte array to upload
            bFile = new byte[(int) file.length()];

            try {
                //convert file into array of bytes
                fileInputStream = new FileInputStream(file);
                fileInputStream.read(bFile);
                fileInputStream.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            Log.d(TAG, "file handler: "+file.getAbsolutePath());
            container.upload("receipt.bmp",bFile,"image/bmp", new ObjectCallback<File>() {
                @Override
                public void onSuccess(File remoteFile) {
                    // localFile was uploaded
                    // call `remoteFile.getUrl()` to get its URL
                    remoteName = remoteFile.getName();
                    remoteUrl = PREFIX_REMOTE_IMAGE_PATH + customerId
                            + POSTFIX_REMOTE_IMAGE_PATH + remoteName;
                    dbController.setRemoteUrlReceipt(
                            cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.RECEIPT_ID)),
                            remoteUrl);

                    Log.d(TAG, "Success Uploading: "+remoteUrl);
                    if (!cursor.isAfterLast()) {
                        cursor.moveToNext();
                        uploadFile(container, cursor);
                    }
                    else {
                        Log.d(TAG, "Success Uploading All Files Finished:");
                        uploadTag();
                    }
                }

                @Override
                public void onError(Throwable error) {
                    // upload failed
                    Log.d(TAG, "Uploading Failed");
                    gotoHome(getString(R.string.save_fail_image_message));
                }
            });
        } else {
            Log.d(TAG, "ERROR at Mounting");
            gotoHome(getString(R.string.save_fail_image_message));
        }
    }




    /*
          Tag
    */
    private void uploadTag() {
        final String customerID;

        if (!isLogin(CALLBACK_UPLOAD_TAG))
            return;

        customerID = (String) userRepo.getCurrentUserId();
        tagRepository = adapter.createRepository(TagRepository.class);
        Cursor cursor = dbController.getAllUnSyncTag();

        if ((cursor == null) || (cursor.getCount()==0)){
            uploadStore();
            return;  //already all synced
        }

        Log.d(TAG, "#of Tag:"+cursor.getCount());
        cursor.moveToFirst();
        saveTag(cursor, customerID);
    }

    public void saveTag(final Cursor cursor, final String customerID) {
        if (cursor.isAfterLast()) {
            uploadStore();
            return;
        }

        tag  = tagRepository.createObject(ImmutableMap.of(
                "name", cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TAG_NAME)),
                "customerId", customerID,
                "groupId", ""));

        tag.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                // Success
                Log.d(TAG, "Success Save Tag:"+ tag.getId());
                dbController.setSyncedTag(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.TAG_ID)),
                        tag.getId().toString());

                if (!cursor.isAfterLast()) {
                    cursor.moveToNext();
                    saveTag(cursor, customerID);
                }
                else {
                    Log.d(TAG, "Success Tag Finished:");
                    uploadStore();
                }
            }

            @Override
            public void onError(Throwable t) {
                // save failed, handle the error
                Log.e(TAG, "Saving E", t);
                gotoHome(getString(R.string.save_fail_save_message));
            }
        });
    }


    /*
            Store
    */
    private void uploadStore() {
        final String customerID;

        if (!isLogin(CALLBACK_UPLOAD_STORE))
            return;

        customerID = (String) userRepo.getCurrentUserId();
        storeRepository = adapter.createRepository(StoreRepository.class);
        Cursor cursor = dbController.getAllUnSyncStore();

        if ((cursor == null) || (cursor.getCount() == 0)){
            uploadCategory();
            return;  //already all synced
        }

        Log.d(TAG, "#of Store:"+cursor.getCount());
        cursor.moveToFirst();
        saveStore(cursor, customerID);
    }

    public void saveStore(final Cursor cursor, final String customerID) {
        if (cursor.isAfterLast()) {
            uploadCategory();
            return;
        }

        store  = storeRepository.createObject(ImmutableMap.of(
                "name", cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.STORE_NAME)),
                "customerId", customerID,
                "groupId", ""));

        store.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                // Success
                Log.d(TAG, "Success Save Store:"+ store.getId());
                dbController.setSyncedStore(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.STORE_ID)),
                        store.getId().toString());

                if (!cursor.isAfterLast()) {
                    cursor.moveToNext();
                    saveStore(cursor, customerID);
                }
                else {
                    Log.d(TAG, "Success Store Finished:");
                    uploadCategory();
                }
            }

            @Override
            public void onError(Throwable t) {
                // save failed, handle the error
                Log.e(TAG, "Saving E", t);
                gotoHome(getString(R.string.save_fail_save_message));
            }
        });
    }

    /*
      Category
 */
    private void uploadCategory() {
        final String customerID;

        if (!isLogin(CALLBACK_UPLOAD_CATEGORY))
            return;

        customerID = (String) userRepo.getCurrentUserId();
        categoryRepository = adapter.createRepository(CategoryRepository.class);
        Cursor cursor = dbController.getAllUnSyncedCategory();

        if ((cursor == null) || (cursor.getCount()==0)){
            uploadStoreCategory();
            return;  //already all synced, so go to next item
        }

        Log.d(TAG, "#of Category:"+cursor.getCount());
        cursor.moveToFirst();
        saveCategory(cursor, customerID);
    }

    public void saveCategory(final Cursor cursor, final String customerID) {
        if (cursor.isAfterLast()) {
            uploadStoreCategory();
            return;
        }

        category  = categoryRepository.createObject(ImmutableMap.of(
                "name", cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.CATEGORY_NAME)),
                "customerId", customerID,
                "groupId", ""));

        category.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                // Success
                Log.d(TAG, "Success Save Category:"+ category.getId());
                dbController.setSyncedCategory(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CATEGORY_ID)),
                        category.getId().toString());

                if (!cursor.isAfterLast()) {
                    cursor.moveToNext();
                    saveCategory(cursor, customerID);
                }
                else {
                    Log.d(TAG, "Success Category Finished:");
                    uploadStoreCategory();
                }
            }

            @Override
            public void onError(Throwable t) {
                // save failed, handle the error
                Log.e(TAG, "Saving E", t);
                gotoHome(getString(R.string.save_fail_save_message));
            }
        });
    }


    /*
     StoreCategory
    */
    private void uploadStoreCategory() {
        final String customerID;

        if (!isLogin(CALLBACK_UPLOAD_STORE_CATEGORY))
            return;

        customerID = (String) userRepo.getCurrentUserId();
        storeCategoryRepository = adapter.createRepository(StoreCategoryRepository.class);
        Cursor cursor = dbController.getAllUnSyncedStoreCategory();

        if ((cursor == null) || (cursor.getCount()==0)){
            uploadReceipt();
            return;  //already all synced
        }

        Log.d(TAG, "#of StoreCategory:"+cursor.getCount());
        cursor.moveToFirst();
        saveStoreCategory(cursor, customerID);
    }

    public void saveStoreCategory(final Cursor cursor, final String customerID) {
        if (cursor.isAfterLast()) {
            uploadReceipt();
            return;
        }

        storeCategory  = storeCategoryRepository.createObject(ImmutableMap.of(
                "storeId", dbController.getStoreRemoteId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.STORE_CATEGORY_FK_STORE_ID))),
                "categoryId", dbController.getCategoryRemoteId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID))),
                "customerId", customerID,
                "groupId", ""));

        storeCategory.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                // Success
                Log.d(TAG, "Success Save StoreCategory:"+ storeCategory.getId());
                dbController.setSyncedStoreCategory(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.STORE_CATEGORY_FK_STORE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.STORE_CATEGORY_FK_CATEGORY_ID)),
                        storeCategory.getId().toString());

                if (!cursor.isAfterLast()) {
                    cursor.moveToNext();
                    saveStoreCategory(cursor, customerID);
                }
                else {
                    Log.d(TAG, "Success StoreCategory Finished:");
                    uploadReceipt();
                }
            }

            @Override
            public void onError(Throwable t) {
                // save failed, handle the error
                Log.e(TAG, "Saving E", t);
                gotoHome(getString(R.string.save_fail_save_message));
            }
        });
    }


    /*
           Receipt
    */
    private void uploadReceipt() {
        final String customerID;

        if (!isLogin(CALLBACK_UPLOAD_RECEIPT))
            return;
        customerID = (String) userRepo.getCurrentUserId();
        receiptRepository = adapter.createRepository(ReceiptRepository.class);
        Cursor cursor = dbController.getAllUnSyncReceipt();
        if ((cursor == null) || (cursor.getCount()==0)){
            uploadReceiptTag();
            return;  //already all synced
        }

        Log.d(TAG, "#of Receipt:"+cursor.getCount());
        cursor.moveToFirst();
        saveReceipt(cursor, customerID);
    }

    public void saveReceipt(final Cursor cursor, final String customerID) {
        if (cursor.isAfterLast()){
            uploadReceiptTag();
            return;
        }
        Date currentDate = new Date();
        receipt  = receiptRepository.createObject(ImmutableMap.of(
                "total", cursor.getFloat(cursor.getColumnIndexOrThrow(DBHelper.RECEIPT_TOTAL)),
                "customerId", customerID,
                "numberOfItem", 3,
                "storeId", dbController.getStoreRemoteId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.RECEIPT_FK_STORE_ID))),
                "groupId", ""));
        receipt.setImageFilePath(cursor.getString(cursor.getColumnIndex(DBHelper.RECEIPT_REMOTE_URL)));
        receipt.setComment(cursor.getString(cursor.getColumnIndex(DBHelper.RECEIPT_COMMENT)));
        receipt.setCategoryId(dbController.getCategoryRemoteId(cursor.getInt(cursor.getColumnIndex(DBHelper.RECEIPT_FK_CATEGORY_ID))));
        //todo string -> date format
        try {
            receipt.setDate(ReceiptCursorAdapter.sdf_user.parse(cursor.getString(cursor.getColumnIndex(DBHelper.RECEIPT_DATE))));
        } catch (Exception e) {
            receipt.setDate(new Date());
        }

        // storeId, categoryId,comment, date, tagId;

        receipt.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                // Success
                Log.d(TAG, "Success Save RECEIPT:"+ receipt.getId());
                dbController.setAsSyncedReceipt(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.RECEIPT_ID)),
                        receipt.getId().toString());

                if (!cursor.isAfterLast()) {
                    cursor.moveToNext();
                    saveReceipt(cursor, customerID);
                }
                else {
                    Log.d(TAG, "Success RECEIPT Finished:");
                    uploadReceiptTag();
                }
            }

            @Override
            public void onError(Throwable t) {
                // save failed, handle the error
                Log.e(TAG, "Saving E", t);
                gotoHome(getString(R.string.save_fail_save_message));
            }
        });
    }


    /*
       ReceiptTag
    */
    private void uploadReceiptTag() {
        final String customerID;

        if (!isLogin(CALLBACK_UPLOAD_RECEIPT_TAG))
            return;

        customerID = (String) userRepo.getCurrentUserId();
        receiptTagRepository = adapter.createRepository(ReceiptTagRepository.class);
        Cursor cursor = dbController.getAllUnSyncReceiptTag();

        if ((cursor == null) || (cursor.getCount()==0)){
            //Todo After uploading all, finish progress bar
            Log.d(TAG, "Finish uploading & Dismiss Progress Dialog "+cursor.getCount());

            gotoHome(getString(R.string.save_success_message));
            return;  //already all synced
        }

        Log.d(TAG, "#of ReceiptTag:"+cursor.getCount());
        cursor.moveToFirst();
        saveReceiptTag(cursor, customerID);
    }

    public void saveReceiptTag(final Cursor cursor, final String customerID) {
        if (cursor.isAfterLast()) {
            //Todo After uploading all, finish progress bar

            gotoHome(getString(R.string.save_success_message));
            return;
        }

        receiptTag  = receiptTagRepository.createObject(ImmutableMap.of(
                "receiptId", dbController.getReceiptRemoteId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.RECEIPT_TAG_FK_RECEIPT_ID))),
                "tagId", dbController.getTagRemoteId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.RECEIPT_TAG_FK_TAG_ID))),
                "customerId", customerID,
                "groupId", ""));

        receiptTag.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                // Success
                Log.d(TAG, "Success Save Tag:"+ receiptTag.getId());
                dbController.setSyncedReceiptTag(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.RECEIPT_TAG_FK_RECEIPT_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.RECEIPT_TAG_FK_TAG_ID)),
                        receiptTag.getId().toString());

                if (!cursor.isAfterLast()) {
                    cursor.moveToNext();
                    saveReceiptTag(cursor, customerID);
                }
                else {
                    Log.d(TAG, "Success ReceiptTag Finished:");
                    gotoHome(getString(R.string.save_success_message));
                }
            }

            @Override
            public void onError(Throwable t) {
                // save failed, handle the error
                Log.e(TAG, "Saving E", t);
                gotoHome(getString(R.string.save_fail_save_message));
            }
        });
    }

    void gotoHome(String ret_msg) {
        mCallbacks.onItemUploaded(ret_msg);
    }

}

