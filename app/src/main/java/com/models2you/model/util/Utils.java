package com.models2you.model.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.model.Step;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.models2you.model.DialogOkButtonListener;
import com.models2you.model.R;
import com.models2you.model.app.App;
import com.models2you.model.app.Constants;
import com.models2you.model.model.BookingModel;
import com.models2you.model.receiver.LogoutAlarmManagerReceiver;
import com.models2you.model.service.LogoutService;
import com.models2you.model.ui.activity.LoginActivity;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.models2you.model.ui.activity.SignUpActivity.MEDIA_TYPE_IMAGE;

/**
 * Created by yogeshsoni on 26/09/16.
 */

public class Utils {

    private static final LogFactory.Log log = LogFactory.getLog(Utils.class);
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private static final String IMAGE_DIRECTORY_NAME = "Models2YouModel";
    public static final int INTENT_EXTRA_CAMERA_PICK_REQUEST = 1331;
    public static final int PERMISSION_REQUEST_CODE = 200;
    public static Uri fileUri;
    private static List<Step> stepList;

    public static List<Step> getStepList() {
        return stepList;
    }

    public static void setStepList(List<Step> stepList) {
        Utils.stepList = stepList;
    }

    //Number Picker
    public static String[] footNumberPickerStringArray = new String[]{"4'", "5'", "6'"};
    public static String[] inchNumberPickerStringArray = new String[]{"1''", "2''", "3''", "4''", "5''", "6''", "7''", "8''", "9''", "10''", "11''"};
    public static final String NOTIFICATION_MESSAGE = "com.models2you.model.Message_Handle_Brodcast_Reciever.DISPLAY_MESSAGE";

    public static ProgressDialog showProgressBar(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        try {
            progressDialog.setCancelable(false);
            if(!TextUtils.isEmpty(message)) {
                progressDialog.setMessage(message);
            }else{
                progressDialog.setMessage(context.getString(R.string.please_wait));
            }
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        } catch (Exception e) {
            LogFactory.Log.formatMessage("Unable to create Progress Dialog :" + e.getMessage());
        }
        return progressDialog;
    }

