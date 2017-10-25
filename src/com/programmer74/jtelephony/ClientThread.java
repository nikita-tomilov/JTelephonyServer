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

                    if ((callingToClient != null) && (callingToClient.interlocutor != null)) {
                        return "busy";
                    }

                    boolean call_ok = true;
                    if (callingToClient != null) {
                        //ClientInfo callingToClient = entry.getValue();
                        if (callingToClient.nickname.equals(param)) {
                            System.out.println("[INFO] " + thisClient.nickname + " tries to call " + callingToClient.nickname);
                            thisClient.interlocutor= callingToClient;
                            callingToClient.interlocutor = thisClient;
                            thisClient.callStatus = "calling_to_wait";
                            callingToClient.callStatus = "being_called_by_wait";
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
                if (thisClient.callStatus.equals("call_in_progress"))
                    status += "talking_to " + thisClient.interlocutor.nickname + ";";
                if (thisClient.callStatus.equals("being_called_by_wait"))
                    status += "called_by " + thisClient.interlocutor.nickname + ";";
                if (thisClient.callStatus.equals("calling_to_wait")) {
                    if (thisClient.interlocutor.callStatus.equals("being_called_by_wait")) {
                        status += "calling_to wait;";
                    } else if (thisClient.interlocutor.callStatus.equals("call_accept")) {
                        status += "calling_to ok;";
                    }
                }
                if (thisClient.callStatus.equals("call_hang")) {
                    status += "calling_to hanged;";
                }
                if (thisClient.callStatus.equals("call_finish")) {
                    status += "calling_to finished;";
                }

                if (status.equals("")) status = "nothing dummy;";
                return (status);
            case "call_accept":
                if (thisClient.interlocutor == null) break;

                thisClient.interlocutor.callStatus = "call_accept";

                thisClient.interlocutor.callStatus = "call_in_progress";
                thisClient.interlocutor.interlocutor = thisClient;
                thisClient.callStatus = "call_in_progress";

                return ("acc dummy;");
            case "call_decline":
                if (thisClient.interlocutor == null) break;
                //if (!thisClient.interlocutor.callStatus.equals("calling_to_wait")) break;

                thisClient.interlocutor.callStatus = "call_hang";
                thisClient.interlocutor.interlocutor = null;
                thisClient.interlocutor = null;
                return ("dec dummy;");
            case "call_hangup":
                thisClient.callStatus = "nothing";
                if (thisClient.interlocutor != null) {
                    if ((thisClient.interlocutor.interlocutor != null) && (thisClient.interlocutor.interlocutor.ID == thisClient.ID)) {
                        thisClient.interlocutor.callStatus = "call_finish";
                    }
                }
                thisClient.interlocutor = null;

                return ("hang dummy;");
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

            } catch (java.io.IOException ioex) {
                if (thisClient != null) {
                    System.out.println("[INFO] Client on " + ip + " disconnected. " + ioex.getMessage());

                    if (thisClient.interlocutor != null) {
                        if ((thisClient.interlocutor.interlocutor != null) && (thisClient.interlocutor.interlocutor.ID == thisClient.ID)) {
                            if (thisClient.callStatus.equals("call_in_progress")) {
                                thisClient.interlocutor.callStatus = "call_hang";
                                System.out.println("  [INFO] Client " + thisClient.interlocutor.nickname + " notified.");
                            }
                        }
                    }

                    //System.out.println(ex.toString() + ":" + ex.getMessage());
                    isConnected = false;
                    clients.remove(thisClient.ID);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }



    }
}