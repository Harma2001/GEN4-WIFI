package com.yana.yanagen4.Utils;


import com.yana.yanagen4.WifiComm;

public class Singleton {
    private static final Singleton ourInstance = new Singleton();
    static Singleton getInstance() {
        return ourInstance;
    }
    /*creating instances*/


    /*For Wifi comm*/
    public  static WifiComm wifiComm;
    public static   WifiComm getWifiComm()
    {
        return wifiComm;
    }
    public  static void  setWifiComm(WifiComm tcpConversation)
    {
        Singleton.wifiComm=tcpConversation;
    }

}
