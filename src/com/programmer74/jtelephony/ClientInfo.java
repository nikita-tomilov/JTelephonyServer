package com.programmer74.jtelephony;


import java.net.InetAddress;

//Client information
class ClientInfo {
    public InetAddress ip;
    public int realPort;
    public String nickname;
    public boolean isWaitingCall = false;
    public boolean hasAcceptedCall = false;
    public ClientInfo callingTo = null;

    public ClientInfo (String nickname, InetAddress ip, int port)
    {
        this.nickname = nickname;
        this.ip = ip;
        this.realPort = port;
    }
}
