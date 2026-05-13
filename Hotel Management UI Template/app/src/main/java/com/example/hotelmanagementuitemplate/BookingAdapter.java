package com.example.hotelmanagementuitemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookingAdapter extends BaseAdapter {

    private Context  context;
    private String[] guestNames;
    private String[] roomDetails;
    private String[] dates;
    private String[] statuses;

    public BookingAdapter(Context context,
                          String[] guestNames,
                          String[] roomDetails,
                          String[] dates,
                          String[] statuses) {
        this.context     = context;
        this.guestNames  = guestNames;
        this.roomDetails = roomDetails;
        this.dates       = dates;
        this.statuses    = statuses;
    }

    @Override
    public int getCount()              { return guestNames.length; }

    @Override
    public Object getItem(int pos)     { return guestNames[pos]; }

    @Override
    public long getItemId(int pos)     { return pos; }

    // getView() — inflates list_booking_item.xml and fills it with data
    // Uses ViewHolder pattern for view recycling efficiency
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_booking_item, parent, false);

            holder = new ViewHolder();
            holder.ivAvatar         = convertView.findViewById(R.id.ivGuestAvatar);
            holder.tvGuestName      = convertView.findViewById(R.id.tvGuestName);
            holder.tvBookingRoom    = convertView.findViewById(R.id.tvBookingRoom);
            holder.tvBookingDates   = convertView.findViewById(R.id.tvBookingDates);
            holder.tvBookingStatus  = convertView.findViewById(R.id.tvBookingStatus);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Populate row with data
        holder.tvGuestName.setText(guestNames[position]);
        holder.tvBookingRoom.setText(roomDetails[position]);
        holder.tvBookingDates.setText("📅 " + dates[position]);
        holder.tvBookingStatus.setText(statuses[position]);

        // Alternate row background for readability
        if (position % 2 == 0) {
            convertView.setBackgroundColor(0xFFFFFFFF);  // white
        } else {
            convertView.setBackgroundColor(0xFFF9F9FF);  // very light blue
        }

        // Color-code the status badge
        String status = statuses[position];
        if (status.equals("Confirmed")) {
            holder.tvBookingStatus.setBackgroundColor(0xFF4CAF50);  // green
        } else if (status.equals("Checked In")) {
            holder.tvBookingStatus.setBackgroundColor(0xFF3F51B5);  // indigo
        } else if (status.equals("Checked Out")) {
            holder.tvBookingStatus.setBackgroundColor(0xFF888888);  // grey
        } else if (status.equals("Cancelled")) {
            holder.tvBookingStatus.setBackgroundColor(0xFFF44336);  // red
        } else {
            holder.tvBookingStatus.setBackgroundColor(0xFFFF9800);  // orange
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView ivAvatar;
        TextView  tvGuestName;
        TextView  tvBookingRoom;
        TextView  tvBookingDates;
        TextView  tvBookingStatus;
    }
}