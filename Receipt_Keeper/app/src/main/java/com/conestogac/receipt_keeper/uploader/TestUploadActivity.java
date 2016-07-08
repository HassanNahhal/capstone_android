package com.conestogac.receipt_keeper.uploader;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.conestogac.receipt_keeper.R;
import com.conestogac.receipt_keeper.ReceiptKeeperApplication;
import com.conestogac.receipt_keeper.authenticate.UserProfileActivity.CustomerRepository;
import com.conestogac.receipt_keeper.models.Receipt;
import com.conestogac.receipt_keeper.models.ReceiptRepository;

import com.google.common.collect.ImmutableMap;
import com.strongloop.android.loopback.Container;
import com.strongloop.android.loopback.ContainerRepository;
import com.strongloop.android.loopback.File;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import java.util.Date;

public class TestUploadActivity extends AppCompatActivity {
    private static final String TAG = "TestUploadActivity";
    private ReceiptKeeperApplication app;
    private RestAdapter adapter;
    private CustomerRepository userRepo;
    private String remoteUrl;
    static private Container container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_upload);

        app = (ReceiptKeeperApplication)this.getApplication();
        adapter = app.getLoopBackAdapter();
        userRepo = adapter.createRepository(CustomerRepository.class);
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

    public void uploadReceiptItem(View view) {
        Date currentDate = new Date();
        ReceiptRepository repository = adapter.createRepository(ReceiptRepository.class);
        Receipt receipt = repository.createObject(ImmutableMap.of(
                    "total", 120,
                    "numberOfItem", 3,
                    "imgaeFilePath", "http://localhost",
                    "date", currentDate,
                    "storeId", "577bc87cb1ac300300f6cfbe"
            )
        );

        Log.d(TAG, "Current User Id: "+userRepo.getCurrentUserId());
        receipt.setCustomerRemoteId((String) (userRepo.getCurrentUserId()));

        receipt.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                // Success
                showMessage("Success Save");
            }

            @Override
            public void onError(Throwable t) {
                // save failed, handle the error
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(TestUploadActivity.this, message,
                Toast.LENGTH_SHORT).show();
    }


    /**TODO****************/
//    private void attemptSignin_() {
//
//        showProgressDialog(getString(R.string.signin_progress_message));
//
//        //Login
//        userRepo.loginUser(mEmailView.getText().toString() , mPasswordView.getText().toString()
//                , new CustomerRepository.LoginCallback() {
//                    @Override
//                    public void onSuccess(AccessToken token, Customer currentUser) {
//                        dismissProgressDialog();
//                        app.setCurrentUser(currentUser);
//
//                        showResult(getString(R.string.signin_success_message) +" "+ currentUser.username);
//
//                /* Todo Goto OCR*/
//                        TaskStackBuilder.create(getApplicationContext())
//                                .addParentStack(WelcomeActivity.class)
//                                .addNextIntent(new Intent(getApplicationContext(), HomeActivity.class))
//                                .addNextIntent(new Intent(getApplicationContext(), CaptureActivity.class))
//                                .startActivities();
//
//                        finish();
//                        Log.d(TAG, "Goto OCR and current user's token:Id "+token.getUserId() + ":" + currentUser.getId());
//                    }
//                    @Override
//                    public void onError(Throwable t) {
//                        dismissProgressDialog();
//                        showResult(getString(R.string.sigin_fail_message));
//                        Log.e("Chatome", "Login E", t);
//                    }
//                });
//    }

}
