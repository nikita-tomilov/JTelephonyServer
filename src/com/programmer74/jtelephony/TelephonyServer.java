package com.programmer74.jtelephony;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


//Server class to accept connection from clients
public class TelephonyServer {


    //Hearbeat class for continuing our nat udp traffic routing
    class Heartbeat implements Runnable {
        @Override
        public void run() {

            byte[] buf = new byte[1];
            DatagramPacket packet = new DatagramPacket(buf, 1);
            while (true) {
                try {
                    udpSocket.receive(packet);
                    ClientInfo thisClient = clients.get(0);
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

    private Integer userCounter = 0;

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    //HashMap <String, ClientInfo> clients = new HashMap<>();
    //List<ClientInfo> clients = Collections.synchronizedList(new ArrayList<ClientInfo>());
    Map<Integer, ClientInfo> clients = new ConcurrentHashMap<>(); // Collections.synchronizedMap(new HashMap<Integer, ClientInfo>());

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
        Thread proxyThread = new Thread(new ProxyThread(clients, proxySocket));
        proxyThread.start();

        //waiting for the connection
        while (true) {
            //client connected
            try {
                clientSocket = welcomeSocket.accept();
                synchronized (clients) {
                    System.out.println("[INFO] Client connected from " + clientSocket.getInetAddress().toString() + " at " + getCurrentDate());

                    //adding him to hashmap
                    ClientInfo thisClient = new ClientInfo(clientSocket.getInetAddress().toString(), clientSocket.getInetAddress(), -1);
                    //clients.put(clientSocket.getInetAddress().toString()/* + ":" + clientSocket.getPort()*/ , thisClient);
                    thisClient.tcpPort = clientSocket.getPort();
                    clients.put(userCounter, thisClient);
                    thisClient.ID = userCounter;
                    userCounter++;
                    System.out.println("[INFO] Client added to hashmap");

                    //launching client
                    Thread clientThread = new Thread(new ClientThread(clientSocket, clients));
                    clientThread.start();

                }

            } catch (IOException ex) {
                System.err.println("[ERROR] Client tried to connect, but failed somehow.");
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
