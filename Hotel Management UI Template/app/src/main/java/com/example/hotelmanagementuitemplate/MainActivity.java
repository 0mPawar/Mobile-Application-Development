package com.example.hotelmanagementuitemplate;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // ── Tab buttons ───────────────────────────────────────────────────────
    TextView tabRooms, tabBookings, tabInfo;

    // ── Content sections ──────────────────────────────────────────────────
    LinearLayout sectionRooms, sectionBookings;
    ScrollView sectionInfo;

    // ── GridView (Rooms) ──────────────────────────────────────────────────
    GridView gridViewRooms;

    // ── ListView (Bookings) ───────────────────────────────────────────────
    ListView listViewBookings;

    // ── Header date ───────────────────────────────────────────────────────
    TextView tvDate;

    // ====================================================================
    // ROOM DATA — used to populate the GridView via RoomAdapter
    // ====================================================================
    String[] roomNumbers = {
            "Room 101", "Room 102", "Room 103", "Room 104",
            "Room 201", "Room 202", "Room 203", "Room 204",
            "Room 301", "Room 302", "Room 303", "Room 304"
    };
    String[] roomTypes = {
            "Standard", "Standard", "Deluxe", "Deluxe",
            "Deluxe", "Suite", "Suite", "Deluxe",
            "Suite", "Penthouse", "Standard", "Deluxe"
    };
    String[] roomPrices = {
            "₹1,500/night", "₹1,500/night", "₹2,500/night", "₹2,500/night",
            "₹2,500/night", "₹5,000/night", "₹5,000/night", "₹2,500/night",
            "₹5,000/night", "₹9,999/night", "₹1,500/night", "₹2,500/night"
    };
    String[] roomStatuses = {
            "Available", "Occupied", "Available", "Maintenance",
            "Occupied", "Available", "Occupied", "Available",
            "Available", "Occupied", "Maintenance", "Available"
    };

    // ====================================================================
    // BOOKING DATA — used to populate the ListView via BookingAdapter
    // ====================================================================
    String[] guestNames = {
            "Rahul Sharma", "Priya Patel", "Amit Desai",
            "Sneha Kulkarni", "Vikas Mehta", "Anjali Joshi",
            "Rohit Verma", "Kavita Nair", "Suresh Rao",
            "Meena Iyer"
    };
    String[] roomDetails = {
            "Room 202 · Suite", "Room 103 · Deluxe", "Room 301 · Suite",
            "Room 102 · Standard", "Room 201 · Deluxe", "Room 304 · Deluxe",
            "Room 104 · Deluxe", "Room 302 · Penthouse", "Room 101 · Standard",
            "Room 203 · Suite"
    };
    String[] dates = {
            "14 Mar – 17 Mar 2026", "13 Mar – 15 Mar 2026", "12 Mar – 18 Mar 2026",
            "14 Mar – 16 Mar 2026", "10 Mar – 14 Mar 2026", "15 Mar – 20 Mar 2026",
            "14 Mar – 19 Mar 2026", "13 Mar – 14 Mar 2026", "11 Mar – 13 Mar 2026",
            "16 Mar – 21 Mar 2026"
    };
    String[] statuses = {
            "Confirmed", "Checked In", "Checked In",
            "Confirmed", "Checked Out", "Confirmed",
            "Confirmed", "Checked Out", "Cancelled",
            "Confirmed"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----------------------------------------------------------------
        // Step 1: Link XML components to Java using findViewById()
        // ----------------------------------------------------------------
        tabRooms = findViewById(R.id.tabRooms);
        tabBookings = findViewById(R.id.tabBookings);
        tabInfo = findViewById(R.id.tabInfo);

        sectionRooms = findViewById(R.id.sectionRooms);
        sectionBookings = findViewById(R.id.sectionBookings);
        sectionInfo = findViewById(R.id.sectionInfo);

        gridViewRooms = findViewById(R.id.gridViewRooms);
        listViewBookings = findViewById(R.id.listViewBookings);
        tvDate = findViewById(R.id.tvDate);

        // Set today's date in the header
        String today = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        tvDate.setText(today);

        // ----------------------------------------------------------------
        // Step 2: Set up GridView with RoomAdapter (custom BaseAdapter)
        //         GridView displays room cards in a 2-column grid.
        //         RoomAdapter inflates grid_room_item.xml for each cell.
        // ----------------------------------------------------------------
        RoomAdapter roomAdapter = new RoomAdapter(
                this, roomNumbers, roomTypes, roomPrices, roomStatuses
        );
        gridViewRooms.setAdapter(roomAdapter);

        // GridView item click listener
        gridViewRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = roomNumbers[position] + " — " + roomTypes[position]
                        + "\n" + roomPrices[position]
                        + " · " + roomStatuses[position];
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        // ----------------------------------------------------------------
        // Step 3: Set up ListView with BookingAdapter (custom BaseAdapter)
        //         ListView displays each booking as a row using
        //         list_booking_item.xml inflated in BookingAdapter.getView()
        // ----------------------------------------------------------------
        BookingAdapter bookingAdapter = new BookingAdapter(
                this, guestNames, roomDetails, dates, statuses
        );
        listViewBookings.setAdapter(bookingAdapter);

        // ListView row click listener
        listViewBookings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = "Guest: " + guestNames[position]
                        + "\n" + roomDetails[position]
                        + "\n" + dates[position]
                        + "\nStatus: " + statuses[position];
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });

        // ----------------------------------------------------------------
        // Step 4: Tab click listeners — show/hide sections using VISIBLE/GONE
        // ----------------------------------------------------------------
        tabRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(0);
            }
        });

        tabBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(1);
            }
        });

        tabInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(2);
            }
        });
    }

    // ----------------------------------------------------------------
    // Helper: switch between the 3 sections using View.VISIBLE / View.GONE
    // Also updates tab highlight styling
    // ----------------------------------------------------------------
    private void switchTab(int index) {
        // Hide all sections first
        sectionRooms.setVisibility(View.GONE);
        sectionBookings.setVisibility(View.GONE);
        sectionInfo.setVisibility(View.GONE);

        // Reset all tab styles
        tabRooms.setTextColor(0xFF888888);
        tabRooms.setBackgroundColor(0xFFFFFFFF);
        tabBookings.setTextColor(0xFF888888);
        tabBookings.setBackgroundColor(0xFFFFFFFF);
        tabInfo.setTextColor(0xFF888888);
        tabInfo.setBackgroundColor(0xFFFFFFFF);

        // Show selected section and highlight tab
        if (index == 0) {
            sectionRooms.setVisibility(View.VISIBLE);
            tabRooms.setTextColor(0xFF3F51B5);
            tabRooms.setBackgroundColor(0xFFE8EAF6);
        } else if (index == 1) {
            sectionBookings.setVisibility(View.VISIBLE);
            tabBookings.setTextColor(0xFF3F51B5);
            tabBookings.setBackgroundColor(0xFFE8EAF6);
        } else {
            sectionInfo.setVisibility(View.VISIBLE);
            tabInfo.setTextColor(0xFF3F51B5);
            tabInfo.setBackgroundColor(0xFFE8EAF6);
        }
    }
}