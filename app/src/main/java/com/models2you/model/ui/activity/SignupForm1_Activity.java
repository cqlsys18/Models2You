package com.models2you.model.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.models2you.model.R;
import com.models2you.model.app.App;
import com.models2you.model.ui.base.BaseAppCompatActivity;
import com.models2you.model.util.SharePreferenceKeyConstants;
import com.models2you.model.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupForm1_Activity extends BaseAppCompatActivity {

    @BindView(R.id.scroll_view)
    ScrollView scroll_view;

    @BindView(R.id.form_layout)
    LinearLayout form_layout;

    @BindView(R.id.next)
    Button next;

    @BindView(R.id.user_name)
    EditText user_name;

    @BindView(R.id.business_name)
    EditText business_name;

    @BindView(R.id.address)
    EditText address;

    @BindView(R.id.radio_group)
    RadioGroup radio_group;

    @BindView(R.id.screensh)
    ImageView screensh;
    byte[] byteArray1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form1_);
        ButterKnife.bind(this);
        main_layout.setVisibility(View.GONE);
    }

    @OnClick(R.id.next)
    public void onNextClicked(View view) {
        takeScreenshot();
    }

    public void takeScreenshot() {

        if (user_name.getText().toString().equals("")) {
            user_name.setError(getResources().getString(R.string.error_username));
            user_name.requestFocus();
        } else if (business_name.getText().toString().equals("")) {
            business_name.setError(getResources().getString(R.string.error_business));
            business_name.requestFocus();
        } else if (radio_group.getCheckedRadioButtonId() == -1) {
            Utils.showToast(SignupForm1_Activity.this, "Please select atleast one option");
        } else if (address.getText().toString().equals("")) {
            address.setError(getResources().getString(R.string.error_address));
            address.requestFocus();
        } else {
            next.setVisibility(View.GONE);
            progressDialog = Utils.showProgressBar(SignupForm1_Activity.this, getResources().getString(R.string.please_wait));

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveNextScreen();
                }
            }, 1000);
        }
    }

    private void moveNextScreen() {
        scroll_view.setDrawingCacheEnabled(true);
        scroll_view.buildDrawingCache();
        new Thread(new Runnable() {
            @Override
            public void run() {

                Bitmap bitmap1 = Utils.loadBitmapFromView(scroll_view, scroll_view.getChildAt(0).getHeight(), scroll_view.getChildAt(0).getWidth());
                saveImage(Utils.encodeTobase64(bitmap1));
//
//                Bitmap.createScaledBitmap(bitmap1, 270, 270, false);
//
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byteArray1 = stream.toByteArray();
                scroll_view.setDrawingCacheEnabled(false);
                mHandler.sendEmptyMessage(0);
            }
        }).start();

//        ;


    }

    private void saveImage(String base64) {
        SharedPreferences.Editor editor = App.get().getPreferenceEditor();
        editor.putString(SharePreferenceKeyConstants.FORM_1, base64);
        editor.apply();
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Intent intent = new Intent(SignupForm1_Activity.this, SignupForm2_Activity.class);
            intent.putExtra("image1", getIntent().getByteArrayExtra("image1"));
            intent.putExtra("image2", getIntent().getByteArrayExtra("image2"));
            intent.putExtra("image3", getIntent().getByteArrayExtra("image3"));
            intent.putExtra("image4", getIntent().getByteArrayExtra("image4"));
            intent.putExtra("email", getIntent().getStringExtra("email"));
            intent.putExtra("password", getIntent().getStringExtra("password"));
            intent.putExtra("phone", getIntent().getStringExtra("phone"));
            intent.putExtra("name", getIntent().getStringExtra("name"));
            intent.putExtra("address", getIntent().getStringExtra("address"));
            intent.putExtra("city", getIntent().getStringExtra("city"));
            intent.putExtra("state", getIntent().getStringExtra("state"));
            intent.putExtra("convertedDOB", getIntent().getStringExtra("convertedDOB"));
            intent.putExtra("intagramId", getIntent().getStringExtra("intagramId"));
            intent.putExtra("facebookId", getIntent().getStringExtra("facebookId"));
            intent.putExtra("favorites", getIntent().getStringExtra("favorites"));
            intent.putExtra("eyeColor", getIntent().getStringExtra("eyeColor"));
            intent.putExtra("hairColor", getIntent().getStringExtra("hairColor"));
            intent.putExtra("footSelectedVal", getIntent().getIntExtra("footSelectedVal", 0));
            intent.putExtra("inchSelectedVal", getIntent().getIntExtra("inchSelectedVal", 0));
            intent.putExtra("zipCode", getIntent().getStringExtra("zipCode"));

            startActivity(intent);

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            next.setVisibility(View.VISIBLE);
//            scroll_view.setDrawingCacheEnabled(false);
        }
    };

}
