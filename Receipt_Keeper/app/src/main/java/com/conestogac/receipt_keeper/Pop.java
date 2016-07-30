package com.conestogac.receipt_keeper;

/**
 * Created by Nicholas on 2016-07-03.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


public class Pop extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popwindow);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        //Popups do not completely take over the screen, they should be a bit
        //smaller than the view they are superimposed on top of.
        getWindow().setLayout((int)(width * .8), (int)(height * .6));
        TextView tvInfo = (TextView)findViewById(R.id.tvPopUp);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String data= extras.getString("POP_INFO");
            if (data!= null) {
                tvInfo.setText(data);
                //Since Data could exceed the size of the PopUp Window, we will allow scrolling
                tvInfo.setMovementMethod(new ScrollingMovementMethod());
            }
            int picNum = extras.getInt("PICNUM");
            ImageView ivToast = (ImageView) findViewById(R.id.ivPopUp);

            String imagePath = extras.getString("imagePath");

            File file = new File(imagePath);

            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                // BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file));
                ivToast.setImageBitmap(b);
               // ImageView img=(ImageView)findViewById(R.id.receiptImage);
               // img.setImageBitmap(b);


            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }




            //ivToast.setImageResource(picNum);
        }
    }
}
