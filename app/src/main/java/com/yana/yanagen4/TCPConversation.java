package com.yana.yanagen4;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;


public class TCPConversation {

    private Socket socket;
    Handler handler;
    private boolean minusOne =false;
    private boolean recieveData = false;
    public static int size ;

    public static  volatile  int responseCase;

    public TCPConversation(Socket socket1) {
        socket = socket1;
        Thread recievingThread = new Thread(new Receiving(socket));
        Log.d("TCP constructor","TCP Constructor called");
        recievingThread.start();
    }
    public  void AssignHandler(Handler mhandler)
    {
        handler = mhandler;
    }

    public class Receiving implements Runnable
    {
        private Receiving(Socket socket1) {
            socket =socket1;
        }

        @Override
        public void run()
        {

            StringBuilder readMessage = new StringBuilder();
            ArrayList<Byte> readMessage2 = new ArrayList<>();
            ArrayList<Byte> readMessage3 = new ArrayList<>();

            byte[] tempBuffer ;

            tempBuffer = new byte[8094];

            while (true)
            {

                try {
                    size   = socket.getInputStream().read(tempBuffer);
                    Log.d("Size of temp buffer", String.valueOf(size));
                    switch (responseCase)
                    {
                        case 1:/*If response contains  "\n" , then send the response  (FOR Terminal) */
                            //  String read = new String(tempBuffer,0,size);

                            Log.d("Response Case 1: " , String.valueOf(tempBuffer));
                            String byteString = Arrays.toString(tempBuffer);
                            Log.d("special characters", byteString);

                            String read = new String(tempBuffer,0,size);
                            for(int i=0; i<size-1; i++) {
                                readMessage.append(read.charAt(i));

                                /*
                                if(read.charAt(i)=='\r' || read.charAt(i) =='\n')
                                {
                                    Log.d("Response Case 1a: " , String.valueOf(tempBuffer[i]));
                                    Log.d("Response Case 1b: " , String.valueOf(tempBuffer));
                                    readMessage.append(read.charAt(i));
                                    String tempReadMessage = readMessage.toString();
                                    handler.obtainMessage(11, size, -1, tempReadMessage.getBytes()).sendToTarget();
                                    Log.e("BC rs=1  ", tempReadMessage);

                                    CollectLogData(" RX :"+tempReadMessage );

                                    readMessage = new StringBuilder();
                                    tempBuffer = new byte[8094];
                                }
                                */
                            }
                            String tempReadMessage = readMessage.toString();
                            handler.obtainMessage(11, size, -1, tempReadMessage.getBytes()).sendToTarget();
                            readMessage = new StringBuilder();
                            tempBuffer = new byte[8094];

                            /*

                            readMessage2 = new ArrayList<>();
                            for(int i=0; i<size; i++) {

                                Log.e("TCPCONV RES1",String.valueOf(tempBuffer[i]));

                                    readMessage2.add(tempBuffer[i]);
                                    byte[] tempByte = new byte[readMessage2.size()];
                                    for(int j=0;j<readMessage2.size();j++)
                                    {
                                        tempByte[j] = readMessage2.get(j);
                                    }
                                    handler.obtainMessage(11, size, -1, tempByte).sendToTarget();
                                    Log.e("TCPCONV RES2 in Case 1",new String(tempByte));

                                    readMessage2=new ArrayList<>();
                                    tempBuffer = new byte[8094];

                            }

                             */


                            break;

                        case 2:/*Checks response until  "\n" comes (FOR FLASH BOOTLOADER)*/
                            //  String read2 = new String(tempBuffer,0,size);
                            for(int i=0 ; i<size ; i++)
                            {
                                Log.e("TCPCONV RES2",String.valueOf(tempBuffer[i]));
                                if(tempBuffer[i] == ':'){
                                    recieveData =true;
                                    readMessage2.add(tempBuffer[i]);
                                }
                                else if(tempBuffer[i] =='\n')
                                {
                                    readMessage2.add(tempBuffer[i]);
                                    byte[] tempByte = new byte[readMessage2.size()];
                                    for(int j=0;j<readMessage2.size();j++)
                                    {
                                        tempByte[j] = readMessage2.get(j);
                                    }
                                    handler.obtainMessage(11, size, -1, tempByte).sendToTarget();

                                    recieveData =false;
                                    Log.e("BC ResBytes :",new String(tempByte));
                                    readMessage2 = new ArrayList<>();
                                    tempBuffer = new byte[8094];
                                }
                                else
                                {
                                    if(recieveData)
                                    {
                                        readMessage2.add(tempBuffer[i]);
                                    }
                                }


                            }
                            break;
                        case 3: /*STN FLash*/

                            for(int i=0 ; i<size ; i++)
                            {
                                Log.e("BC Res :",new String(tempBuffer));
                                if(tempBuffer[i] == 4 && tempBuffer[i-1]!=5)
                                {
                                    readMessage3.add(tempBuffer[i]);
                                    byte[] tempByte = new byte[readMessage3.size()];
                                    for(int j=0;j<readMessage3.size();j++)
                                    {
                                        tempByte[j] = readMessage3.get(j);
                                    }
                                    handler.obtainMessage(11, tempByte).sendToTarget();
                                    Log.e("BC ResBytes :",new String(tempBuffer));
                                    readMessage3.clear();
                                    tempBuffer = new byte[8094];
                                }
                                else {
                                    readMessage3.add(tempBuffer[i]);
                                }
                            }
                            break;
                        case 4:/*Only  for ADC graphs*/
                            // String read2 = new String(tempBuffer,0,size);
                            Log.e("BC Res :",new String(tempBuffer));
                            handler.obtainMessage(11,size,-1,tempBuffer).sendToTarget();
                            // Log.e("BC Res :",new String(tempBuffer));
                            //tempBuffer = new byte[8094];
                            // tempBuffer =new byte[0];
                            //  Log.e("TCPCONVERSATION RECEIVE",new String(adcBuff));

                            break;


                        case 5:

                            Log.e("TCPCONV RES1",new String(tempBuffer));
                            readMessage2 = new ArrayList<>();
                            handler.obtainMessage(11, size, -1, tempBuffer).sendToTarget();
                            tempBuffer = new byte[8094];

                           /* for(int i=0; i<size; i++) {

                                if( tempBuffer[i] == '>' || tempBuffer[i]=='#'  || (tempBuffer[i] == '\n'))
                                {
                                    readMessage2.add(tempBuffer[i]);
                                    byte[] tempByte = new byte[readMessage2.size()];
                                    for(int j=0;j<readMessage2.size();j++)
                                    {
                                        tempByte[j] = readMessage2.get(j);
                                    }
                                    handler.obtainMessage(11, size, -1, tempByte).sendToTarget();
                                    Log.e("TCPCONV RES2",new String(tempByte));

                                    readMessage2=new ArrayList<>();
                                    tempBuffer = new byte[8094];
                                }
                                else
                                {
                                    readMessage2.add(tempBuffer[i]);
                                }
                            }*/
                            break;

                        case 6:
                            handler.obtainMessage(11, size, -1, tempBuffer).sendToTarget();

                            break;

                        default:
                            handler.obtainMessage(11, "no response from the above").sendToTarget();
                    }
                }
                catch (IOException e)
                {
                    minusOne=true;
                    handler.obtainMessage(33, "Wifi Socket Connection Lost!").sendToTarget();
                    e.printStackTrace();
                    break;
                }
            }
        }
    }


    public void Send(final byte[] bite)
    {
        final byte[] msg;
        msg=bite;
        Thread t =  new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    socket.getOutputStream().write(msg);
                    Log.e("CommandSent",new String(msg));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
    public void TerminateConnection()
    {
        if(socket.isConnected())
        {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean CheckConnection()
    {
        /*Disconnected*/
        return minusOne;
    }

}