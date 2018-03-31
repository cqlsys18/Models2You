package com.models2you.model.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.models2you.model.R;
import com.models2you.model.app.Constants;
import com.models2you.model.model.BookingModel;
import com.models2you.model.util.LogFactory;
import com.models2you.model.util.Utils;


import java.util.List;

import butterknife.ButterKnife;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {
    private static final LogFactory.Log log = LogFactory.getLog(ReservationAdapter.class);

    private static final String CURRENT_DATE_TIME_FORMAT = "yyyy-mm-dd hh:mm:ss a";
    private static final String FORMAT_TO_CONVERT_DATE_TIME = "MMM dd, yyyy hh:mm a";
    private static final String FORMAT_TO_CONVERT_MONTH = "MMM dd";

    private final Context context;
    private List<BookingModel> previousBookings;
    private ReservationItemClickListener reservationItemClickListener;

    public ReservationAdapter(Context context) {
        this.context = context;
    }

    public void setReservationItemClickListener(ReservationItemClickListener reservationItemClickListener) {
        this.reservationItemClickListener = reservationItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_reservation_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookingModel bookings = previousBookings.get(position);
        log.verbose("booking " + bookings);
        if(bookings.appointmentTime!=null) {
            String dateStr = bookings.appointmentTime;
            log.verbose("booking dateStr " + dateStr);

            String formattedBookingDate = Utils.getLocalTimeZoneDateFormat(dateStr, Constants.desiredFormattedDateToSend, FORMAT_TO_CONVERT_DATE_TIME);
            log.verbose("booking formattedBookingDate " + formattedBookingDate);
            holder.bookingDate.setText(formattedBookingDate);
            String formattedCalDate = Utils.getLocalTimeZoneDateFormat(dateStr, Constants.desiredFormattedDateToSend, FORMAT_TO_CONVERT_MONTH);
            log.verbose("booking formattedCalDate " + formattedCalDate);
            holder.bookingDateWithCalendarBackGround.setText(formattedCalDate);
            holder.modelName.setText(bookings.name);
            holder.bookingAddress.setText(bookings.location);
            holder.reservationMainLinearLay.setId(bookings.id); // set id of booking to main layout
        }
        holder.reservationMainLinearLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reservationItemClickListener != null) {
                    int id = v.getId();
                    log.verbose("reservationMainLinearLay onClick id " + id);
                    reservationItemClickListener.onReservationItemClicked(id);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (previousBookings != null) {
            return previousBookings.size();
        }
        return 0;
    }

    public void setData(List<BookingModel> previousBookings) {
        this.previousBookings = previousBookings;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout reservationMainLinearLay;
        public TextView bookingDateWithCalendarBackGround;
        public TextView modelName;
        public TextView bookingDate;
        public TextView bookingAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            reservationMainLinearLay = (LinearLayout) itemView.findViewById(R.id.reservationMainLinearLayout);
            bookingDateWithCalendarBackGround = (TextView) itemView.findViewById(R.id.resCalendarDate);
            modelName = (TextView) itemView.findViewById(R.id.resModelName);
            bookingDate = (TextView) itemView.findViewById(R.id.resBookingDate);
            bookingAddress = (TextView) itemView.findViewById(R.id.resBookingAddress);
        }
    }

    public interface ReservationItemClickListener {
        void onReservationItemClicked(int position);
    }
}
