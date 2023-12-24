package com.yana.yanagen4;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.yana.yanagen4.Menu.MenuActivity;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class InfoActivity extends AppCompatActivity {

    TextView wifiLevel;
    Context context;
    TextView realTime;
    TextView updateTime;
    WifiManager wifiManager;

    ConnectionThread connectionThread;
    TCPConversation tcpConversation;
    DhcpInfo d;
    public static String selectedIP ="";
    public static String selected_port = "2022";
    Button button_back, connect;
    private WifiManager wifiMgr;
    private boolean isUpdated = false;
    long bootTime;


    @SuppressLint("SetTextI18n")
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                if (ActivityCompat.checkSelfPermission(InfoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(InfoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                List<ScanResult> results = wifiMgr.getScanResults();
                isUpdated = false;
                //Update Wi-Fi Strength
                boolean flag = false;
                ScanResult scanResult = InfoActivity.this.getIntent().getParcelableExtra("WiFi_Info");
                String target_ssid = scanResult.SSID;
                String target_bssid = scanResult.BSSID;
                for (ScanResult result : results) {
                    if (Objects.equals(result.SSID, target_ssid) && Objects.equals(result.BSSID, target_bssid)) {
                        wifiLevel.setText("level: " + result.level);
                        flag = true;
                        break;
                    }
                }
                if (!flag)
                    wifiLevel.setText("The Wi-Fi is out of signal range");
                updateTime.setText("Last update: " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date())));
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        bootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();
        Intent intent = getIntent();
        wifiLevel = findViewById(R.id.wifi_ssid);
        ScanResult scanResult = intent.getParcelableExtra("WiFi_Info");
        String target_ssid = scanResult.SSID;
        wifiLevel.setText(target_ssid);
        wifiLevel = findViewById(R.id.wifi_level);
        wifiLevel.setText("level: " + scanResult.level);
        realTime = findViewById(R.id.real_time);
        updateTime = findViewById(R.id.update_time);
        updateTime.setText("Last update: " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(scanResult.timestamp / 1000 + bootTime)));

        // Add an event handler for the "Back" button
        button_back = (Button) findViewById(R.id.button_back);
        button_back.setOnClickListener(v -> InfoActivity.this.finish());

        // wifi_status(target_ssid);

        connect = (Button) findViewById(R.id.button_connect);

        connect.setOnClickListener(v -> {

            wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            d=wifiManager.getDhcpInfo();
            selectedIP =  Formatter.formatIpAddress(d.gateway);

            connectionThread = new ConnectionThread(selectedIP,selected_port);
            connectionThread.start();


        });

        // Register broadcast receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiver, intentFilter);

        wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //Check if the Wi-Fi scan list is updated
        TimerTask checkUpdate = new TimerTask() {
            @Override
            public void run() {
                if (!isUpdated)
                    isUpdated = true;
            }
        };

        //time update
        Handler handler = new Handler(msg -> {
            if (msg.what == 1) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                realTime.setText(dateFormat.format(new Date()));
            }
            return false;
        });
        TimerTask refresh = new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };

        Timer timer = new Timer();
        timer.schedule(refresh, 0, 1000);
        timer.schedule(checkUpdate, 0, 2000);
    }


    public class ConnectionThread extends Thread
    {
        String strip;
        String portt;
        Socket socket1;
        public ConnectionThread(String strip1,String port_t)
        {
            strip = strip1;
            portt = port_t;
        }
        @Override
        public void run()
        {
            try
            {
                InetAddress inetAddress =InetAddress.getByName(strip);
                socket1 = new Socket(inetAddress, Integer.parseInt(portt));
                if(socket1.isConnected())
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InfoActivity.this, "Connection Successful", Toast.LENGTH_SHORT).show();
                            Toast.makeText(InfoActivity.this, strip, Toast.LENGTH_LONG).show();
                        }
                    });
                    tcpConversation = new TCPConversation(socket1);
                    TCPConversation.responseCase =1;
                    tcpConversation.AssignHandler(mhandler);
                    Singletone.setTcpConversation(tcpConversation);

                    SystemClock.sleep(200);


                    Intent  next=new Intent(InfoActivity.this, MenuActivity.class);
                    startActivity(next);
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InfoActivity.this, "Unable to Connect!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
            catch (IOException e)
            {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InfoActivity.this, "Host not Found!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            super.run();
        }
    }

    Handler mhandler = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what)
            {
                case 22:
                    String responseString=(String) message.obj;
                    Toast.makeText(context, responseString, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 33:
                    String responseString2=(String) message.obj;
                    if(tcpConversation!=null && tcpConversation.CheckConnection())
                    {
                        tcpConversation.TerminateConnection();
                    }
                    Toast.makeText(context, responseString2, Toast.LENGTH_SHORT).show();
                    finish();
                    break;



            }
            return true;
        }
    });


    private boolean isWifiConnectedToSSID(String ssid) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String connectedSSID = wifiInfo.getSSID().replaceAll("\"", "");

        if (connectedSSID.equals(ssid)) {
            return true;
        } else {
            return false;
        }
    }

}
