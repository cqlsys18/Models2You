package com.models2you.model.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.models2you.model.R;
import com.models2you.model.app.App;
import com.models2you.model.app.Constants;
import com.models2you.model.event.Events;
import com.models2you.model.job.AddPhotoJob;
import com.models2you.model.job.GetAllPhotosJob;
import com.models2you.model.rest.models.ResponseViews;
import com.models2you.model.ui.adapter.ManagePhotoAdapter;
import com.models2you.model.ui.base.BaseAppCompatActivity;
import com.models2you.model.util.ErrorView;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.SharePreferenceKeyConstants;
import com.models2you.model.util.Utils;
import com.soundcloud.android.crop.Crop;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amitsingh on 9/27/2016.
 * Manage Photo Activity to update Photos
 */
public class ManagePhotoActivity extends BaseAppCompatActivity {

    private static final LogFactory.Log log = LogFactory.getLog(PreviousReservationActivity.class);
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Uri fileUri;

    @BindView(R.id.mp_user_photo_recycler)
    RecyclerView userPhotoRecycler;

    @BindView(R.id.mp_add_btn)
    Button addBtn;

    @BindView(R.id.mp_edit_btn)
    Button editBtn;

    @BindView(R.id.mp_done_btn)
    Button doneBtn;

    private List<ResponseViews.Photos> photoList;
    private ManagePhotoAdapter adapter;
    private boolean isHideActionButton;
    private String forUpdateProfileImageURL;

    @Override
    protected boolean needToFinishCurrentActivity() { return true; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.android_manage_photo);
        ButterKnife.bind(this);
        btnHome.setImageResource(R.drawable.back_button);

        isHideActionButton = getIntent().getBooleanExtra(Constants.INTENT_EXTRA_HIDE_ACTION_BUTTON, false);

        //set layout manager
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        userPhotoRecycler.setLayoutManager(layoutManager);

        //Hide Action Button
        if(isHideActionButton){
            addBtn.setVisibility(View.VISIBLE);
            addBtn.setText(R.string.alert_dialog_button_cancel);
            editBtn.setVisibility(View.GONE);
            doneBtn.setVisibility(View.VISIBLE);
        }

