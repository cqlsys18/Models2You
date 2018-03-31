package com.models2you.model.ui.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.models2you.model.R;
import com.models2you.model.app.App;
import com.models2you.model.job.RemovePhotoJob;
import com.models2you.model.rest.models.ResponseViews;
import com.models2you.model.util.SharePreferenceKeyConstants;
import com.models2you.model.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yogeshsoni on 30/09/16.
 */

public class ManagePhotoAdapter extends RecyclerView.Adapter<ManagePhotoAdapter.ViewHolder> {

    private List<ResponseViews.Photos> photosList;
    private Context context;
    private boolean isEditMode;
    private ProgressDialog progressDialog;
    private ManagePhotoListener photoListener;
    private boolean isHideActionButton;

    private static String url = "";


    public void setPhotoListener(ManagePhotoListener photoListener) {
        this.photoListener = photoListener;
    }

    public ManagePhotoAdapter(Context context, ProgressDialog progressDialog, boolean isHideActionButton) {
        this.context = context;
        this.progressDialog = progressDialog;
        this.isHideActionButton = isHideActionButton;

        //Only Working on UpdateProfileActivity
        url = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.USER_PICTURE, "");

    }

    public void setPhotosList(List<ResponseViews.Photos> photosList) {
        this.photosList = photosList;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_manage_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String imageUrl = photosList.get(position).photoUrl;
        Utils.showGlideImage(context, holder.modelPhotoView, imageUrl, 0);

        if (isHideActionButton) {
            holder.modelPhotoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    url = imageUrl;
                    photoListener.retrieveSelectUrl(imageUrl);
                    holder.removeBtn.setImageResource(R.drawable.checked_image);
                    notifyDataSetChanged();
                }
            });
            holder.removeBtn.setVisibility(View.VISIBLE);

            if(url.equalsIgnoreCase(imageUrl)){
                holder.removeBtn.setImageResource(R.drawable.checked_image);
            }else {
                holder.removeBtn.setImageResource(R.drawable.unchecked_image);
            }
        }else {
            if (isEditMode) {
                holder.removeBtn.setVisibility(View.VISIBLE);
                holder.removeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (photosList.size() > 3) {
                            String profileImage = App.get().getDefaultAppSharedPreferences().getString(SharePreferenceKeyConstants.USER_PICTURE, "");
                            if (!profileImage.equalsIgnoreCase(imageUrl)) {
                                progressDialog.show();
                                App.get().getJobManager().addJob(new RemovePhotoJob(photosList.get(position).id, position));
                            } else {
                                photoListener.displaySnackBar(context.getString(R.string.cannot_delete_profile_pic));
                            }
                        } else {
                            photoListener.displaySnackBar(context.getString(R.string.cannot_delete_last_photo));
                        }
                    }
                });
            } else {
                holder.removeBtn.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        if (photosList != null && !photosList.isEmpty()) {
            return photosList.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.model_photo_view)
        ImageView modelPhotoView;

        @BindView(R.id.remove_photo_btn)
        ImageButton removeBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ManagePhotoListener {
        void displaySnackBar(String displayMsg);

        void retrieveSelectUrl(String url);
    }
}
