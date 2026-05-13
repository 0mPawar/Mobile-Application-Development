package com.example.hotelmanagementuitemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RoomAdapter extends BaseAdapter {

    private Context  context;
    private String[] roomNumbers;   // e.g. "Room 101"
    private String[] roomTypes;     // e.g. "Deluxe"
    private String[] roomPrices;    // e.g. "₹2,500/night"
    private String[] roomStatuses;  // e.g. "Available" / "Occupied" / "Maintenance"

    // Constructor — receives context and data arrays from MainActivity
    public RoomAdapter(Context context,
                       String[] roomNumbers,
                       String[] roomTypes,
                       String[] roomPrices,
                       String[] roomStatuses) {
        this.context      = context;
        this.roomNumbers  = roomNumbers;
        this.roomTypes    = roomTypes;
        this.roomPrices   = roomPrices;
        this.roomStatuses = roomStatuses;
    }

    // ── BaseAdapter required methods ────────────────────────────────────

    // Returns total number of items in the grid
    @Override
    public int getCount() {
        return roomNumbers.length;
    }

    // Returns data item at a given position
    @Override
    public Object getItem(int position) {
        return roomNumbers[position];
    }

    // Returns unique ID for item at position
    @Override
    public long getItemId(int position) {
        return position;
    }

    // ── getView() — called for EACH grid cell ───────────────────────────
    // This is the key method: inflates grid_room_item.xml and populates it.
    // convertView is a recycled view (View Recycling) — reuse if not null
    // to avoid inflating a new layout every time (improves performance).
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // ViewHolder pattern: cache view references to avoid repeated
        // findViewById() calls, which are expensive on large lists
        ViewHolder holder;

        if (convertView == null) {
            // Inflate the grid_room_item.xml layout for a new cell
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.grid_room_item, parent, false);

            // Create and store ViewHolder
            holder = new ViewHolder();
            holder.ivRoomIcon   = convertView.findViewById(R.id.ivRoomIcon);
            holder.tvRoomNumber = convertView.findViewById(R.id.tvRoomNumber);
            holder.tvRoomType   = convertView.findViewById(R.id.tvRoomType);
            holder.tvRoomPrice  = convertView.findViewById(R.id.tvRoomPrice);
            holder.tvRoomStatus = convertView.findViewById(R.id.tvRoomStatus);

            convertView.setTag(holder);  // attach holder to recycled view
        } else {
            // Reuse the recycled view — retrieve stored holder
            holder = (ViewHolder) convertView.getTag();
        }

        // ── Populate the views with data for this position ──────────────
        holder.tvRoomNumber.setText(roomNumbers[position]);
        holder.tvRoomType.setText(roomTypes[position]);
        holder.tvRoomPrice.setText(roomPrices[position]);
        holder.tvRoomStatus.setText(roomStatuses[position]);

        // Set room icon based on type
        String type = roomTypes[position];
        if (type.equals("Suite")) {
            holder.ivRoomIcon.setImageResource(android.R.drawable.ic_menu_compass);
        } else if (type.equals("Deluxe")) {
            holder.ivRoomIcon.setImageResource(android.R.drawable.ic_menu_myplaces);
        } else {
            holder.ivRoomIcon.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Set status badge color based on status string
        String status = roomStatuses[position];
        if (status.equals("Available")) {
            holder.tvRoomStatus.setBackgroundColor(0xFF4CAF50);  // green
        } else if (status.equals("Occupied")) {
            holder.tvRoomStatus.setBackgroundColor(0xFFF44336);  // red
        } else {
            holder.tvRoomStatus.setBackgroundColor(0xFFFF9800);  // orange
        }

        return convertView;
    }

    // ViewHolder: static inner class that caches view references per cell
    static class ViewHolder {
        ImageView ivRoomIcon;
        TextView  tvRoomNumber;
        TextView  tvRoomType;
        TextView  tvRoomPrice;
        TextView  tvRoomStatus;
    }
}