        //showSmileyLoadingView();
        progressDialog = Utils.showProgressBar(this, null);
        adapter = new ManagePhotoAdapter(this, progressDialog, isHideActionButton);
        adapter.setPhotoListener(new ManagePhotoAdapter.ManagePhotoListener() {
            @Override
            public void displaySnackBar(String displayMsg) {
                showLongSnackBar(displayMsg);
            }

            @Override
            public void retrieveSelectUrl(String url) {
                forUpdateProfileImageURL = url;
            }
        });
        imgUserProfileBtn.setVisibility(View.VISIBLE);
        App.get().getJobManager().addJob(new GetAllPhotosJob());
    }

    @OnClick(R.id.mp_add_btn)
    void addPhotoAction(View view) {
        if (!isTooEarlyMultipleClicks(view)) {
            //userProfileboolValueOne = true;
            if(isHideActionButton){
                finish();
            }else {
                Utils.selectImage(this);
            }
        }
    }

    @OnClick(R.id.mp_edit_btn)
    void photoEditAction() {
        doneBtn.setVisibility(View.VISIBLE);
        editBtn.setVisibility(View.GONE);
        addBtn.setVisibility(View.GONE);
        adapter.setEditMode(true);
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.mp_done_btn)
    void finishEditPhotoAction() {
        if(isHideActionButton) {
            Intent intent=new Intent();
            intent.putExtra(UpdateProfileActivity.UPDATE_PROFILE_URL, forUpdateProfileImageURL);
            setResult(UpdateProfileActivity.REQUEST_CODE,intent);
            finish();
        }else{
            doneBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.VISIBLE);
            addBtn.setVisibility(View.VISIBLE);
            adapter.setEditMode(false);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onOptionsMenuManagePhotoClicked(View view) {
        if (frameLayout.getParent() != null) {
            ((ViewGroup) frameLayout.getParent()).removeView(frameLayout);
        }
    }

    /**
     * Handling crop result from cropper library
     *
     * @param resultCode resultCode for cropper
     * @param result     intent data
     */
    private void handleCrop(int resultCode, Intent result) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 9;
        if (resultCode == RESULT_OK) {
            String path = Crop.getOutput(result).getPath();
            String p = Uri.parse(path).getPath();
            Bitmap imageBitmap = BitmapFactory.decodeFile(path,
                    options);
            if (imageBitmap != null) {
                imageBitmap = Utils.checkImageOrientation(path, imageBitmap); // check image orientation and return after image rotate if required.
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArrayImageBitmap = stream.toByteArray();
                if(progressDialog == null){
                    progressDialog = Utils.showProgressBar(this, null);
                }else {
                    progressDialog.show();
                }
                //testUploadImage();
                if(Utils.isNetworkAvailable(ManagePhotoActivity.this)) {
                    App.get().getJobManager().addJob(new AddPhotoJob(byteArrayImageBitmap));
                }else {
                    showLongSnackBar(getString(R.string.error_check_internet_to_add_internet));
                    progressDialog.dismiss();
                }
            }else {
                showShortSnackBar(getString(R.string.sign_up_unable_to_capture_msg));
           }
        } else if (resultCode == Crop.RESULT_ERROR) {
            showShortSnackBar(Crop.getError(result).getMessage());
        }
    }

    /**
     * Start the cropper
     *
     * @param source URI of the image
     */
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            fileUri = Utils.fileUri;
            if (requestCode == Utils.INTENT_EXTRA_CAMERA_PICK_REQUEST) {
                if (fileUri != null) {
                    beginCrop(fileUri);
                } else {
                    showShortSnackBar(getString(R.string.sign_up_unable_to_capture_msg));
                }
            } else if (requestCode == Crop.REQUEST_PICK) {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        fileUri = data.getData();
                        beginCrop(fileUri);
                        return;
                    } else {
                        showShortSnackBar(getString(R.string.sign_up_unable_to_capture_msg));
                    }
                }

            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode, data);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeExternalStorageAccepted) {
                        Utils.launchCamera(this);
                    } else {
                        showLongSnackBar(getResources().getString(R.string.error_camera_permission_not_granted));
                    }
                }
                break;
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Events.SetAllPhotosListEventStatus event){
        log.verbose("SetAllPhotosListEventStatus isSuccess " + event.isSuccess);
        event.removeStickySelf();
        progressDialog.dismiss();
        if(event.isSuccess){
            photoList = event.photosList;
            adapter.setPhotosList(photoList);
            userPhotoRecycler.setAdapter(adapter);
        }else{
            if (event.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN_LOGOUT.toLowerCase()) ||event.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN.toLowerCase()) ) {
                App.get().getPreferenceEditor().putString(SharePreferenceKeyConstants.APP_TOKEN, "").commit();
                App.get().getPreferenceEditor().apply();
                Utils.showAlertForLoginOnViewCartClick(ManagePhotoActivity.this);
            } else {
                showLongSnackBar(event.errorMessage);
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Events.SetNewPhotoEventStatus event){
        log.verbose("SetNewPhotoEventStatus isSuccess " + event.isSuccess);
        event.removeStickySelf();
        progressDialog.dismiss();
        if(event.isSuccess){
            ResponseViews.Photos photo = new ResponseViews.Photos();
            photo.id = event.newPhoto.id;
            photo.photoUrl = event.newPhoto.photoUrl;

            photoList.add(photo);
            adapter.setPhotosList(photoList);
            adapter.notifyDataSetChanged();

        }else{
            if (event.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN_LOGOUT.toLowerCase()) ||event.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN.toLowerCase()) ) {
                App.get().getPreferenceEditor().putString(SharePreferenceKeyConstants.APP_TOKEN, "").commit();
                App.get().getPreferenceEditor().apply();
                Utils.showAlertForLoginOnViewCartClick(ManagePhotoActivity.this);
            } else {
                showLongSnackBar(event.errorMessage);
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Events.SetRemovePhotoEventStatus event) {
        log.verbose("SetNewPhotoEventStatus isSuccess " + event.isSuccess);
        event.removeStickySelf();
        progressDialog.dismiss();
        if (event.isSuccess) {
            photoList.remove(event.id);
            adapter.setPhotosList(photoList);
            adapter.notifyDataSetChanged();
        }else{
            if (event.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN_LOGOUT.toLowerCase()) ||event.errorMessage.toLowerCase().equals(ErrorView.ERROR_MESSAGE_NOT_VALID_TOKEN.toLowerCase()) ) {
                App.get().getPreferenceEditor().putString(SharePreferenceKeyConstants.APP_TOKEN, "").commit();
                App.get().getPreferenceEditor().apply();
                Utils.showAlertForLoginOnViewCartClick(ManagePhotoActivity.this);
            } else {
                showLongSnackBar(event.errorMessage);
            }
        }
    }
}