    /**
     * convertToDesiredFormat : method to check entered number is of desired length or not
     * @param numberStr : number in string
     * @return true/false
     */
    public static boolean isNumberLengthValid(String numberStr) {
        int NEEDED_PHONE_NUMBER_LENGTH = 8;
        if (!TextUtils.isEmpty(numberStr)) {
            if (numberStr.length() == NEEDED_PHONE_NUMBER_LENGTH) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * putDouble : method to save double value in sharedpreference
     * @param edit : editor
     * @param key : key
     * @param value : value
     */
    public static void putDoubleIntoSharedPreference(final SharedPreferences.Editor edit, final String key, final double value) {
        edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    /**
     * getDoubleFromSharedPreference : method to return double value from sharedpreference
     * @param prefs : pref
     * @param key : key
     * @param defaultValue : default value
     * @return double value
     */
    public static double getDoubleFromSharedPreference(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    /**
     * convertToDesiredFormat : method to check entered zip code  is of desired length or not
     * @param zipCodeStr : number in string
     * @return true/false
     */
    public static boolean isZipCodeLengthValid(String zipCodeStr) {
        int NEEDED_ZEEP_CODE_LENGTH = 6;
        if (!TextUtils.isEmpty(zipCodeStr)) {
            if (zipCodeStr.length() >= NEEDED_ZEEP_CODE_LENGTH) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }


    /*
   * returning image / video
   */
    public static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                log.debug("getOutputMediaFile : mediaStorageDir");
                return null;
            }
        }
        File mediaFile = new File(mediaStorageDir.getPath() + ".jpg");
        return mediaFile;
    }

    /**
     * convertToDesiredFormat : method to check entered password is of desired length or not
     * @param passwordStr : password in string
     * @return true/false
     */
    public static boolean isPasswordLengthValid(String passwordStr) {
        int NEEDED_PASSWORD_LENGTH = 6;
        if (!TextUtils.isEmpty(passwordStr)) {
            if (passwordStr.length() >= NEEDED_PASSWORD_LENGTH) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * convertToDesiredFormat : method to check entered name is valid
     * @param nameStr : name in string
     * @return true/false
     */
    public static boolean isNameValid(String nameStr) {
        if (!TextUtils.isEmpty(nameStr)) {

            if (nameStr.length() < 3) {
                return false;
            } else if (!nameStr.matches("[a-zA-Z ]+")) {
                return false;
            } else if (nameStr.startsWith(" ")) {
                return false;
            } else {
                return true;
            }

        } else {
            return false;
        }
    }



    /**
     * convertToDesiredFormat : method to check entered name is valid
     * @param cityStr : name in string
     * @return true/false
     */
    public static boolean isCityValid(String cityStr) {
        if (!TextUtils.isEmpty(cityStr)) {

            if (cityStr.length() < 2) {
                return false;
            } else if (!cityStr.matches("[a-zA-Z ]+")) {
                return false;
            } else if (cityStr.startsWith(" ")) {
                return false;
            } else {
                return true;
            }

        } else {
            return false;
        }
    }
    /**
     * convertToDesiredFormat : method to convert passed date to desired date format
     * @param dateToConvert : date in string to convert
     * @param currentFormat : current date format
     * @param convertToFormat : convert to format
     * @return formatted date after conversion
     */
    public static String convertDateToUTCDateFormat(String dateToConvert, String currentFormat, String convertToFormat) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat(currentFormat);
            Date date = fmt.parse(dateToConvert);
            SimpleDateFormat fmtOut = new SimpleDateFormat(convertToFormat);
            fmtOut.setTimeZone(TimeZone.getTimeZone("UTC"));
            return fmtOut.format(date);
        }catch (Exception e) {
            e.getMessage();
        }
        return "";
    }

    /**
     * convertToDesiredFormat : method to convert passed date to desired date format
     * @param dateToConvert : date in string to convert
     * @param currentFormat : current date format
     * @param convertToFormat : convert to format
     * @return formatted date after conversion
     */
    public static String convertSimpleDateFormat(String dateToConvert, String currentFormat, String convertToFormat) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat(currentFormat);
            Date date = fmt.parse(dateToConvert);
            SimpleDateFormat fmtOut = new SimpleDateFormat(convertToFormat);
            return fmtOut.format(date);
        }catch (Exception e) {
            e.getMessage();
        }
        return "";
    }

