package com.programmer74.jtelephony;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.HashMap;

//Proxy thread, in which we overcome traffic
public class ProxyThread implements  Runnable {

    Map<Integer, ClientInfo> clients = null;
    private DatagramSocket proxySocket;

    public ProxyThread(Map<Integer, ClientInfo> clients, DatagramSocket proxySocket) {
        this.clients = clients;
        this.proxySocket = proxySocket;
    }

    @Override
    public void run() {

        while (true) {
            try {

                byte[] buf = new byte[4096];
                byte[] resend;
                DatagramPacket packet = new DatagramPacket(buf, 4096);
                DatagramPacket resendp;

                proxySocket.receive(packet);
                ClientInfo thisClient = null;
                synchronized (clients) {
                    thisClient = clients.get((int)(buf[0]));


                    if (thisClient == null) continue;

                    if (thisClient.realPort != packet.getPort())
                    {
                        thisClient.realPort = packet.getPort();
                        System.out.println("[INFO] " + thisClient.nickname + "'s port has changed to " + thisClient.realPort);
                    }


                    if (!thisClient.callingToStatus.equals("ok")) continue;
                    ClientInfo sendTo = thisClient.talkingTo;
                    if (sendTo == null) {
                        thisClient.callingToStatus = "hanged";
                        continue;
                    }
                    resend = new byte[packet.getLength()];
                    System.arraycopy(packet.getData(), packet.getOffset(), resend, 0, packet.getLength());
                    resendp = new DatagramPacket(resend, resend.length, sendTo.ip, sendTo.realPort);
                    proxySocket.send(resendp);
                    //System.out.println(">>Heartbeat from " + packet.getAddress().toString() + ":" + packet.getPort());
                    System.err.println(">>> " + thisClient.nickname + "@" + packet.getAddress().toString() + ":" + packet.getPort() + " -> " + sendTo.nickname + "@" + resendp.getAddress().toString() + ":" + resendp.getPort() + ", bytes: " + resend.length);
                }
            } catch (SocketTimeoutException stex) {
                // timeout cause noone is online
            }
            catch (Exception ex) {
                System.err.println("[ERROR] Proxy error: " + ex.toString());
                //ex.printStackTrace();
            };
        }
    }
}
