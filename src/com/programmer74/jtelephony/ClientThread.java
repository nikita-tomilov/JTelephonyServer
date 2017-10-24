package com.programmer74.jtelephony;


import com.sun.security.ntlm.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

//Client threads, in which we parse text commands got from clients
public class ClientThread implements Runnable {
    private Socket clientSocket;
    private boolean isConnected = true;
    private String ip;

    //HashMap<String, ClientInfo> clients = new HashMap<>();
    Map<Integer, ClientInfo> clients = null;

    public ClientThread (Socket socket,  Map<Integer, ClientInfo> clients) {
        this.clientSocket = socket;
        ip = socket.getInetAddress().toString();
        this.clients = clients;
    }

    private String parseCommandAndGetAnswer(ClientInfo thisClient, String cmd, String param) {
        switch (cmd) {
            case "nick":
                String nick = param.split(":")[0];
                String passhash_given = param.split(":")[1];
                String passhash_real = Utils.stringToMD5(thisClient.password);
                if (passhash_given.equals(passhash_real)) {
                    thisClient.nickname = nick;
                    return (String.valueOf(thisClient.ID));
                } else {
                    isConnected = false;
                    clients.remove(thisClient.ID);
                    return ("-1");
                }
            case "call":
                ClientInfo callingToClient = null;
                synchronized (clients) {
                    for (Map.Entry<Integer, ClientInfo> m : clients.entrySet()) {
                        ClientInfo cli = m.getValue();
                        if (cli.nickname.equals(param)) {
                            callingToClient = cli;
                            break;
                        }
                    }

                    boolean call_ok = true;
                    if (callingToClient != null) {
                        //ClientInfo callingToClient = entry.getValue();
                        if (callingToClient.nickname.equals(param)) {
                            System.out.println("[INFO] " + thisClient.nickname + " tries to call " + callingToClient.nickname);
                            thisClient.callingTo = callingToClient;
                            callingToClient.beingCalledBy = thisClient;
                            thisClient.callingToStatus = "wait";
                            return ("wait");
                            //System.out.println(callingToClient.beingCalledBy.toString());

                        } else call_ok = false;
                    } else call_ok = false;

                    if (!call_ok) {
                        return ("error");
                    }
                }
            case "status":
                String status = "";
                if (thisClient.callingToStatus == "ok")
                    status += "talking_to " + thisClient.talkingTo.nickname + ";";
                if (thisClient.beingCalledBy != null)
                    status += "called_by " + thisClient.beingCalledBy.nickname + ";";
                if (thisClient.callingTo != null) {
                    if (thisClient.callingToStatus == "wait") {
                        status += "calling_to wait;";
                    } else if (thisClient.callingToStatus == "ok") {
                        status += "calling_to ok;";
                    }
                }
                if (thisClient.callingToStatus == "hanged") {
                    status += "calling_to hanged;";
                }

                if (status == "") status = "nothing dummy;";
                return (status);
            case "call_accept":
                if (thisClient.beingCalledBy == null) break;

                thisClient.beingCalledBy.callingToStatus = "ok";
                thisClient.beingCalledBy.talkingTo = thisClient;
                thisClient.callingToStatus = "ok";
                thisClient.talkingTo = thisClient.beingCalledBy;
                thisClient.beingCalledBy = null;
                return ("acc dummy");
            case "call_decline":
                if (thisClient.beingCalledBy == null) break;
                if (thisClient.beingCalledBy.callingToStatus != "wait") break;

                thisClient.beingCalledBy.callingToStatus = "hanged";
                thisClient.beingCalledBy.callingTo = null;
                thisClient.beingCalledBy = null;
                return ("dec dummy");
            case "call_hangup":
                thisClient.callingToStatus = "nothing";
                if ((thisClient.talkingTo != null) && (thisClient.talkingTo.talkingTo.ID == thisClient.ID))
                    thisClient.talkingTo.callingToStatus = "nothing";

                return ("hang ok");
            case "ls":
                String all = "";
                synchronized (clients) {
                    for (Map.Entry<Integer, ClientInfo> m : clients.entrySet()) {
                        ClientInfo cli = m.getValue();
                        all += cli.nickname + ";";
                    }
                }
                //all += ";";
                return all;
            default:
                return ("wtf " + cmd);
        }
        return "wtf " + cmd;
    }

    @Override
    public void run() {

        DataInputStream inputs;
        DataOutputStream outputs;

        try {
            inputs = new DataInputStream(clientSocket.getInputStream());
            outputs = new DataOutputStream(clientSocket.getOutputStream());
            isConnected = true;
            System.out.println("[INFO] I/O OK with " + clientSocket.getInetAddress().toString());
        } catch (IOException ex) {
            System.out.println("[ERROR] I/O error on " + ip + " Probably client disconnected.");
            return;
        }

        ClientInfo thisClient = null;

        while (isConnected) {
            //Communication based on text commands goes here
            try {
                String s = inputs.readUTF();
                synchronized (clients) {
                    for (Map.Entry<Integer, ClientInfo> m : clients.entrySet()) {
                        ClientInfo cli = m.getValue();
                    /*}
                    Iterator i = clients.iterator(); // Must be in synchronized block
                    while (i.hasNext()) {
                        ClientInfo cli = (ClientInfo) i.next();*/
                        if ((cli.tcpPort == clientSocket.getPort()) && (cli.ip == clientSocket.getInetAddress())) {
                            thisClient = cli;
                            //System.out.println("[INFO] Found client");
                            break;
                        }
                    }
                }

                if (thisClient == null) continue;
                synchronized (thisClient) {
                    //clients.get(clientSocket.getInetAddress().toString() );

                    //System.out.println(thisClient.ip.toString() + ":" + clientSocket.getPort() + " said " + s);
                    String cmd = s.split(" ")[0];
                    String param = s.split(" ")[1];

                    String ans = parseCommandAndGetAnswer(thisClient, cmd, param);

                    outputs.writeUTF(ans);
                    System.out.println("[LOG] " + thisClient.nickname + " said " + cmd + "(" + param + "), reply: " + ans);

                }

            } catch (Exception ex) {
                if (thisClient != null) {
                    System.out.println("[INFO] Client on " + ip + " disconnected. " + ex.getMessage());
                    if (thisClient.talkingTo != null) {
                        thisClient.talkingTo.callingToStatus = "hanged";
                        System.out.println("  [INFO] Client " + thisClient.talkingTo.nickname + " notified.");
                    }
                    //System.out.println(ex.toString() + ":" + ex.getMessage());
                    isConnected = false;
                    clients.remove(thisClient.ID);
                }
            }

        }



    }
}