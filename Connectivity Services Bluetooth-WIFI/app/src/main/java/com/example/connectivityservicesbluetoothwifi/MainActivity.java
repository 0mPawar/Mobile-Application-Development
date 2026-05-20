package com.example.connectivityservicesbluetoothwifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // ── Bluetooth UI ──────────────────────────────────────────────────────
    TextView tvBTStatus, tvBTDeviceName, tvBTAddress;
    TextView tvPairedDevices, tvScannedDevices;
    Button   btnBTEnable, btnBTDisable, btnBTRefresh;
    Button   btnGetPaired, btnBTScan, btnBTDiscoverable;

    // ── Wi-Fi UI ──────────────────────────────────────────────────────────
    TextView tvWifiStatus, tvWifiSSID, tvWifiIP, tvWifiSpeed;
    Button   btnWifiEnable, btnWifiDisable, btnWifiRefresh, btnWifiSettings;

    // ── Network Info UI ───────────────────────────────────────────────────
    TextView tvNetworkInfo;
    Button   btnNetworkRefresh;

    // ── Bluetooth system service ──────────────────────────────────────────
    // BluetoothAdapter.getDefaultAdapter() returns the device's BT adapter.
    // Returns null if the device does not support Bluetooth.
    BluetoothAdapter bluetoothAdapter;

    // ── Wi-Fi system service ──────────────────────────────────────────────
    // WifiManager is retrieved from the system service registry.
    WifiManager wifiManager;

    // ── ConnectivityManager — for overall network status ─────────────────
    ConnectivityManager connectivityManager;

    // ── Request codes for permissions and BT enable ───────────────────────
    static final int REQUEST_ENABLE_BT          = 1;
    static final int REQUEST_DISCOVERABLE_BT    = 2;
    static final int REQUEST_PERMISSIONS        = 3;

    // StringBuilder to accumulate scanned device names
    StringBuilder scannedDevicesLog = new StringBuilder();

    // ====================================================================
    // BroadcastReceiver — listens for Bluetooth state changes and
    // newly discovered devices during a scan.
    //
    // BroadcastReceiver is registered with an IntentFilter so Android
    // calls onReceive() when matching broadcasts are sent by the system.
    // ====================================================================
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action == null) return;

            switch (action) {

                // ── Bluetooth state changed (ON / OFF / TURNING_ON etc.) ─
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(
                            BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR);
                    updateBTStatusUI(state);
                    break;

                // ── A new device found during discovery scan ─────────────
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        device = intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE,
                                BluetoothDevice.class);
                    } else {
                        device = intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE);
                    }
                    if (device != null) {
                        appendScannedDevice(device);
                    }
                    break;

                // ── Discovery finished ────────────────────────────────────
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Toast.makeText(context,
                            "Scan complete!", Toast.LENGTH_SHORT).show();
                    if (scannedDevicesLog.length() == 0) {
                        tvScannedDevices.setText("No nearby devices found.");
                    }
                    break;

                // ── Wi-Fi state changed ───────────────────────────────────
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    int wifiState = intent.getIntExtra(
                            WifiManager.EXTRA_WIFI_STATE,
                            WifiManager.WIFI_STATE_UNKNOWN);
                    updateWifiStatusUI(wifiState);
                    break;

                // ── Wi-Fi network connection changed ──────────────────────
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    refreshWifiInfo();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----------------------------------------------------------------
        // Step 1: Link all XML views to Java
        // ----------------------------------------------------------------
        tvBTStatus      = findViewById(R.id.tvBTStatus);
        tvBTDeviceName  = findViewById(R.id.tvBTDeviceName);
        tvBTAddress     = findViewById(R.id.tvBTAddress);
        tvPairedDevices = findViewById(R.id.tvPairedDevices);
        tvScannedDevices= findViewById(R.id.tvScannedDevices);
        btnBTEnable     = findViewById(R.id.btnBTEnable);
        btnBTDisable    = findViewById(R.id.btnBTDisable);
        btnBTRefresh    = findViewById(R.id.btnBTRefresh);
        btnGetPaired    = findViewById(R.id.btnGetPaired);
        btnBTScan       = findViewById(R.id.btnBTScan);
        btnBTDiscoverable = findViewById(R.id.btnBTDiscoverable);

        tvWifiStatus  = findViewById(R.id.tvWifiStatus);
        tvWifiSSID    = findViewById(R.id.tvWifiSSID);
        tvWifiIP      = findViewById(R.id.tvWifiIP);
        tvWifiSpeed   = findViewById(R.id.tvWifiSpeed);
        btnWifiEnable = findViewById(R.id.btnWifiEnable);
        btnWifiDisable= findViewById(R.id.btnWifiDisable);
        btnWifiRefresh= findViewById(R.id.btnWifiRefresh);
        btnWifiSettings=findViewById(R.id.btnWifiSettings);

        tvNetworkInfo     = findViewById(R.id.tvNetworkInfo);
        btnNetworkRefresh = findViewById(R.id.btnNetworkRefresh);

        // ----------------------------------------------------------------
        // Step 2: Initialise system services
        //
        // BluetoothAdapter.getDefaultAdapter()
        //   Returns the BluetoothAdapter for the default Bluetooth hardware.
        //   Returns null if the device does not have Bluetooth support.
        //
        // getSystemService(Context.WIFI_SERVICE)
        //   Returns the WifiManager for managing Wi-Fi connectivity.
        //
        // getSystemService(Context.CONNECTIVITY_SERVICE)
        //   Returns ConnectivityManager for querying network state.
        // ----------------------------------------------------------------
        bluetoothAdapter    = BluetoothAdapter.getDefaultAdapter();
        wifiManager         = (WifiManager)
                getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // ----------------------------------------------------------------
        // Step 3: Request runtime permissions (API 23+)
        // ----------------------------------------------------------------
        requestRequiredPermissions();

        // ----------------------------------------------------------------
        // Step 4: Register BroadcastReceiver for BT and WiFi state events
        //
        // IntentFilter tells Android which broadcasts to deliver.
        // Multiple actions can be added to a single filter.
        // ----------------------------------------------------------------
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(bluetoothReceiver, filter);

        // ----------------------------------------------------------------
        // Step 5: Initial status refresh
        // ----------------------------------------------------------------
        refreshAllStatus();

        // ================================================================
        //  BLUETOOTH BUTTON HANDLERS
        // ================================================================

        // Enable Bluetooth
        // ACTION_REQUEST_ENABLE shows a system dialog asking the user
        // to enable Bluetooth. Result is in onActivityResult().
        btnBTEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter == null) {
                    Toast.makeText(MainActivity.this,
                            "Bluetooth not supported", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!bluetoothAdapter.isEnabled()) {
                    Intent turnOn = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn, REQUEST_ENABLE_BT);
                } else {
                    Toast.makeText(MainActivity.this,
                            "Bluetooth is already ON",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Disable Bluetooth
        // bluetoothAdapter.disable() turns Bluetooth off programmatically.
        // Requires BLUETOOTH_ADMIN permission (pre-API 31)
        // or BLUETOOTH_CONNECT permission (API 31+).
        btnBTDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        // API 31+: open settings, programmatic disable not allowed
                        Toast.makeText(MainActivity.this,
                                "Please disable Bluetooth from Settings on Android 12+",
                                Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                    } else {
                        bluetoothAdapter.disable();
                        Toast.makeText(MainActivity.this,
                                "Bluetooth disabling...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this,
                            "Bluetooth is already OFF",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBTRefresh.setOnClickListener(v -> refreshBluetoothInfo());

        // Get Paired Devices
        // getBondedDevices() returns a Set<BluetoothDevice> of all
        // devices that have been paired with this device before.
        btnGetPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter == null) {
                    tvPairedDevices.setText("Bluetooth not supported.");
                    return;
                }
                if (!bluetoothAdapter.isEnabled()) {
                    tvPairedDevices.setText(
                            "Enable Bluetooth first to see paired devices.");
                    return;
                }

                // getBondedDevices() — returns all previously paired devices
                Set<BluetoothDevice> pairedDevices =
                        bluetoothAdapter.getBondedDevices();

                if (pairedDevices.isEmpty()) {
                    tvPairedDevices.setText("No paired devices found.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    int i = 1;
                    for (BluetoothDevice d : pairedDevices) {
                        sb.append(i++).append(". ")
                                .append(getDeviceName(d))
                                .append("\n   ")
                                .append(d.getAddress())
                                .append("\n\n");
                    }
                    tvPairedDevices.setText(sb.toString().trim());
                }
            }
        });

        // Scan for nearby Bluetooth devices
        // startDiscovery() initiates a 12-second scan.
        // Results arrive via the BroadcastReceiver (ACTION_FOUND).
        btnBTScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter == null) return;
                if (!bluetoothAdapter.isEnabled()) {
                    Toast.makeText(MainActivity.this,
                            "Enable Bluetooth before scanning",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // Cancel any ongoing discovery before starting a new one
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                scannedDevicesLog.setLength(0);
                tvScannedDevices.setText("Scanning... (12 seconds)");
                bluetoothAdapter.startDiscovery();
                Toast.makeText(MainActivity.this,
                        "Scanning for devices...", Toast.LENGTH_SHORT).show();
            }
        });

        // Make device discoverable to other BT devices
        // ACTION_REQUEST_DISCOVERABLE shows a dialog.
        // EXTRA_DISCOVERABLE_DURATION sets how long (seconds).
        btnBTDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent discoverableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(
                        BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                startActivityForResult(discoverableIntent,
                        REQUEST_DISCOVERABLE_BT);
            }
        });

        // ================================================================
        //  WI-FI BUTTON HANDLERS
        // ================================================================

        btnWifiEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiManager == null) return;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // API 29+: programmatic enable/disable is deprecated.
                    // Must open Wi-Fi settings panel instead.
                    Toast.makeText(MainActivity.this,
                            "Opening Wi-Fi Settings (Android 10+)",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                } else {
                    // Pre-API 29: setWifiEnabled() works directly
                    wifiManager.setWifiEnabled(true);
                    Toast.makeText(MainActivity.this,
                            "Wi-Fi enabling...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnWifiDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiManager == null) return;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Toast.makeText(MainActivity.this,
                            "Opening Wi-Fi Settings (Android 10+)",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                } else {
                    wifiManager.setWifiEnabled(false);
                    Toast.makeText(MainActivity.this,
                            "Wi-Fi disabling...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnWifiRefresh.setOnClickListener(v -> refreshWifiInfo());

        // Open system Wi-Fi settings via implicit intent
        btnWifiSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        btnNetworkRefresh.setOnClickListener(v -> refreshAllStatus());
    }

    // ====================================================================
    // refreshAllStatus() — refreshes Bluetooth, Wi-Fi and network info
    // ====================================================================
    private void refreshAllStatus() {
        refreshBluetoothInfo();
        refreshWifiInfo();
        refreshNetworkInfo();
    }

    // ====================================================================
    // refreshBluetoothInfo() — reads BluetoothAdapter state and updates UI
    // ====================================================================
    @SuppressLint("MissingPermission")
    private void refreshBluetoothInfo() {
        if (bluetoothAdapter == null) {
            tvBTStatus.setText("Not Supported");
            tvBTStatus.setTextColor(0xFFE53935);
            tvBTDeviceName.setText("N/A");
            tvBTAddress.setText("N/A");
            return;
        }

        // Update status chip
        updateBTStatusUI(bluetoothAdapter.getState());

        // Get device name — requires BLUETOOTH_CONNECT on API 31+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.BLUETOOTH_CONNECT)
                        == PackageManager.PERMISSION_GRANTED) {
            tvBTDeviceName.setText(bluetoothAdapter.getName());
            tvBTAddress.setText(bluetoothAdapter.getAddress());
        } else {
            tvBTDeviceName.setText("Permission needed");
            tvBTAddress.setText("Permission needed");
        }
    }

    // ====================================================================
    // updateBTStatusUI() — maps BluetoothAdapter state int to a status label
    // ====================================================================
    private void updateBTStatusUI(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_ON:
                tvBTStatus.setText("ON ✅");
                tvBTStatus.setTextColor(0xFF388E3C);
                tvBTStatus.setBackgroundColor(0xFFE8F5E9);
                break;
            case BluetoothAdapter.STATE_OFF:
                tvBTStatus.setText("OFF ❌");
                tvBTStatus.setTextColor(0xFFE53935);
                tvBTStatus.setBackgroundColor(0xFFFFEBEE);
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                tvBTStatus.setText("Turning ON...");
                tvBTStatus.setTextColor(0xFFF57F17);
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                tvBTStatus.setText("Turning OFF...");
                tvBTStatus.setTextColor(0xFFF57F17);
                break;
            default:
                tvBTStatus.setText("Unknown");
                tvBTStatus.setTextColor(0xFF888888);
        }
    }

    // ====================================================================
    // refreshWifiInfo() — reads WifiManager state and updates UI
    // ====================================================================
    @SuppressLint("MissingPermission")
    private void refreshWifiInfo() {
        if (wifiManager == null) {
            tvWifiStatus.setText("Not Supported");
            return;
        }

        int wifiState = wifiManager.getWifiState();
        updateWifiStatusUI(wifiState);

        // WifiInfo — detailed info about the current connection
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null && wifiManager.isWifiEnabled()) {
            // SSID — name of the connected network
            String ssid = wifiInfo.getSSID();
            // Remove surrounding quotes Android adds to the SSID string
            if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            tvWifiSSID.setText(ssid != null && !ssid.equals("<unknown ssid>")
                    ? ssid : "Not connected");

            // IP Address — stored as a 32-bit integer, needs formatting
            // Formatter.formatIpAddress() converts int → "192.168.1.x"
            int ipInt = wifiInfo.getIpAddress();
            String ipStr = android.text.format.Formatter.formatIpAddress(ipInt);
            tvWifiIP.setText(ipStr.equals("0.0.0.0") ? "Not assigned" : ipStr);

            // Link speed in Mbps
            int speed = wifiInfo.getLinkSpeed();
            tvWifiSpeed.setText(speed > 0 ? speed + " Mbps" : "—");
        } else {
            tvWifiSSID.setText("Not connected");
            tvWifiIP.setText("—");
            tvWifiSpeed.setText("—");
        }
    }

    // ====================================================================
    // updateWifiStatusUI() — maps WifiManager state int to a status label
    // ====================================================================
    private void updateWifiStatusUI(int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_ENABLED:
                tvWifiStatus.setText("ON ✅");
                tvWifiStatus.setTextColor(0xFF388E3C);
                tvWifiStatus.setBackgroundColor(0xFFE8F5E9);
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                tvWifiStatus.setText("OFF ❌");
                tvWifiStatus.setTextColor(0xFFE53935);
                tvWifiStatus.setBackgroundColor(0xFFFFEBEE);
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                tvWifiStatus.setText("Enabling...");
                tvWifiStatus.setTextColor(0xFFF57F17);
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                tvWifiStatus.setText("Disabling...");
                tvWifiStatus.setTextColor(0xFFF57F17);
                break;
            default:
                tvWifiStatus.setText("Unknown");
                tvWifiStatus.setTextColor(0xFF888888);
        }
    }

    // ====================================================================
    // refreshNetworkInfo() — reads ConnectivityManager for overall state
    // ====================================================================
    private void refreshNetworkInfo() {
        if (connectivityManager == null) return;

        StringBuilder sb = new StringBuilder();

        // NetworkInfo provides type and state of the active network
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            sb.append("Connected   : YES ✅\n");
            sb.append("Type        : ").append(activeNetwork.getTypeName()).append("\n");
            sb.append("Subtype     : ").append(activeNetwork.getSubtypeName()).append("\n");
            sb.append("State       : ").append(activeNetwork.getState()).append("\n");
            sb.append("Roaming     : ").append(activeNetwork.isRoaming() ? "Yes" : "No").append("\n");
        } else {
            sb.append("Connected   : NO ❌\n");
            sb.append("No active network connection.\n");
        }

        // Check individual connection types
        NetworkInfo wifiNet = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNet = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE);

        sb.append("\nWi-Fi Net   : ")
                .append(wifiNet != null && wifiNet.isConnected() ? "Connected" : "Not connected")
                .append("\n");
        sb.append("Mobile Net  : ")
                .append(mobileNet != null && mobileNet.isConnected() ? "Connected" : "Not connected");

        tvNetworkInfo.setText(sb.toString());
    }

    // ====================================================================
    // appendScannedDevice() — adds a discovered device to the scan log
    // ====================================================================
    @SuppressLint("MissingPermission")
    private void appendScannedDevice(BluetoothDevice device) {
        scannedDevicesLog
                .append("• ").append(getDeviceName(device))
                .append("\n  ").append(device.getAddress())
                .append("\n\n");
        tvScannedDevices.setText(scannedDevicesLog.toString().trim());
    }

    // ====================================================================
    // getDeviceName() — safely reads device name with permission check
    // ====================================================================
    @SuppressLint("MissingPermission")
    private String getDeviceName(BluetoothDevice device) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.BLUETOOTH_CONNECT)
                        == PackageManager.PERMISSION_GRANTED) {
            String name = device.getName();
            return (name != null && !name.isEmpty()) ? name : "Unknown Device";
        }
        return "Unknown Device";
    }

    // ====================================================================
    // requestRequiredPermissions() — asks user for runtime permissions
    // ====================================================================
    private void requestRequiredPermissions() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // API 31+: new granular BT permissions
            permissions = new String[]{
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
        } else {
            permissions = new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
        }

        boolean allGranted = true;
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions,
                    REQUEST_PERMISSIONS);
        }
    }

    // ====================================================================
    // onRequestPermissionsResult() — handles user response to permission dialog
    // ====================================================================
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            refreshAllStatus();
        }
    }

    // ====================================================================
    // onActivityResult() — handles results from BT enable/discoverable dialogs
    // ====================================================================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth enabled!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth enable denied", Toast.LENGTH_SHORT).show();
            }
            refreshBluetoothInfo();
        } else if (requestCode == REQUEST_DISCOVERABLE_BT) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Discoverable request denied",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Device discoverable for " + resultCode + " seconds",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ====================================================================
    // onResume() / onPause() — manage BroadcastReceiver registration
    //
    // Best practice: unregister in onPause() to avoid receiving
    // broadcasts when the Activity is not visible (saves battery).
    // Re-register in onResume() so we catch updates when visible.
    // ====================================================================
    @Override
    protected void onResume() {
        super.onResume();
        refreshAllStatus();
    }

    // ====================================================================
    // onDestroy() — MUST unregister BroadcastReceiver to prevent leaks
    // ====================================================================
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel ongoing BT discovery to save battery
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.BLUETOOTH_SCAN)
                            == PackageManager.PERMISSION_GRANTED) {
                bluetoothAdapter.cancelDiscovery();
            }
        }
        // Unregister receiver — prevents "Activity has leaked IntentReceiver" error
        unregisterReceiver(bluetoothReceiver);
    }
}