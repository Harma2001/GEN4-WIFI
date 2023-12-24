package com.yana.yanagen4;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    Button button_scan,button_clean;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_scan = (Button) findViewById(R.id.button_scan);
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        AtomicReference<ListView> wifi_list = new AtomicReference<>(findViewById(R.id.wifi_list));
        Timer timer = new Timer();

        Handler handler = new Handler(msg -> {
            if(msg.what == 1) {
                //Permission Check
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},  1);
                }
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "No location permission, unable to obtain Wi Fi information", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (!wifiMgr.isWifiEnabled()) {
                    Toast.makeText(MainActivity.this, "Please turn on the Wi-Fi switch to scan", Toast.LENGTH_SHORT).show();
                    return false;
                }
                button_scan.setText("REFRESH");
                List<ScanResult> results = wifiMgr.getScanResults();
                //results.sort(Comparator.comparingInt(a -> -a.level));
                wifi_list.set(findViewById(R.id.wifi_list));
                if (wifi_list.get().getAdapter() == null)
                    wifi_list.get().setAdapter(new WifiInfoAdapter(results, MainActivity.this));
                else
                    ((WifiInfoAdapter)wifi_list.get().getAdapter()).setData(results);
            }
            return false;
        });
        AtomicReference<TimerTask> refresh = new AtomicReference<>();

        // Add "Scan" button event handler
        button_scan.setOnClickListener(v -> {
            if(Objects.equals(button_scan.getText(), "REFRESH")) {
                Message msg = handler.obtainMessage();
                msg.what = 1;
                handler.sendMessage(msg);
                return;
            }
            refresh.set(new TimerTask() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            });
            timer.schedule(refresh.get(), 0 ,2000);

        });

        //Add a "Clear" button event handler
        button_clean = (Button) findViewById(R.id.button_clean);
        button_clean.setOnClickListener(v -> {
            if(Objects.equals(button_scan.getText(), "scanning"))
                return;
            refresh.get().cancel();
            button_scan.setText("SCAN");
            wifi_list.set(findViewById(R.id.wifi_list));
            WifiInfoAdapter wifiInfoAdapter = (WifiInfoAdapter) wifi_list.get().getAdapter();
            if (wifiInfoAdapter != null)
                wifiInfoAdapter.clear();
        });

        // Add a click event processing function to the list to switch to the Wi Fi details interface
        wifi_list.get().setOnItemClickListener((parent, view, position, id) -> startActivity(new Intent(MainActivity.this, InfoActivity.class).putExtra("WiFi_Info",  (ScanResult)(wifi_list.get().getAdapter().getItem(position)))));
    }
}