package com.yana.yanagen4;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;


public class WifiComm {

    private Socket socket;
    Handler handler;
    private boolean minusOne =false;
    public static int responseCase;
    private int counter;
    private Timer timer;
    ArrayList<Integer> arr_byte = new ArrayList<Integer>();


    static  ArrayList<Byte> readMessage2 = new ArrayList<>();

    public WifiComm(Socket socket1)
    {
        socket = socket1;

        Receiving recievingThread = new Receiving(socket);
        recievingThread.start();
    }


    public  void AssignHandler(Handler mhandler)
    {
        handler = mhandler;
    }
    static  byte[] tempBuffer = new byte[2048];

    public class Receiving extends Thread
    {
        private Receiving(Socket socket1) {
            socket =socket1;
        }

        @Override
        public void run()
        {


            boolean recieveData =false;
            ArrayList<Byte>  readMessage1 =  new ArrayList<>();
            StringBuilder readMessage2 = new StringBuilder();
            ArrayList<Byte> readMessage3 = new ArrayList<>();
            minusOne =false;
            tempBuffer = new byte[2048];
            while (true)
            {

                try {
                    int size=0;
                    size   = socket.getInputStream().read(tempBuffer);
                    //  Log.e("E",new String(tempBuffer));
                  //  Log.e("Socket res:",new String(tempBuffer));

                    switch (responseCase)
                    {
                        case 1:/*Checks response until  "\n" comes*/
                            //readMessage1 = new StringBuilder();
                            String read = new String(tempBuffer,0,size);
                            for (int i = 0; i < size; i++) {
                                if (tempBuffer[i] == ':') {
                                    recieveData = true;
                                    readMessage1.add(tempBuffer[i]);
                                } else if (tempBuffer[i] == '\n') {
                                    readMessage1.add(tempBuffer[i]);
                                    byte[] tempByte = new byte[readMessage1.size()];
                                    for (int j = 0; j < readMessage1.size(); j++) {
                                        tempByte[j] = readMessage1.get(j);
                                    }
                                    Log.e("BC1",readMessage1.toString());
                                    readMessage1 = new ArrayList<>();

                                    handler.obtainMessage(11,  tempByte).sendToTarget();
                                    // CollectLogData(" RX :"+new String(tempByte));
                                    recieveData = false;
                                    tempBuffer = new byte[2048];
                                } else {
                                    if (recieveData) {
                                        readMessage1.add(tempBuffer[i]);
                                    }
                                }


                            }

                            break;
                        case 2:/*Checks response until  "#" comes*/
                            String read2 = new String(tempBuffer);
                            Log.e("BC2",read2);
                            for(int i=0; i<tempBuffer.length; i++)
                            {
                                switch (read2.charAt(i))
                                {
                                    case '\n':
                                        readMessage2.append(read2.charAt(i));
                                        tempBuffer = new byte[2048];
                                        byte[] temp2 =readMessage2.toString().getBytes();

                                        handler.obtainMessage(11, temp2).sendToTarget();
                                        //Log.e("BC n :",readMessage2.toString());

                                        readMessage2 = new StringBuilder();
                                        break;



                                    default:
                                        readMessage2.append(read2.charAt(i));
                                        break;
                                }
                            }
                            break;

                        case 3:
                            try
                            {
                                Log.e("BC Res :",new String(tempBuffer));
                                for(int i=0 ; i<size ; i++)
                                {
                                    if(size>=2 && tempBuffer[i] == 4 && tempBuffer[i-1]!=5)
                                    {
                                        readMessage3.add(tempBuffer[i]);
                                        byte[] tempByte = new byte[readMessage3.size()];
                                        for(int j=0;j<readMessage3.size();j++)
                                        {
                                            tempByte[j] = readMessage3.get(j);
                                        }
                                        handler.obtainMessage(11, tempByte).sendToTarget();
                                        Log.e("BC ResBytes STN:",new String(tempBuffer));
                                        readMessage3.clear();
                                        readMessage3 = new ArrayList<>();
                                        tempBuffer = new byte[8094];
                                    }
                                    else {
                                        readMessage3.add(tempBuffer[i]);
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                // CollectLogData(" excp @ STN hanlder :"+e.getMessage());

                                e.printStackTrace();
                            }
                            break;
                    }
                }
                catch (IOException e)
                {
                    minusOne=true;
                    handler.obtainMessage(22, "Socket Connection Lost!").sendToTarget();
                    e.printStackTrace();
                    break;
                }
            }

        }
    }


    public static void clearBuffer()
    {
        readMessage2 = new ArrayList<>();
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
                    Log.e("BlueComm SENT ",new String(msg));
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
        /*Something made socket disconnected*/
        return minusOne;
    }

}
