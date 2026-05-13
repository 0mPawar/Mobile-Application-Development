package com.example.e_commercelayouts;


import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // ── RelativeLayout section UI components ────────────────────────────
    Button btnMinus, btnPlus;
    Button btnAddToCart, btnBuyNow;
    TextView tvQtyCount;
    TextView tvFinalPrice;
    TextView tvOriginalPrice;   // strikethrough set in Java via Paint.STRIKE_THRU_TEXT_FLAG
    TextView tvWishlist;

    // ── State variables ──────────────────────────────────────────────────
    int quantity = 1;               // current quantity
    int unitPrice = 6299;            // price per unit in ₹
    boolean wishlisted = false;        // wishlist toggle state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----------------------------------------------------------------
        // Step 1: Link XML components to Java using findViewById()
        // ----------------------------------------------------------------
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        tvQtyCount = findViewById(R.id.tvQtyCount);
        tvFinalPrice = findViewById(R.id.tvFinalPrice);
        tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        tvWishlist = findViewById(R.id.tvWishlist);

        // Apply strikethrough to original price in Java
        // android:paintFlags is NOT a valid XML attribute —
        // Paint.STRIKE_THRU_TEXT_FLAG must be set programmatically
        tvOriginalPrice.setPaintFlags(
                tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
        );

        // ----------------------------------------------------------------
        // Step 2: MINUS button — decrease quantity (minimum 1)
        //         RelativeLayout positions this button relative to Qty label
        // ----------------------------------------------------------------
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    updatePriceAndQty();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Minimum quantity is 1", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ----------------------------------------------------------------
        // Step 3: PLUS button — increase quantity (maximum 10)
        // ----------------------------------------------------------------
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity < 10) {
                    quantity++;
                    updatePriceAndQty();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Maximum 10 units per order", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ----------------------------------------------------------------
        // Step 4: ADD TO CART button
        //         Demonstrates RelativeLayout: button is positioned using
        //         layout_alignParentStart + layout_toStartOf(BuyNow)
        // ----------------------------------------------------------------
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = quantity + " item(s) added to cart — ₹" + (unitPrice * quantity);
                Toast.makeText(MainActivity.this, "🛒 " + msg, Toast.LENGTH_LONG).show();
            }
        });

        // ----------------------------------------------------------------
        // Step 5: BUY NOW button
        //         Positioned using layout_alignParentEnd in RelativeLayout
        // ----------------------------------------------------------------
        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "⚡ Proceeding to checkout — ₹" + (unitPrice * quantity),
                        Toast.LENGTH_LONG).show();
            }
        });

        // ----------------------------------------------------------------
        // Step 6: Wishlist heart icon (FrameLayout overlay — bottom-right)
        //         Toggling the heart icon ON/OFF demonstrates how
        //         FrameLayout layers views that can be independently updated
        // ----------------------------------------------------------------
        tvWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wishlisted = !wishlisted;
                if (wishlisted) {
                    tvWishlist.setText("❤️");
                    Toast.makeText(MainActivity.this,
                            "Added to Wishlist ❤️", Toast.LENGTH_SHORT).show();
                } else {
                    tvWishlist.setText("🤍");
                    Toast.makeText(MainActivity.this,
                            "Removed from Wishlist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // ────────────────────────────────────────────────────────────────────
    // Helper: updates the quantity TextView and recalculates the price
    // The price shown in RelativeLayout updates dynamically with quantity
    // ────────────────────────────────────────────────────────────────────
    private void updatePriceAndQty() {
        tvQtyCount.setText(String.valueOf(quantity));
        int totalPrice = unitPrice * quantity;
        tvFinalPrice.setText("₹" + String.format("%,d", totalPrice));
    }
}