package com.programmer74.jtelephony;


import com.programmer74.jtdb.Credential;
import com.programmer74.jtdb.Profile;

import java.net.InetAddress;

//Client information
class OnlineClientInfo {
    public InetAddress ip;
    public int realPort;
    public int tcpPort;
    public String nickname;
    public Credential credential;
    public Profile profile;
    public boolean isWaitingCall = false;
    public boolean hasAcceptedCall = false;

    public OnlineClientInfo interlocutor = null;
    public String callStatus = "";

    public String password = "123";

    public int ID;

    public boolean isLoggedIn;

    public OnlineClientInfo (String nickname, InetAddress ip, int port)
    {
        this.nickname = nickname;
        this.ip = ip;
        this.realPort = port;
        isLoggedIn = false;
    }
}
