package com.yana.yanagen4.Menu;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import com.yana.yanagen4.Libs.Tbus;
import com.yana.yanagen4.MainActivity;
import com.yana.yanagen4.R;
import com.yana.yanagen4.Singletone;
import com.yana.yanagen4.TCPConversation;
import com.yana.yanagen4.Terminal.wifi_terminal;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MenuActivity extends AppCompatActivity {

    CardView wifi_diag;

    TCPConversation wificomm;

    byte[] response;
    boolean aBoolean=false;

    private final String TAG = "MenuActivity.class";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        wifi_diag = findViewById(R.id.wifi_term);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            capabilities = connManager.getNetworkCapabilities(connManager.getActiveNetwork());
        }
        if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
         //   Toast.makeText(this,"Connected",Toast.LENGTH_SHORT).show();
        } else {
            Intent bck = new Intent(this, MainActivity.class);
            startActivity(bck);
        }

        wificomm = Singletone.getTcpConversation();
        wificomm.AssignHandler(response_Handler);

        wifi_diag.setOnClickListener(v -> {
            byte[] res = Tbus.formCommand((byte) 0x46, (byte) 0x01, null, (short)0);
            wificomm.Send(res);
            Intent intent = new Intent(MenuActivity.this, wifi_terminal.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        wificomm = Singletone.getTcpConversation();

        wificomm.AssignHandler(response_Handler);

        if(wificomm!=null && wificomm.CheckConnection())
        {
            Intent bck = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(bck);

        }
        if(wificomm==null)
        {
            Intent bck = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(bck);
        }

    }

    Handler response_Handler = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what)
            {
                case 11:
                    response=(byte[]) message.obj;
                    aBoolean=true;
                    break;

                case 33:
                    String responseString=(String) message.obj;
                    if(wificomm!=null && wificomm.CheckConnection())
                    {
                        wificomm.TerminateConnection();
                        wifi_error();
                    }
                    Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    });

    public void wifi_error() {
        new SweetAlertDialog(MenuActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("No Connection !")
                .setContentText("Check your wifi connection !")
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        Intent bck = new Intent(MenuActivity.this,MainActivity.class);
                        startActivity(bck);
                    }
                }).show();
    }

}