package com.programmer74.jtelephony;

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


//Client information
class ClientInfo {
    public InetAddress ip;
    public int realPort;
    public String nickname;
    public boolean isWaitingCall = false;
    public boolean hasAcceptedCall = false;

    public ClientInfo (String nickname, InetAddress ip, int port)
    {
        this.nickname = nickname;
        this.ip = ip;
        this.realPort = port;
    }
}

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
                    ClientInfo cli = clients.get(clientSocket.getInetAddress().toString() );

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
                        cli.nickname = s.split(" ")[1];
                        outputs.writeUTF("ok");
                        continue;
                    }
                    if (s.equals("y") && cli.isWaitingCall) {
                        cli.hasAcceptedCall = true;
                        cli.isWaitingCall = false;
                        outputs.writeUTF("call accepted");
                        continue;
                    }
                    if (s.equals("n") && cli.isWaitingCall) {
                        cli.hasAcceptedCall = false;
                        cli.isWaitingCall = false;
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
                        cli.isWaitingCall = true;
                        cli.hasAcceptedCall = false;
                        //cli.callHash = (cli.ip.toString() + callTo.ip.toString()).hashCode();
                        callTo.isWaitingCall = true;
                        callTo.hasAcceptedCall = false;
                        //actual asking
                        askForPhoneCall(cli, callTo);
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
                            sendUDPString(cli, "callstart " + callTo.ip.toString() + " " + callTo.realPort);
                            sendUDPString(callTo, "callstart " + cli.ip.toString() + " " + cli.realPort);

                        } else {
                            outputs.writeUTF("call fail");
                        }
                        cli.isWaitingCall = false;
                        cli.hasAcceptedCall = false;
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


    private ServerSocket welcomeSocket;
    private Socket clientSocket;
    private DatagramSocket udpSocket;

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
            udpSocket.setSoTimeout(10000);
            System.out.println("TCP Server started at " + welcomeSocket.getLocalSocketAddress());
            System.out.println("UDP Heartbeat listening on " + (port+1));
        } catch (IOException ex) {
            System.out.println("Unable to start server socket.");
            return;
        }
        //initialising udp heartbeat server
        Thread heartbeatThread = new Thread(new Runnable() {
            @Override
            public void run() {

                byte[] buf = new byte[1];
                DatagramPacket packet = new DatagramPacket(buf, 1);
                while (true) {
                    try {
                        udpSocket.receive(packet);
                        ClientInfo cli = clients.get(packet.getAddress().toString());
                        cli.realPort = packet.getPort();
                        //System.out.println(">>Heartbeat from " + packet.getAddress().toString() + ":" + packet.getPort());
                    } catch (Exception ex) {
                        System.out.println(">>Heartbeat error: " + ex.toString());
                    };
                }
            }
        });
        heartbeatThread.start();

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
                ClientInfo cli = new ClientInfo(clientSocket.getInetAddress().toString(), clientSocket.getInetAddress(), -1);
                clients.put(clientSocket.getInetAddress().toString() , cli);
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
