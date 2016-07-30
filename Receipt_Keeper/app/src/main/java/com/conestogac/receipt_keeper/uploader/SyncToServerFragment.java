package com.conestogac.receipt_keeper.uploader;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.conestogac.receipt_keeper.R;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


/**
 * This call is a fragment for processing uploading new post
 * It will be run as Async Task for non blocking UI thread and
 * Call back onPostUploaded will be called after finishing or error is occurred.
 */
public class SyncToServerFragment extends Fragment {
    private static final String TAG = "SyncToServerFragment";

    public interface SyncCallbacks {
        void onDataUploaded(String error);
    }

    private Context mApplicationContext;
    private SyncCallbacks mCallbacks;

    //default contructor
    public SyncToServerFragment() {
        // Required empty public constructor
    }

    //create static to survive after exit
    public static SyncToServerFragment newInstance() {
        return new SyncToServerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across config changes.
        setRetainInstance(true);
    }
    //when fragement is called, set context
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SyncCallbacks) {
            mCallbacks = (SyncCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TaskCallbacks");
        }
        mApplicationContext = context.getApplicationContext();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /*
        Called from ListPostActivity for uploading
        It will call async task, UploadPostTask for uploading picture and set database
     */
    public void uploadReceipt(Bitmap bitmap, String inBitmapPath, Bitmap thumbnail,
                           String inThumbnailPath, String inFileName, String inPostText, Location currentLocation) {

        UploadReceiptTask uploadTask = new UploadReceiptTask(bitmap, inBitmapPath, thumbnail,
                inThumbnailPath, inFileName, inPostText, currentLocation);
        uploadTask.execute();
    }

    /*
        Async task for uploading picture
        After finishing, callback,onPostUploaded() will be called, which is at NewPostActivity(UI Thread)
        AsyncTask enables proper and easy use of the UI thread.
        This class allows to perform background operations and publish results on the UI thread
        without having to manipulate threads and/or handlers.
        https://developer.android.com/reference/android/os/AsyncTask.html
     */
    class UploadReceiptTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Bitmap> bitmapReference;
        private WeakReference<Bitmap> thumbnailReference;
        private String postText;
        private String fileName;
        private Location currentLocation;

        public UploadReceiptTask(Bitmap bitmap, String inBitmapPath, Bitmap thumbnail, String inThumbnailPath,
                              String inFileName, String inPostText, Location location) {
            bitmapReference = new WeakReference<Bitmap>(bitmap);
            thumbnailReference = new WeakReference<Bitmap>(thumbnail);
            postText = inPostText;
            fileName = inFileName;
            currentLocation = location;
        }

        @Override
        protected void onPreExecute() {

        }

        /*
            This method is for uploading picture at the firebase server's storage service folder
            After uploading pictures(full, thumbnail), uri will be returned.
            and the post data will be stored at the Firebase database services with including this uri
         */

        @Override
        protected Void doInBackground(Void... params) {
//            //get full size bitmap reference
            Bitmap fullSize = bitmapReference.get();
//
//            //get thumbnail bitmap reference
//            final Bitmap thumbnail = thumbnailReference.get();
//
//            //if one of ref is null, do not send
//            if (fullSize == null || thumbnail == null) {
//                return null;
//            }
//
//            //get system time
//            Long timestamp = System.currentTimeMillis();
//
//
//
//            //change into byte array to upload
//            byte[] bytes = fullSizeStream.toByteArray();
//            //write compressed data into fire storage
//            fullSizeRef.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadReceiptTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadReceiptTask.TaskSnapshot taskSnapshot) {
//
//                    //After success, get download url
//                    final Uri fullSizeUrl = taskSnapshot.getDownloadUrl();
//
//                    //Upload thumbnail like full size image
//                    ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream();
//                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, thumbnailStream);
//
//                    //upload files
//                    thumbnailRef.putBytes(thumbnailStream.toByteArray())
//                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                    //Get database reference
//                                    final DatabaseReference ref = FirebaseUtil.getBaseRef();
//
//                                    //get post reference
//                                    DatabaseReference postsRef = FirebaseUtil.getPostsRef();
//
//                                    //get post key
//                                    final String newPostKey = postsRef.push().getKey();
//
//                                    //get thumbnail download url
//                                    final Uri thumbnailUrl = taskSnapshot.getDownloadUrl();
//
//                                    //get current user's object
//                                    Author author = FirebaseUtil.getAuthor();
//
//                                    //exception, if user didn't login
//                                    if (author == null) {
//                                        FirebaseCrash.logcat(Log.ERROR, TAG, "Couldn't upload post: Couldn't get signed in user.");
//                                        mCallbacks.onDataUploaded(mApplicationContext.getString(
//                                                R.string.error_user_not_signed_in));
//                                        return;
//                                    }
//
//                                    //create post object to upload at databse
//                                    Receipt newPost = new Receipt(author, currentLocation , fullSizeUrl.toString(), fullSizeRef.toString(),
//                                            thumbnailUrl.toString(), thumbnailRef.toString(), postText, ServerValue.TIMESTAMP);
//
//                                    //create Map to upload data
//                                    Map<String, Object> updatedUserData = new HashMap<>();
//
//                                    //Add post_id to current user's folder
//                                    updatedUserData.put(FirebaseUtil.getUsersPath() + author.getUid() + "/posts/"
//                                            + newPostKey, true);
//
//                                    //Add new post to post_id folder
//                                    updatedUserData.put(FirebaseUtil.getPostsPath() + newPostKey,
//                                            new ObjectMapper().convertValue(newPost, Map.class));
//
//                                    ref.updateChildren(updatedUserData, new DatabaseReference.CompletionListener() {
//                                        @Override
//                                        public void onComplete(DatabaseError firebaseError, DatabaseReference databaseReference) {
//                                            if (firebaseError == null) {
//                                                mCallbacks.onDataUploaded(null);
//                                            } else {
//                                                Log.e(TAG, "Unable to create new post: " + firebaseError.getMessage());
//                                                FirebaseCrash.report(firebaseError.toException());
//                                                mCallbacks.onDataUploaded(mApplicationContext.getString(
//                                                        R.string.error_upload_task_create));
//                                            }
//                                        }
//                                    });
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            FirebaseCrash.logcat(Log.ERROR, TAG, "Failed to upload post to database.");
//                            FirebaseCrash.report(e);
//                            mCallbacks.onDataUploaded(mApplicationContext.getString(
//                                    R.string.error_upload_task_create));
//                        }
//                    });
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//
//                    mCallbacks.onDataUploaded(mApplicationContext.getString(
//                            R.string.error_upload_task_create));
//                }
//            });
//            // TODO: Refactor these insanely nested callbacks.
            return null;
        }
    }

}
