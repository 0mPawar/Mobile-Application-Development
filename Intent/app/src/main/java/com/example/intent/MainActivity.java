package com.example.intent;

// MainActivity.java — Intent Demo App
// Demonstrates:
//   1. Explicit Intent  — opens SecondActivity with putExtra() data
//   2. Implicit Intent  — opens websites, dialer, email, web search
//

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // ── Explicit Intent inputs ────────────────────────────────────────────
    EditText etName, etEmail, etMessage;
    Button   btnExplicit;

    // ── Implicit Intent — Website buttons ────────────────────────────────
    EditText etCustomUrl;
    Button   btnOpenCustomUrl;
    Button   btnGoogle, btnYouTube, btnWikipedia, btnGithub;

    // ── Implicit Intent — Other actions ──────────────────────────────────
    EditText etPhone, etEmailImplicit, etSearchQuery;
    Button   btnDial, btnEmail, btnSearch;

    // Request code for startActivityForResult (to get reply from SecondActivity)
    static final int REQUEST_CODE_SECOND = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----------------------------------------------------------------
        // Step 1: Link all components via findViewById()
        // ----------------------------------------------------------------

        // Explicit intent section
        etName      = findViewById(R.id.etName);
        etEmail     = findViewById(R.id.etEmail);
        etMessage   = findViewById(R.id.etMessage);
        btnExplicit = findViewById(R.id.btnExplicit);

        // Website section
        etCustomUrl      = findViewById(R.id.etCustomUrl);
        btnOpenCustomUrl = findViewById(R.id.btnOpenCustomUrl);
        btnGoogle    = findViewById(R.id.btnGoogle);
        btnYouTube   = findViewById(R.id.btnYouTube);
        btnWikipedia = findViewById(R.id.btnWikipedia);
        btnGithub    = findViewById(R.id.btnGithub);

        // Other implicit intents
        etPhone         = findViewById(R.id.etPhone);
        etEmailImplicit = findViewById(R.id.etEmailImplicit);
        etSearchQuery   = findViewById(R.id.etSearchQuery);
        btnDial   = findViewById(R.id.btnDial);
        btnEmail  = findViewById(R.id.btnEmail);
        btnSearch = findViewById(R.id.btnSearch);

        // ================================================================
        // A.  EXPLICIT INTENT
        //
        // An explicit intent specifies EXACTLY which component to open
        // by providing the full class name of the target Activity.
        //
        // Syntax:
        //   Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        //   intent.putExtra("key", value);  // bundle data to send
        //   startActivity(intent);          // or startActivityForResult()
        // ================================================================
        btnExplicit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name    = etName.getText().toString().trim();
                String email   = etEmail.getText().toString().trim();
                String message = etMessage.getText().toString().trim();

                // Validation
                if (name.isEmpty()) {
                    etName.setError("Name is required");
                    etName.requestFocus();
                    return;
                }

                // ── Create Explicit Intent ────────────────────────────
                // First argument  : source context (this activity)
                // Second argument : target class (SecondActivity.class)
                // This is "explicit" because we name the exact class.
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);

                // ── Bundle data using putExtra(key, value) ────────────
                // putExtra() attaches key-value pairs to the Intent.
                // SecondActivity reads them with getIntent().getStringExtra(key)
                intent.putExtra("NAME",    name);
                intent.putExtra("EMAIL",   email.isEmpty()   ? "Not provided" : email);
                intent.putExtra("MESSAGE", message.isEmpty() ? "No message"   : message);

                // ── Start the target Activity ─────────────────────────
                // startActivityForResult() allows SecondActivity to send
                // a result back via setResult() → onActivityResult() here.
                startActivityForResult(intent, REQUEST_CODE_SECOND);
            }
        });

        // ================================================================
        // B.  IMPLICIT INTENT — Open Websites
        //
        // An implicit intent does NOT name a specific component.
        // Instead it declares an ACTION and DATA, and Android finds
        // the best matching app (e.g. the default browser) to handle it.
        //
        // Syntax:
        //   Intent intent = new Intent(Intent.ACTION_VIEW);
        //   intent.setData(Uri.parse("https://www.google.com"));
        //   startActivity(intent);
        //
        // Intent.ACTION_VIEW — general "view this data" action
        // Uri.parse(url)     — wraps the URL string into a Uri object
        // ================================================================

        // Custom URL entered by user
        btnOpenCustomUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = etCustomUrl.getText().toString().trim();
                if (url.isEmpty()) {
                    etCustomUrl.setError("Please enter a URL");
                    return;
                }
                // Ensure the URL has a scheme (https://)
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://" + url;
                }
                openUrl(url);
            }
        });

        // Preset website buttons — each calls openUrl() with a fixed URL
        btnGoogle.setOnClickListener(v ->
                openUrl("https://www.google.com"));

        btnYouTube.setOnClickListener(v ->
                openUrl("https://www.youtube.com"));

        btnWikipedia.setOnClickListener(v ->
                openUrl("https://www.wikipedia.org"));

        btnGithub.setOnClickListener(v ->
                openUrl("https://www.github.com"));

        // ================================================================
        // C.  OTHER IMPLICIT INTENTS
        //
        // ACTION_DIAL    — opens the Phone dialer with a pre-filled number
        // ACTION_SENDTO  — opens the Email app with recipient pre-filled
        // ACTION_WEB_SEARCH — opens a web search for the given query
        // ================================================================

        // ── Dial Intent ───────────────────────────────────────────────────
        // Intent.ACTION_DIAL opens the dialer UI — does NOT make a call.
        // No CALL_PHONE permission needed (user still has to press dial).
        // tel: URI scheme is used for phone numbers.
        btnDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString().trim();
                if (phone.isEmpty()) {
                    etPhone.setError("Enter a phone number");
                    return;
                }
                // Intent.ACTION_DIAL — opens dialer without calling
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phone));
                startActivity(dialIntent);
            }
        });

        // ── Email Intent ──────────────────────────────────────────────────
        // Intent.ACTION_SENDTO opens the default email client.
        // mailto: URI scheme is used for email addresses.
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddr = etEmailImplicit.getText().toString().trim();
                if (emailAddr.isEmpty()) {
                    etEmailImplicit.setError("Enter an email address");
                    return;
                }
                // ACTION_SENDTO with mailto: URI
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + emailAddr));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hello from Intent Demo App");
                emailIntent.putExtra(Intent.EXTRA_TEXT,    "This email was sent via an implicit intent!");

                // Check if any app can handle this intent before launching
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                } else {
                    Toast.makeText(MainActivity.this,
                            "No email app found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ── Web Search Intent ─────────────────────────────────────────────
        // Intent.ACTION_WEB_SEARCH opens a web search for the given query.
        // SearchManager.QUERY is the key for the search string.
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = etSearchQuery.getText().toString().trim();
                if (query.isEmpty()) {
                    etSearchQuery.setError("Enter a search query");
                    return;
                }
                // ACTION_WEB_SEARCH uses SearchManager.QUERY as the extra key
                Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
                searchIntent.putExtra(SearchManager.QUERY, query);

                if (searchIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(searchIntent);
                } else {
                    // Fallback: open Google search via ACTION_VIEW
                    openUrl("https://www.google.com/search?q="
                            + Uri.encode(query));
                }
            }
        });
    }

    // ====================================================================
    // openUrl() — Helper for Implicit Intent to open a URL in the browser
    //
    // Steps:
    //   1. new Intent(Intent.ACTION_VIEW) — general view action
    //   2. intent.setData(Uri.parse(url)) — attach the URL as data
    //   3. startActivity(intent)          — Android picks the best browser
    // ====================================================================
    private void openUrl(String url) {
        // Create implicit intent with ACTION_VIEW
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // Uri.parse() converts the URL string to a Uri object
        intent.setData(Uri.parse(url));

        // resolveActivity() checks if any installed app can handle this intent
        // to avoid ActivityNotFoundException crash
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No browser found!", Toast.LENGTH_SHORT).show();
        }
    }

    // ====================================================================
    // onActivityResult() — receives the result sent back from SecondActivity
    //
    // Called when SecondActivity calls setResult() and finishes.
    // requestCode — matches REQUEST_CODE_SECOND we used in startActivityForResult
    // resultCode  — RESULT_OK or RESULT_CANCELED
    // data        — Intent with any extras sent back from SecondActivity
    // ====================================================================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SECOND
                && resultCode == RESULT_OK
                && data != null) {
            String reply = data.getStringExtra("REPLY");
            if (reply != null) {
                Toast.makeText(this,
                        "Reply from SecondActivity:\n" + reply,
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}