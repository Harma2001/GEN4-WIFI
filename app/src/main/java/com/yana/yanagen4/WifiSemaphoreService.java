package com.yana.yanagen4;


import com.yana.yanagen4.Libs.DataConversion;
import com.yana.yanagen4.Libs.Tbus;
import com.yana.yanagen4.Utils.Singleton;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class WifiSemaphoreService {
    private WifiComm wifiComm;
    byte[] btResponse;
    String btResponseString;

    Semaphore btSemaphore;


    public byte[] SendTbusCommand(byte sid,byte did, byte[] command, short length,boolean doConversion)
    {
        btSemaphore = new Semaphore(0);
        wifiComm = Singleton.getWifiComm();

        /*Forming the Tbus Command*/
        byte[] tbusFrame = Tbus.formCommand(sid,did,command,length);

        wifiComm.Send(tbusFrame);

        /*Block on semaphore till response comes*/
        try {
            btSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*parsing the Tbus response*/
        btResponse=Tbus.parseResponse(btResponse);
        if(doConversion && btResponse!=null)
        {
            /*Converting response from pan To ByteArray*/
            btResponse = DataConversion._PanToByteArray(btResponse,btResponse.length/2);
        }
        return btResponse;
    }
    public byte[] SendTbusCommandwithTimeout(byte sid, byte did, byte[] command, short length,boolean doParsing ,boolean doConversion, int timeout)
    {
        btSemaphore = new Semaphore(0);
        wifiComm = Singleton.getWifiComm();

        /*Forming the Tbus Command*/
        byte[] tbusFrame = Tbus.formCommand(sid,did,command,length);

        wifiComm.Send(tbusFrame);
        /*Block on semaphore till response comes*/
        try {
            if(! btSemaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS))
            {
                btSemaphore.release(0);
            }

            /*parsing the Tbus response*/
            if(btResponse!=null)
            {
                if(doParsing)
                {
                    btResponse=Tbus.parseResponse(btResponse);

                }

                if(doConversion && btResponse!=null)
                {
                    /*Converting response from pan To ByteArray*/
                    btResponse = DataConversion._PanToByteArray(btResponse,btResponse.length/2);
                }

                return btResponse;
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public byte[] SendCommandwithTimeout(byte [] command,boolean parseRepsonse, boolean doConversion, int timeout)
    {
        btSemaphore = new Semaphore(0);
        wifiComm = Singleton.getWifiComm();


        wifiComm.Send(command);

        /*Block on semaphore till response comes*/
        try {
            if(! btSemaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS))
            {
                btSemaphore.release(0);
            }
            if(btResponse!=null)
            {
                if(parseRepsonse)
                {
                    /*parsing the Tbus response*/
                    btResponse= Tbus.parseResponse(btResponse);
                }
                if(doConversion && btResponse!=null)
                {
                    /*Converting response from pan To ByteArray*/
                    btResponse = DataConversion._PanToByteArray(btResponse,btResponse.length/2);
                }



                return btResponse;

            }


        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return  null;
        }

        return null;
    }


    public String SendCommandwithTimeoutString (byte [] command, boolean parseRepsonse, boolean doConversion, int timeout)
    {
        btSemaphore = new Semaphore(0);
        wifiComm = Singleton.getWifiComm();


        wifiComm.Send(command);

        /*Block on semaphore till response comes*/
        try {
            if(! btSemaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS))
            {
                btSemaphore.release(0);
            }
            if(btResponseString!=null)
            {
                if(parseRepsonse)
                {
                    /*parsing the Tbus response*/
                    btResponseString=  new String(Tbus.parseResponse(btResponseString.getBytes()));
                }
                if(doConversion )
                {
                    /*Converting response from pan To ByteArray*/
                    btResponseString = new String( DataConversion._PanToByteArray(btResponseString.getBytes(),btResponseString.getBytes().length/2));
                }

                return btResponseString;

            }


        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return  null;
        }

        return null;
    }

    public  byte[] SendCommand(byte[] command,boolean doConversion )
    {
        btSemaphore = new Semaphore(0);
        wifiComm = Singleton.getWifiComm();


        wifiComm.Send(command);
        /*Block on semaphore till response comes*/
        try {
            btSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*parsing the Tbus response*/
        btResponse=Tbus.parseResponse(btResponse);

        if(doConversion && btResponse!=null)
        {
            /*Converting response from pan To ByteArray*/
            btResponse = DataConversion._PanToByteArray(btResponse,btResponse.length/2);
        }

        return btResponse;
    }

    public void getResponse(byte[] response)
    {

        if(btSemaphore!=null)
        {
            btResponse= response;  /*Copying response*/

            btSemaphore.release();  /*releasing  semaphore */
        }
    }
    public  void getResponseInString(String responseString)
    {
        if(btSemaphore!=null)
        {
            btResponseString= responseString;  /*Copying response*/

            btSemaphore.release();  /*releasing  semaphore */
        }
    }

    public void hardRelease()
    {
        btSemaphore.release();
    }

}
