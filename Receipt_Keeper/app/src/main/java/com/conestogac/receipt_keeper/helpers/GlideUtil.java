package com.conestogac.receipt_keeper.helpers;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.conestogac.receipt_keeper.R;

import java.io.File;

/*
    It is Util class to use Glide opensource which helps caching or decoding remote located images
    moreover it is east to use.

    http://google-opensource.blogspot.ca/2014/09/glide-30-media-management-library-for.html
 */
public class GlideUtil {
    public static void loadImage(File url, ImageView imageView) {
        Context context = imageView.getContext();
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.bill_receipt_check)
                .crossFade()
                .centerCrop()
                .into(imageView);
    }

    public static void loadProfileIcon(String url, ImageView imageView) {
        Context context = imageView.getContext();
        Glide.with(context)
                .load(Uri.parse(url))
                .placeholder(R.drawable.ic_broken_image_white_24dp)
                .dontAnimate()
                .fitCenter()
                .into(imageView);
    }
}