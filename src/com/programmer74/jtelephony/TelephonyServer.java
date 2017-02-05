package com.programmer74.jtelephony;

import sun.plugin2.main.server.HeartbeatThread;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


//Server class to accept connection from clients
public class TelephonyServer {

    //Client threads, in which we parse text commands got from clients
    class Client implements Runnable {
        private Socket clientSocket;
        private boolean isConnected = true;
        private String ip;

        public Client (Socket socket) {
            this.clientSocket = socket;
            ip = socket.getInetAddress().toString();
        }

        @Override
        public void run() {

            DataInputStream inputs;
            DataOutputStream outputs;

            try {
                inputs = new DataInputStream(clientSocket.getInputStream());
                outputs = new DataOutputStream(clientSocket.getOutputStream());
                isConnected = true;
            } catch (IOException ex) {
                System.out.println(">>IO stabilizing error on " + ip + " Probably client disconnected.");
                return;
            }

            while (isConnected) {
                //Communication based on text commands goes here
                try {
                    String s = inputs.readUTF();
                    ClientInfo thisClient = clients.get(clientSocket.getInetAddress().toString() );

                    //System.out.println(">" + s + "<");
                    if (s.equals("list")) {
                        s = "-----\n";
                        for (ClientInfo cl : clients.values()) {
                            s += cl.nickname + " on " + cl.ip.toString() + ":" + cl.realPort + "\n";

                        }
                        s += "-----";
                        outputs.writeUTF(s);
                        continue;
                    }
                    if (s.matches("setnick [^ ]+")) {
                        thisClient.nickname = s.split(" ")[1];
                        outputs.writeUTF("ok");
                        continue;
                    }
                    if (s.equals("y") && thisClient.isWaitingCall) {
                        thisClient.hasAcceptedCall = true;
                        thisClient.isWaitingCall = false;
                        outputs.writeUTF("call accepted");
                        continue;
                    }
                    if (s.equals("n") && thisClient.isWaitingCall) {
                        thisClient.hasAcceptedCall = false;
                        thisClient.isWaitingCall = false;
                        outputs.writeUTF("call not accepted");
                        continue;
                    }

                    if (s.matches("call [^ ]+")) {
                        String callToNick = s.split(" ")[1];
                        ClientInfo callTo = null;
                        for (ClientInfo cl : clients.values()) {
                           if (cl.nickname.equals(callToNick)) callTo = cl;
                        }
                        //no client with that nick
                        if (callTo == null) {
                            outputs.writeUTF("who's that?");
                            continue;
                        }
                        //ask for call
                        thisClient.isWaitingCall = true;
                        thisClient.hasAcceptedCall = false;

                        callTo.isWaitingCall = true;
                        callTo.hasAcceptedCall = false;
                        //actual asking
                        askForPhoneCall(thisClient, callTo);
                        //now waiting till callTo accepts it or not (or timeout?)
                        long start_time = System.nanoTime();
                        long end_time;
                        while (callTo.isWaitingCall) {
                            end_time = System.nanoTime();
                            double difference = (end_time - start_time) / 1e6;
                            if (difference > 30000) break;
                            if (callTo.isWaitingCall == false) break;
                            try {
                                Thread.sleep(100);
                            } catch (Exception ex) {};
                        }
                        if (callTo.hasAcceptedCall) {
                            outputs.writeUTF("call ok");
                            callTo.callingTo = thisClient;
                            thisClient.callingTo = callTo;
                            if (IS_PROXY) {
                                sendUDPString(thisClient, "callstart on_proxy");
                                sendUDPString(callTo, "callstart on_proxy");
                            } else {
                                sendUDPString(thisClient, "callstart " + callTo.ip.toString() + " " + callTo.realPort);
                                sendUDPString(callTo, "callstart " + thisClient.ip.toString() + " " + thisClient.realPort);
                            }

                        } else {
                            outputs.writeUTF("call fail");
                        }
                        thisClient.isWaitingCall = false;
                        thisClient.hasAcceptedCall = false;
                        callTo.isWaitingCall = false;
                        callTo.hasAcceptedCall = false;
                        continue;
                    }
                    outputs.writeUTF("what?");
                } catch (Exception ex) {
                    System.out.println(">>Client on " + ip + " disconnected.");
                    System.out.println(ex.toString());
                    isConnected = false;
                    clients.remove(clientSocket.getInetAddress().toString() );
                }

            }



        }
    }