    /***
     * getLocalTimeZoneDateFormat : method to convert local time zone from UTC from server
     * @param dateToConvert : date to convert in format
     * @param currentFormat : current format of Date
     * @param convertToFormat : format in which local date to convert
     * @return formatted date string
     */
    public static String getLocalTimeZoneDateFormat(String dateToConvert, String currentFormat, String convertToFormat) {
        SimpleDateFormat df = new SimpleDateFormat(currentFormat);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        String formattedDate = "";
        try {
            date = df.parse(dateToConvert);
            df.setTimeZone(TimeZone.getDefault());
            df = new SimpleDateFormat(convertToFormat);
            return formattedDate = df.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;

    }

    /**
     * method to get time difference in hours between two date
     * @param datetime : time to fetch difference from current time in hours
     * @return number of hours difference in hours
     */
    public static int getHoursDifferenceBetweenTime(String datetime) {
        try {
            Calendar date = Calendar.getInstance();
            date.setTime(new SimpleDateFormat(Constants.desiredFormattedDateToSend, Locale.getDefault()).parse(datetime)); // Parse into Date object
            Calendar now = Calendar.getInstance(); // Get time now
            long differenceInMillis = date.getTimeInMillis() - now.getTimeInMillis();
            long differenceInHours = (differenceInMillis) / 1000L / 60L / 60L; // Divide by millis/sec, secs/min, mins/hr
            return (int)differenceInHours;
        }catch (Exception e) { log.error("hoursAgo exception " + e);}
        return 0;
    }

    /**
     * convertStringToMillis : method to convert date string to time in milli seconds
     * @param dateStr : date in string format
     * @return long time in milliseconds
     */
    public static long convertStringToMillis(String dateStr , String dateTimeFormat) {
        long millis = 10;
        DateFormat readFormat = new SimpleDateFormat(dateTimeFormat);
        Date date = null;
        try {
            date = readFormat.parse(dateStr);
            millis = date.getTime();
            log.verbose("convertStringToMillis millis " + millis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return millis;
    }
    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    public static String convertTimeMillisToDateFormat(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        log.verbose("convertTimeMillisToDateFormat time " + calendar.getTime());
        return formatter.format(calendar.getTime());
    }

    /**
     * method to convert string to date
     * @param stringToConvert : string to convert in date format
     * @param currentFormat : current format of string
     * @return date instance
     */
    public static Date convertStringToDate(String stringToConvert , String currentFormat) {
        SimpleDateFormat format = new SimpleDateFormat(currentFormat , Locale.getDefault());
        try {
            return format.parse(stringToConvert);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * getCurrentTimeInMillis : method to get current time in milliseconds
     * @return current time in milliseconds
     */
    public static long getCurrentTimeInMillis() {
        return System.currentTimeMillis();
    }

    /**
     * method to send current time in String format
     * @return current time in string format
     */
    public static String getCurrentTimeInString() {
        long currentTimeMillis = getCurrentTimeInMillis();
        return convertTimeMillisToDateFormat(currentTimeMillis , Constants.desiredFormattedDateToSend);
    }
    /**
     * isActivityRunning : method to check Activity running in foreground or not
     * @param ctx : context
     * @return true/false
     */
    public static boolean isActivityRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }
        return false;
    }
    /**
     * Checking device has camera hardware or not
     * */
    public static boolean isDeviceSupportCamera() {
        if (App.get().getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    /**
     *
     *  @param photoPath : path of Image Stored by Cropper Library
     * @param thumbnailBitmap : bitmap of image
     * @return Bitmap After Rotate or unchanged
     */
    public static Bitmap checkImageOrientation(String photoPath, Bitmap thumbnailBitmap) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    thumbnailBitmap = rotateImage(thumbnailBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    thumbnailBitmap = rotateImage(thumbnailBitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    thumbnailBitmap = rotateImage(thumbnailBitmap, 270);
                    break;
            }
            return thumbnailBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return thumbnailBitmap;
    }
    /**
     *
     * @param thumbnailBitmap : bitmap of Image
     * @param rotationAngle : Rotation Angle of Image
     * @return : Bitmap After Rotate
     */
    private static Bitmap rotateImage(Bitmap thumbnailBitmap, int rotationAngle) {
        Bitmap retVal;
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);
        retVal = Bitmap.createBitmap(thumbnailBitmap, 0, 0, thumbnailBitmap.getWidth(), thumbnailBitmap.getHeight(), matrix, true);
        return retVal;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkReadExternalStoragePermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /*Glide Image Loading*/
    public static void showGlideImage(Context context, ImageView imageView, String imageUrl, int defaultImageId) {
        if (imageView != null) {
            //final ImageView imageView = imageViewWeakReference.get();
            Glide.with(context)
                    .load(imageUrl).asBitmap()
                    .placeholder(defaultImageId)
                    .centerCrop()
                    .error(defaultImageId)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);

        }
    }

    /* method to show glide image and show progress bar while loading */
    public static void showGlideImageUsingProgressBar(Context context, ImageView imageView, String imageUrl, int defaultImageId, final ProgressBar progressBar) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(defaultImageId)
                .centerCrop()
                .error(defaultImageId)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
    }
    /*Glide Rounded image with caching*/
    public static void showRoundedGlideImage(final Context context, final ImageView imageView, String imageUrl , int placeHolderId) {
        showRoundedGlideImage(context, imageView, imageUrl, placeHolderId, false);
    }
    /* Glide Rounded Image Loading*/
    public static void showRoundedGlideImage(final Context context, final ImageView imageView, String imageUrl, int placeHolderId, boolean reset) {
        if (imageView != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .asBitmap()
                    .placeholder(placeHolderId)
                    .centerCrop()
                    .error(placeHolderId)
                    .diskCacheStrategy(reset ? DiskCacheStrategy.NONE : DiskCacheStrategy.SOURCE)
                    .skipMemoryCache(reset)
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            imageView.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
    }
    /* Glide Rounded Image Loading with yellow border */
    public static void showRoundedGlideImageWithYellowBorder(final Context context, final ImageView imageView, String imageUrl,int placeHolderId , boolean reset) {
        if (imageView != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .asBitmap()
                    .placeholder(placeHolderId)
                    .centerCrop()
                    .error(placeHolderId)
                    .diskCacheStrategy(reset ? DiskCacheStrategy.NONE : DiskCacheStrategy.SOURCE)
                    .skipMemoryCache(reset)
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =createRoundedBitmapDrawableWithBorder(context, resource);
                            circularBitmapDrawable.setCircular(true);
                            imageView.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
    }

    /**
     * createRoundedBitmapDrawableWithBorder : method to create rounded border and attach yellow border around it
     * @param context : context
     * @param bitmap : bitmap
     * @return bitmap
     */
    private static RoundedBitmapDrawable createRoundedBitmapDrawableWithBorder(Context context , Bitmap bitmap){
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int borderWidthHalf = 10; // In pixels
        int bitmapRadius = Math.min(bitmapWidth,bitmapHeight)/2;
        int bitmapSquareWidth = Math.min(bitmapWidth,bitmapHeight);
        int newBitmapSquareWidth = bitmapSquareWidth+borderWidthHalf;
        Bitmap roundedBitmap = Bitmap.createBitmap(newBitmapSquareWidth,newBitmapSquareWidth,Bitmap.Config.ARGB_8888);
        // Initialize a new Canvas to draw empty bitmap
        Canvas canvas = new Canvas(roundedBitmap);
        canvas.drawColor(Color.YELLOW);
        int x = borderWidthHalf + bitmapSquareWidth - bitmapWidth;
        int y = borderWidthHalf + bitmapSquareWidth - bitmapHeight;
        canvas.drawBitmap(bitmap, x, y, null);

        // Initializing a new Paint instance to draw circular border
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidthHalf);

        //Check Api Level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // only for gingerbread and newer versions
            borderPaint.setColor(context.getResources().getColor(R.color.yellow, null));
        }else {
            borderPaint.setColor(context.getResources().getColor(R.color.yellow));
        }

        canvas.drawCircle(canvas.getWidth()/2, canvas.getWidth()/2, newBitmapSquareWidth/2, borderPaint);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(),roundedBitmap);
        roundedBitmapDrawable.setCornerRadius(bitmapRadius);
        roundedBitmapDrawable.setAntiAlias(true);
        // Return the RoundedBitmapDrawable
        return roundedBitmapDrawable;
    }

    /* Glide Image Loading with male default profile */
    public static void showGlideImage(final Context context, final ImageView imageView, String imageUrl, int resId , boolean reset) {
        if (imageView != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .asBitmap()
                    .placeholder(resId)
                    .centerCrop()
                    .error(resId)
                    .diskCacheStrategy(reset ? DiskCacheStrategy.NONE : DiskCacheStrategy.SOURCE)
                    .skipMemoryCache(reset)
                    .into(imageView);
        }
    }



    /**
     * isValidEmail : method to check string is valid or not
     * @param target : CharSequence
     * @return true/false
     */
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void disableCopyPaste(EditText editText) {
        editText.setLongClickable(false);
        editText.setTextIsSelectable(false);
        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
    }

    /**
     * startAutoLogoutAlarmManager : start auto logout alarm manager after successful login
     */
    public static void startAutoLogoutAlarmManager(Context context){
        Intent alarmIntent = new Intent(context, LogoutAlarmManagerReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);
        Log.e("alarmRunning", "" + alarmRunning);
        if (!alarmRunning) {
            long sessionTimeout = App.get().getDefaultAppSharedPreferences().getLong(SharePreferenceKeyConstants.LOGOUT_SESSION_DURATION, 0);
            long sessionTimeoutMillis = sessionTimeout * 1000; // converting to millis from second
            if (sessionTimeoutMillis != 0) {
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                PendingIntent pi = PendingIntent.getBroadcast(context, 1253, alarmIntent, 0);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                //After fetching from login response
                //am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AUTO_LOGOUT_TIME, pi);
                am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() +
                                sessionTimeoutMillis, pi);
            }
        }
    }

    public static void showInputAlertDialog(final Activity activity, String message, final DialogOkButtonListener onPositiveDialogButtonClicked) {
        LayoutInflater li = LayoutInflater.from(activity);
        View promptsView = li.inflate(R.layout.dialog_cancel_reason_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setView(promptsView);
        final TextView alertCancelMsg = (TextView) promptsView.findViewById(R.id.alertCancelMsg);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogCancelInput);
        alertCancelMsg.setText(message);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.alert_dialog_button_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = userInput.getText().toString();
                        if (!TextUtils.isEmpty(input)) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            onPositiveDialogButtonClicked.onDialogOkButtonClicked(input);
                        } else {
                            DialogHelper.showOkDialog(activity , activity.getString(R.string.invalid_what_wear) , null);
                        }
                    }
                })
                .setNegativeButton(activity.getString(R.string.alert_dialog_button_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        if (!activity.isFinishing()) {
            alertDialog.show();
        }
    }
    /**
     * stopAutoLogoutService : method to Cancel Auto Logout Service
     */
    public static void stopAutoLogoutService() {
        // check logout alarm receiver running and cancel receiver running
        log.verbose("stopAutoLogoutService");
        Context applicationContext = App.get().getApplicationContext();
        Intent intent = new Intent(applicationContext , LogoutAlarmManagerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(applicationContext , 1253, intent, 0);
        AlarmManager alarmManager = (AlarmManager)App.get().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        // check logout service running and if true - then cancel logout service
        if (isServiceRunning(LogoutService.class)) {
            applicationContext.stopService(new Intent(applicationContext , LogoutService.class));
        }
    }

    /**
     * method to check service running or not
     * @return true/false
     */
    public static boolean isServiceRunning(Class className) {
        ActivityManager manager = (ActivityManager) App.get().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (className.getName().equals(service.service.getClassName())) {
                log.verbose("LogoutService running");
                return true;
            }
        }
        return false;
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static boolean isValidEmailValue(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public static boolean contain(EditText editText) {
        editText.setError(null);
        if (editText.getText().toString().trim().length() > 0) {
            return true;
        }
        return false;
    }

    public static String writeToTempFile(String prefix, String ext, byte[] data) {
        File outputDir = App.get().getCacheDir();
        try {
            File outputFile = File.createTempFile(prefix, ext, outputDir);
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(data);
            fos.flush();
            fos.close();
            outputFile.deleteOnExit();
            return outputFile.getAbsolutePath();
        } catch (IOException ignore) {
        }
        return null;
    }

    /**
     * hideKeyBoard : method to check current focus on view and hide soft keyboard is showing
     * @param context : context
     */
    public static void hideKeyBoard(Context context){
        // Check if no view has focus:
        View view = ((Activity)context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * checkInternetConnectivity : method to check current Internet Connectivity Status
     */

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     * selectImage : method used to open picture options and select image from gallery/camera
     */
    public static void selectImage(final Activity context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    if (Utils.isDeviceSupportCamera()) {
                        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED)) {
                            requestCameraPermission(context);
                        } else {
                            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                            launchCamera(context);
                        }
                    } else {
                        //showLongSnackBar(context.getResources().getString(R.string.error_camera_not_supported));
                    }
                }
                else if (options[item].equals("Choose from Gallery")) {
                    if (Utils.isDeviceSupportCamera()) {
                        boolean result= Utils.checkReadExternalStoragePermission(context);
                        if(result) {
                            Crop.pickImage((Activity) context);
                        }
                    } else {
                        //showLongSnackBar(context.getResources().getString(R.string.error_camera_not_supported));
                    }
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     *  Request Camera Permission
     */
    private static void requestCameraPermission(Activity context) {
        ActivityCompat.requestPermissions(context, new String[]{CAMERA, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }


    /**
     * Launch Front Camera If available else launch back camera
     */
    public static Uri launchCamera(Activity context) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        context.startActivityForResult(cameraIntent, INTENT_EXTRA_CAMERA_PICK_REQUEST);
        return fileUri;
    }

    /**
     * Creating file uri to store image/video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(Utils.getOutputMediaFile(type));
    }

    public static HeightPickerSelectedValue showHeightPickerDialog(Context context) {

        HeightPickerSelectedValue heightPickerSelectedValue = new HeightPickerSelectedValue();

        LinearLayout LL = new LinearLayout(context);
        LL.setOrientation(LinearLayout.HORIZONTAL);

        final NumberPicker footNumberPicker = new NumberPicker(context);
        footNumberPicker.setMaxValue(6);
        footNumberPicker.setMinValue(4);
        footNumberPicker.setDisplayedValues(footNumberPickerStringArray);

        final NumberPicker inchNumberPicker = new NumberPicker(context);
        inchNumberPicker.setMaxValue(11);
        inchNumberPicker.setMinValue(1);
        inchNumberPicker.setDisplayedValues(inchNumberPickerStringArray);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);
        params.gravity = Gravity.CENTER;
        LinearLayout.LayoutParams numPickerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        numPickerParams.weight = 1;
        LinearLayout.LayoutParams qPickerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        qPickerParams.weight = 1;
        LL.setLayoutParams(params);
        LL.addView(footNumberPicker, numPickerParams);
        LL.addView(inchNumberPicker, qPickerParams);

        heightPickerSelectedValue.setPickerSelectedValue(footNumberPicker , inchNumberPicker , LL);
        return heightPickerSelectedValue;
    }

    /**
     * HeightPickerSelectedValue : inner class for Height Picker Selected Value
     */
    public static class HeightPickerSelectedValue {
        public NumberPicker footNumberPicker , inchNumberPicker ;
        public View view;
        HeightPickerSelectedValue() {}
        void setPickerSelectedValue(NumberPicker footNumberPicker , NumberPicker inchNumberPicker , View view) {
            this.footNumberPicker = footNumberPicker;
            this.inchNumberPicker = inchNumberPicker;
            this.view  = view;
        }
    }


    /**
     * getBookingModelDetail : method to return single Booked model from List
     * @param bookingId : bookingId
     * @param bookedModelList : bookedModelList
     * @return booked model
     */
    public static BookingModel getBookingModelDetail(List<BookingModel> bookedModelList, int bookingId) {
        if (bookedModelList != null) {
            for (BookingModel bookingModel : bookedModelList) {
                if (bookingModel.id == bookingId) {
                    log.verbose("getBookingModelDetail id " + bookingId);
                    return bookingModel;
                }
            }
        }
        return null;
    }

    public static Bitmap loadBitmapFromView(View view,int height,int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
//        view.layout(0, 0, view.getLayoutParams().width, view.getLayoutParams().height);
        view.draw(canvas);
        return bitmap;
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static void showAlertForLoginOnViewCartClick(final Context context) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        builder.setMessage(R.string.alert_dialog_title_token_empty);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                Intent toLogin = new Intent(context, LoginActivity.class);
                toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(toLogin);
            }
        });
        builder.show();
    }
}
