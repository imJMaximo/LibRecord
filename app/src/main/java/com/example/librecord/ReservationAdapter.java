package com.example.librecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationAdapter extends BaseAdapter {
    private Context context;
    private List<ReservationRecord> reservationList;

    public ReservationAdapter(Context context, List<ReservationRecord> reservationList) {
        this.context = context;
        this.reservationList = reservationList;
    }

    @Override
    public int getCount() {
        return reservationList.size();
    }

    @Override
    public Object getItem(int position) {
        return reservationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_reservation, parent, false);
        }

        ReservationRecord record = reservationList.get(position);

        TextView nameTextView = convertView.findViewById(R.id.text_view_name);
        TextView detailsTextView = convertView.findViewById(R.id.text_view_details);
        TextView status = convertView.findViewById(R.id.text_view_status);
        TextView positionView = convertView.findViewById(R.id.text_position);
        TextView reserveDate = convertView.findViewById(R.id.text_view_reserve);
        TextView returnDate = convertView.findViewById(R.id.text_view_return);


        positionView.setText(position+1+".");
        nameTextView.setText(record.getName());
        detailsTextView.setText(record.getDetails());
        status.setText(record.getStatus());
        reserveDate.setText(record.getDate());

        String dateString = record.getDate();

        // Parse the date string to a LocalDate
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }
        LocalDate date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = LocalDate.parse(dateString, formatter);
        }

        // Add 3 days to the date
        LocalDate newDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            newDate = date.plusDays(3);
        }

        // Format the new date back to a string
        String newDateString = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            newDateString = newDate.format(formatter);
        }
        returnDate.setText(newDateString);


        return convertView;
    }
}
