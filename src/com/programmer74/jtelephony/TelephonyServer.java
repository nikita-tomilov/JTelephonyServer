package com.programmer74.jtelephony;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


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
            //Communication goes here
            try {
                int x = inputs.readInt();
                x++;
                outputs.writeInt(x);
            } catch (Exception ex) {
                System.out.println(">>Client on " + ip + " disconnected.");
                isConnected = false;
            }

        }



    }
}

//Server class to accept connection from clients
public class TelephonyServer {

    private ServerSocket welcomeSocket;
    private Socket clientSocket;

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void start(int port) {

        System.out.println("JTelephonyServer launched at " + getCurrentDate());

        //Trying to bind port for TCP server
        try {
            welcomeSocket = new ServerSocket(port);
            System.out.println("Server started at " + welcomeSocket.getLocalSocketAddress());
        } catch (IOException ex) {
            System.out.println("Unable to start server socket.");
            return;
        }

        //waiting for the connection
        while (true) {
            //client connected
            try {
                clientSocket = welcomeSocket.accept();
                System.out.println(">>Client connected from " + clientSocket.getInetAddress().toString() + " at " + getCurrentDate());
                Thread clientThread = new Thread(new Client(clientSocket));
                //launching client
                clientThread.start();
            } catch (IOException ex) {
                System.out.println(">>Client tried to connect, but failed somehow.");
            }

        }
    }
}
