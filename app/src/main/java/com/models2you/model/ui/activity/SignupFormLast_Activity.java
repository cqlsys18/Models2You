package com.models2you.model.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.models2you.model.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupFormLast_Activity extends AppCompatActivity {

    @BindView(R.id.scroll_view)
    ScrollView scroll_view;

    @BindView(R.id.form_layout)
    LinearLayout form_layout;

    @BindView(R.id.next)
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form4_);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.next)
    public void onNextClicked(View view) {
        takeScreenshot();
    }

    public void takeScreenshot() {
        Intent intent = new Intent(SignupFormLast_Activity.this,SignupFormLast_Activity.class);
        startActivity(intent);

//        form_layout.setDrawingCacheEnabled(true);
//        form_layout.buildDrawingCache();
//        Bitmap bitmap_back = Utils.loadBitmapFromView(form_layout, form_layout.getChildAt(0).getHeight(), form_layout.getChildAt(0).getWidth());
//        form_layout.setDrawingCacheEnabled(false);
//        storeImage(bitmap_back);
       /* try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (bitmap_back != null) {
                        bitmap_back = Bitmap.createScaledBitmap(bitmap_back, bitmap.getWidth() + 4, bitmap.getHeight() + 32, true);
                    }
                    Bitmap bmp2 = bitmap.copy(bitmap.getConfig(), true);
                    Bitmap bmp3 = bitmap_back.copy(bitmap_back.getConfig(), true);
                    //stuff that updates ui
                    relative_layout.setDrawingCacheEnabled(false);
                    Document doc = createDoc();
                    try {
                        if (!doc.isOpen()) {
                            doc.open();
                        }
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                        bmp2.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        bmp3.compress(Bitmap.CompressFormat.PNG, 100, stream1);
                        Image image = Image.getInstance(stream.toByteArray());
                        Image image2 = Image.getInstance(stream1.toByteArray());
//                        image.scaleToFit(width,getHeight());
//                        image2.scaleToFit(width,getHeight());
                        image.scaleAbsolute(A6.getHeight(), A6.getWidth());
                        image2.scaleAbsolute(A6.getHeight(), A6.getWidth());
                        doc.add(image);
                        doc.add(image2);
                    } catch (DocumentException | IOException e) {
                        e.printStackTrace();
                    } finally {
                        doc.close();
                    }
                    mHandler.sendEmptyMessage(0);
                    //SendSimpleMessage();
                    //mHandler.sendEmptyMessage(0);
                }
            }).start();
        } catch (Exception ex) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            ex.printStackTrace();
        }*/
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("Value", "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("Value", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("Value", "Error accessing file: " + e.getMessage());
        }
    }

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
}
