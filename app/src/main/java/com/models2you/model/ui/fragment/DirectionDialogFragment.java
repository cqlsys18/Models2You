package com.models2you.model.ui.fragment;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.akexorcist.googledirection.model.Step;
import com.models2you.model.R;
import com.models2you.model.ui.adapter.DialogFragmentRecyclerAdapter;
import com.models2you.model.util.Utils;

import java.util.ArrayList;

/**
 * Created by chandrakant on 10/6/2016.
 * Direction dialog fragment to show direction detail from
 * source to destination with symbols
 */
public class DirectionDialogFragment extends DialogFragment {
    private RecyclerView mRecyclerView;
    private DialogFragmentRecyclerAdapter adapter;
    private ImageView closeButton;
    // this method create view for your Dialog

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //inflate layout with recycler view
        View v = inflater.inflate(R.layout.fragment_dialog_direction, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.dialogRecyclerView);
        closeButton = (ImageView) v.findViewById(R.id.imageView_close);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        ArrayList<Step> stepsList = new ArrayList<>((Utils.getStepList()));
        DialogFragmentRecyclerAdapter dialogFragmentRecyclerAdapter = new DialogFragmentRecyclerAdapter(getActivity(),stepsList);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(dialogFragmentRecyclerAdapter);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            } });
        return v;
    }
}
