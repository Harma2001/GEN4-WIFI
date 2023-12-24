package com.yana.yanagen4;



public class Singletone {
    private static final Singletone ourInstance = new Singletone();
    static Singletone getInstance() {
        return ourInstance;
    }


    /*For TCP COnversation*/
    public  static TCPConversation tcpConversation;
    public static   TCPConversation getTcpConversation()
    {
        return tcpConversation;
    }
    public  static void  setTcpConversation(TCPConversation tcpConversation)
    {
        Singletone.tcpConversation=tcpConversation;
    }
}