    //Proxy thread, in which we overcome traffic
    class Proxy implements  Runnable {
        @Override
        public void run() {

            while (true) {
                try {

                    byte[] buf = new byte[4096];
                    byte[] resend;
                    DatagramPacket packet = new DatagramPacket(buf, 4096);
                    DatagramPacket resendp;

                    proxySocket.receive(packet);
                    ClientInfo thisClient = clients.get(packet.getAddress().toString());
                    ClientInfo sendTo = thisClient.callingTo;
                    if (sendTo == null) continue;
                    resend = new byte[packet.getLength()];
                    System.arraycopy(packet.getData(), packet.getOffset(), resend, 0, packet.getLength());
                    resendp = new DatagramPacket(resend, resend.length, sendTo.ip, sendTo.realPort);
                    proxySocket.send(resendp);
                    //System.out.println(">>Heartbeat from " + packet.getAddress().toString() + ":" + packet.getPort());
                    //System.out.println(">>> proxy " + packet.getAddress().toString() + ":" + packet.getPort() + " -> " + resendp.getAddress().toString() + ":" + resendp.getPort() + ", bytes: " + resend.length);
                } catch (SocketTimeoutException stex) {
                    // timeout cause noone is online
                }
                catch (Exception ex) {
                    System.out.println(">>Proxy error: " + ex.toString());
                };
            }
        }
    }

    //Hearbeat class for continuing our nat udp traffic routing
    class Heartbeat implements Runnable {
        @Override
        public void run() {

            byte[] buf = new byte[1];
            DatagramPacket packet = new DatagramPacket(buf, 1);
            while (true) {
                try {
                    udpSocket.receive(packet);
                    ClientInfo thisClient = clients.get(packet.getAddress().toString());
                    thisClient.realPort = packet.getPort();
                    //System.out.println(">>Heartbeat from " + packet.getAddress().toString() + ":" + packet.getPort());
                } catch (SocketTimeoutException stex) {
                    // timeout cause noone is online
                }
                catch (Exception ex) {
                    System.out.println(">>Heartbeat error: " + ex.toString());
                };
            }
        }
    }

    private ServerSocket welcomeSocket;
    private Socket clientSocket;
    private DatagramSocket udpSocket;
    private DatagramSocket proxySocket;

    public boolean IS_PROXY = true;

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    HashMap <String, ClientInfo> clients = new HashMap<>();

    public void start(int port) {

        System.out.println("JTelephonyServer launched at " + getCurrentDate());

        //Trying to bind port for TCP&UDP servers
        try {
            welcomeSocket = new ServerSocket(port);
            udpSocket = new DatagramSocket(port + 1);
            proxySocket = new DatagramSocket(port + 2);
            udpSocket.setSoTimeout(10000);
            System.out.println("TCP Server started at " + welcomeSocket.getLocalSocketAddress());
            System.out.println("UDP Heartbeat listening on " + (port+1));
            System.out.println("UDP Proxy listening on " + (port+2));
        } catch (IOException ex) {
            System.out.println("Unable to start server socket.");
            return;
        }
        //initialising udp heartbeat server
        Thread heartbeatThread = new Thread(new Heartbeat());
        heartbeatThread.start();
        //and proxy
        Thread proxyThread = new Thread(new Proxy());
        proxyThread.start();

        //waiting for the connection
        while (true) {
            //client connected
            try {
                clientSocket = welcomeSocket.accept();
                System.out.println(">>Client connected from " + clientSocket.getInetAddress().toString() + " at " + getCurrentDate());

                //launching client
                Thread clientThread = new Thread(new Client(clientSocket));
                clientThread.start();

                //adding him to hashmap
                ClientInfo thisClient = new ClientInfo(clientSocket.getInetAddress().toString(), clientSocket.getInetAddress(), -1);
                clients.put(clientSocket.getInetAddress().toString() , thisClient);
                System.out.println("added to hashmap");

            } catch (IOException ex) {
                System.out.println(">>Client tried to connect, but failed somehow.");
            }

        }
    }


    public void askForPhoneCall(ClientInfo from, ClientInfo to) {
        System.out.println("Call initialised");
        String s = "callfrom " + from.nickname;
        byte[] bufTo = s.getBytes();
        DatagramPacket packetTo = new DatagramPacket(bufTo, bufTo.length, to.ip, to.realPort);
        byte[] bufFrom = new byte[2];
        DatagramPacket packetFrom = new DatagramPacket(bufFrom, 2, from.ip, from.realPort);
        try {
            udpSocket.send(packetTo);
            System.out.println("Call request sent on " + packetTo.getAddress().toString() + ":" + packetTo.getPort());


        } catch (Exception ex) {
            System.out.println("Call error: " + ex.toString());
            return;
        }

    }

    public void sendUDPString(ClientInfo to, String str) {
        byte[] bufTo = str.getBytes();
        DatagramPacket packetTo = new DatagramPacket(bufTo, bufTo.length, to.ip, to.realPort);
        try {
            udpSocket.send(packetTo);
            System.out.println(str + " sent to " + packetTo.getAddress().toString() + ":" + packetTo.getPort());

        } catch (Exception ex) {
            System.out.println("Sending error: " + ex.toString());
            return;
        }
    }
}
