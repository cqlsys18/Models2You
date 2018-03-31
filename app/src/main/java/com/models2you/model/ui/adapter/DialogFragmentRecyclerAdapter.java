package com.models2you.model.ui.adapter;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.googledirection.model.Step;
import com.models2you.model.R;

import java.util.ArrayList;

/**
 * Created by chandrakant on 10/6/2016.
 */
public class DialogFragmentRecyclerAdapter extends RecyclerView.Adapter<DialogFragmentRecyclerAdapter.ViewHolder> {

    private static String TAG = DialogFragmentRecyclerAdapter.class.getSimpleName().toString();

    private Context context;
    private ArrayList<Step> directionModels;
    private CoordinatorLayout coordinatorLayout;

    public DialogFragmentRecyclerAdapter(Context context, ArrayList<Step> directionModels) {
        this.context = context;
        this.directionModels = directionModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_recycler_view_row, parent,
                false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Step directionModelstep = directionModels.get(position);
        String instructions = Html.fromHtml(directionModelstep.getHtmlInstruction()).toString().trim();
        holder.instructions.setText(instructions);
        String direction = directionModelstep.getManeuver();
        if(direction != null) {
            if (direction.equalsIgnoreCase("roundabout-left")) {
                holder.directionsImage.setImageResource(R.drawable.round_about_left);
            }
            if (direction.equalsIgnoreCase("roundabout-right")) {
                holder.directionsImage.setImageResource(R.drawable.round_about_right);
            }
            if (direction.equalsIgnoreCase("turn-left")) {
                holder.directionsImage.setImageResource(R.drawable.turn_left);
            }
            if (direction.equalsIgnoreCase("turn-right")) {
                holder.directionsImage.setImageResource(R.drawable.turn_right);
            }
            if (direction.equalsIgnoreCase("ferry")) {
                holder.directionsImage.setImageResource(R.drawable.ferry);
            }
            if (direction.equalsIgnoreCase("fork-left")) {
                holder.directionsImage.setImageResource(R.drawable.fork_left);
            }
            if (direction.equalsIgnoreCase("fork-right")) {
                holder.directionsImage.setImageResource(R.drawable.fork_right);
            }
            if (direction.equalsIgnoreCase("ramp-left")) {
                holder.directionsImage.setImageResource(R.drawable.ramp_left);
            }
            if (direction.equalsIgnoreCase("ramp-right")) {
                holder.directionsImage.setImageResource(R.drawable.ramp_right);
            }
            if (direction.equalsIgnoreCase("turn-sharp-right")) {
                holder.directionsImage.setImageResource(R.drawable.turn_sharp_right);
            }
            if (direction.equalsIgnoreCase("turn-sharp-left")) {
                holder.directionsImage.setImageResource(R.drawable.turn_sharp_left);
            }
            if (direction.equalsIgnoreCase("turn-slight-left")) {
                holder.directionsImage.setImageResource(R.drawable.turn_slight_left);
            }
            if (direction.equalsIgnoreCase("turn-slight-right")) {
                holder.directionsImage.setImageResource(R.drawable.turn_slight_right);
            }
            if (direction.equalsIgnoreCase("uturn-right")) {
                holder.directionsImage.setImageResource(R.drawable.uturn_right);
            }
            if (direction.equalsIgnoreCase("uturn-left")) {
                holder.directionsImage.setImageResource(R.drawable.uturn_left);
            }

        }
        String distance = directionModelstep.getDistance().getText().toLowerCase();
        String duration = directionModelstep.getDuration().getText().toLowerCase();
        holder.distance.setText("Distance: "+distance);
        holder.duration.setText("Time: "+duration);

    }

    @Override
    public int getItemCount() {
        return directionModels.size();
    }

    // Recipe Item Holder
    class ViewHolder extends RecyclerView.ViewHolder {


        private TextView instructions;
        private ImageView directionsImage;
        private TextView distance;
        private TextView duration;

        public ViewHolder(View itemView) {
            super(itemView);
            instructions = (TextView) itemView.findViewById(R.id.instructions);
            directionsImage = (ImageView) itemView.findViewById( R.id.directionsImage);
            distance = (TextView) itemView.findViewById(R.id.distanceMeter);
            duration = (TextView) itemView.findViewById(R.id.durationSeconds);

        }

    }

    public void setCoordinatorLayout(CoordinatorLayout coordinatorLayout) {
        this.coordinatorLayout = coordinatorLayout;
    }
}
