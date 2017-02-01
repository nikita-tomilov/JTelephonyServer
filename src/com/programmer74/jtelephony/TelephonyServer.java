package com.programmer74.jtelephony;

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
                    ClientInfo cli = clients.get(clientSocket.getInetAddress());

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
                    outputs.writeUTF("what?");
                } catch (Exception ex) {
                    System.out.println(">>Client on " + ip + " disconnected.");
                    System.out.println(ex.toString());
                    isConnected = false;
                    clients.remove(clientSocket.getInetAddress());
                }

            }



        }
    }


    private ServerSocket welcomeSocket;
    private Socket clientSocket;
    private DatagramSocket heartbeatSocket;

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    HashMap <InetAddress, ClientInfo> clients = new HashMap<>();

    public void start(int port) {

        System.out.println("JTelephonyServer launched at " + getCurrentDate());

        //Trying to bind port for TCP&UDP servers
        try {
            welcomeSocket = new ServerSocket(port);
            heartbeatSocket = new DatagramSocket(port + 1);
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
                        heartbeatSocket.receive(packet);
                        ClientInfo cli = clients.get(packet.getAddress());
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
                clients.put(clientSocket.getInetAddress(), cli);
                System.out.println("added to hashmap");

            } catch (IOException ex) {
                System.out.println(">>Client tried to connect, but failed somehow.");
            }

        }
    }
}
