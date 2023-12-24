package com.yana.yanagen4.Terminal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.yana.yanagen4.MainActivity;
import com.yana.yanagen4.R;
import com.yana.yanagen4.Singletone;
import com.yana.yanagen4.TCPConversation;
import com.yana.yanagen4.Utils.ChatAdapter;
import com.yana.yanagen4.Utils.ChatMessage;
import com.yana.yanagen4.WifiSemaphoreService;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class wifi_terminal extends AppCompatActivity {

    ListView logListView;
    private static ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    Button sendButton;
    EditText editText;

    TCPConversation wifiCommn;
    byte[] resbyts;
    String response;
    boolean aBoolean =false;

    private Context context;
    private final String TAG = "WIFI Terminal Activity";
    WifiSemaphoreService wifiSemaphoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_terminal);

        init();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void init(){

        context = wifi_terminal.this;
        wifiSemaphoreService = new WifiSemaphoreService();
        wifiCommn = Singletone.getTcpConversation();
        wifiCommn.AssignHandler(mhandler);
        TCPConversation.responseCase =1;

        logListView= findViewById(R.id.loglistView);
        logListView.setItemsCanFocus(true);
        logListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        loadDummyHistory();

        sendButton = findViewById(R.id.sendTextid);
        editText= findViewById(R.id.editTextid);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wifiCommn!=null && !wifiCommn.CheckConnection())
                {
                    String  message=editText.getText().toString();


                    if(!message.equals(""))
                    {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                TCPConversation.responseCase =1;

                                wifiCommn.Send(message.getBytes());

                                PrintConv( message,true);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        editText.setText("");
                                    }
                                });
                            }
                        });
                        thread.start();

                    }
                    else
                    {
                        Toast.makeText(context, "Enter some command!", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    PrintConv( "SOCKET CONN LOST",true);
                    Toast.makeText(wifi_terminal.this, "SOCKET CONN LOST", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    Handler mhandler = new Handler(new Handler.Callback()
    {

        @SuppressLint("LongLogTag")
        @Override
        public boolean handleMessage(Message message)
        {
            Log.d(TAG, "Handle Message:-) " + message);
            switch(message.what)
            {
                case 11:
                    resbyts = (byte[]) message.obj;
                    response= new String(resbyts);

                    Log.e("Response from terminal(byte)", String.valueOf(resbyts));
                    if(response.length()>0){
                        response = response.replace("\r\n","\n");
                        aBoolean =true;
                        PrintConv(response,false);
                        Log.e("Response from terminal",response);
                    }
                    else{
                        PrintConv("No Response",false);
                    }

                    break;

                case 22:
                    String responseString=(String) message.obj;
                    if(wifiCommn!=null && wifiCommn.CheckConnection())
                    {
                        wifiCommn.TerminateConnection();
                        PrintConv("CONNECTION LOST",false);
                        wifi_error();
                    }
                    Toast.makeText(context, responseString, Toast.LENGTH_SHORT).show();
                    finish();

                    break;
            }
            return true;
        }
    });

    public void displayMessage(ChatMessage message)
    {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }
    private void scroll() { logListView.setSelection(logListView.getCount() - 1); }
    private void loadDummyHistory()
    {
        chatHistory = new ArrayList<>();
        ChatMessage msg = new ChatMessage();
        msg.setId(1);
        msg.setMe(false);
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);

        adapter = new ChatAdapter(wifi_terminal.this, new ArrayList<ChatMessage>());
        logListView.setAdapter(adapter);

    }

    public void PrintConv(final String msg, final boolean val)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(11);//dummy
                chatMessage.setMessage(msg);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(val);
                displayMessage(chatMessage);
            }
        });
    }

    public void wifi_error() {
        new SweetAlertDialog(wifi_terminal.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("No Connection !")
                .setContentText("Check your wifi connection !")
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        Intent bck = new Intent(wifi_terminal.this, MainActivity.class);
                        startActivity(bck);
                    }
                }).show();
    }

}