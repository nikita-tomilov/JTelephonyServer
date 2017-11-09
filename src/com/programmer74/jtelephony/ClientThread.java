package com.programmer74.jtelephony;


import com.sun.security.ntlm.Client;
import com.programmer74.jtdb.*;

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

    private CredentialsDAO crdao;
    private ProfilesDAO prfdao;
    private MessagesDAO msgdao;
    private LoginHistoryDAO lghistdao;
    private CallDAO cdao;

    //HashMap<String, OnlineClientInfo> clients = new HashMap<>();
    Map<Integer, OnlineClientInfo> clients = null;

    public ClientThread (Socket socket,  Map<Integer, OnlineClientInfo> clients) {
        this.clientSocket = socket;
        ip = socket.getInetAddress().toString();
        this.clients = clients;

        this.crdao = new CredentialsDAO();
        this.prfdao = new ProfilesDAO();
        this.msgdao = new MessagesDAO();
        this.lghistdao = new LoginHistoryDAO();
        this.cdao = new CallDAO();
    }

    private String parseCommandAndGetAnswer(OnlineClientInfo thisClient, String cmd, String param) {

        if (!thisClient.isLoggedIn) {
            if (cmd.equals("nick")) {

                Credential crd;
                Profile prf;

                String nick = param.split(":")[0];
                String passhash_given = param.split(":")[1];

                try {
                    crd = crdao.getCredentialByUsername(nick);
                    if (crd == null) throw new Exception();

                    prf = prfdao.getProfileByCredentialID(crd.getId());
                    if (prf == null) throw new Exception();
                } catch (Exception ex) {
                    return "error";
                }

                String passhash_real = crd.getPasswordHash();
                LoginHistory lh = new LoginHistory(crd.getId(), new Date(), "fail");
                if (passhash_given.equals(passhash_real)) {
                    thisClient.nickname = crd.getUsername();
                    thisClient.credential = crd;
                    thisClient.profile = prf;
                    thisClient.isLoggedIn = true;
                    thisClient.profile.setStatus("online");
                    try {
                        lh.setState("ok");
                        lghistdao.addLoginHistory(lh);
                        prfdao.updateProfile(thisClient.profile);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return (String.valueOf(thisClient.ID));
                } else {
                    isConnected = false;
                    clients.remove(thisClient.ID);
                    try {
                        lghistdao.addLoginHistory(lh);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return ("-1");
                }
            } else {
                return "error";
            }
        }

        String paramNickname = param.split(":")[0];

        OnlineClientInfo paramClient = null;
        if((!cmd.equals("ls")) && (!cmd.equals("status"))) {
            synchronized (clients) {
                for (Map.Entry<Integer, OnlineClientInfo> m : clients.entrySet()) {
                    OnlineClientInfo cli = m.getValue();
                    if (cli.nickname.equals(paramNickname)) {
                        paramClient = cli;
                        break;
                    }
                }
            }
        }

        switch (cmd) {
            case "call":
                OnlineClientInfo callingToClient = null;
                synchronized (clients) {
                    callingToClient = paramClient;

                    if ((callingToClient != null) && (callingToClient.interlocutor != null)) {
                        return "busy";
                    }

                    boolean call_ok = true;
                    if (callingToClient != null) {
                        //OnlineClientInfo callingToClient = entry.getValue();
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
                if (thisClient.hasIncomingMessages)  {
                    status += "has incoming_messages;";
                }

                if (status.equals("")) status = "nothing dummy;";
                return (status);
            case "call_accept":
                if (thisClient.interlocutor == null) break;

                thisClient.interlocutor.callStatus = "call_accept";

                thisClient.interlocutor.callStatus = "call_in_progress";
                thisClient.interlocutor.interlocutor = thisClient;
                thisClient.callStatus = "call_in_progress";

                thisClient.call = new Call(thisClient.interlocutor.profile.getId(), thisClient.profile.getId(), new Date());
                try {
                    cdao.addCall(thisClient.call);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

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
                        if (thisClient.interlocutor.call != null) {
                            thisClient.interlocutor.call.setFinished(new Date());
                            try {
                                cdao.updateCall(thisClient.interlocutor.call);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            thisClient.interlocutor.call = null;
                        }

                    }
                }
                thisClient.interlocutor = null;
                if (thisClient.call != null) {
                    thisClient.call.setFinished(new Date());
                    try {
                        cdao.updateCall(thisClient.call);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    thisClient.call = null;
                }

                return ("hang dummy;");
            case "ls":
                String all = "";
                synchronized (clients) {
                    for (Map.Entry<Integer, OnlineClientInfo> m : clients.entrySet()) {
                        OnlineClientInfo cli = m.getValue();
                        all += cli.nickname + ";";
                    }
                }
                //all += ";";
                return all;
            case "info":
                OnlineClientInfo infoAboutClient = null;
                if (!param.equals("!!me")) {
                    infoAboutClient = paramClient;
                } else infoAboutClient = thisClient;
                if (infoAboutClient == null) return "no_such_user";
                String cliinfo = infoAboutClient.nickname + ":" +
                        infoAboutClient.profile.getFirstName() + ":" +
                        infoAboutClient.profile.getLastName() + ":" +
                        infoAboutClient.profile.getCity() + ":" +
                        infoAboutClient.profile.getStatus();
                return cliinfo;

            case "sendmsg":
                if (paramClient == null) return "error";

                String msgText = Utils.Base64Decode(param.split(":")[1]);
                Message msg = new Message();
                msg.setFromID(thisClient.profile.getId());
                msg.setToID(paramClient.profile.getId());
                msg.setMessage(msgText);
                msg.setSentAt(new Date());
                try {
                    msgdao.addMessage(msg);
                    paramClient.hasIncomingMessages = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return "error";
                }

            case "getmsg":
                if (paramClient == null) return "error";

                String messageDump = "";
                try {
                    List<Message> lm = msgdao.getAllMessagesInDialog(thisClient.profile.getId(), paramClient.profile.getId(), 0, 100);
                    for (Message msgit : lm) {
                        messageDump += (msgit.getFromID() == thisClient.profile.getId() ? thisClient.nickname : paramClient.nickname) + ":" +
                                        Utils.Base64Encode(msgit.getMessage()) + ";";
                    }
                    thisClient.hasIncomingMessages = false;
                    return messageDump;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return "error";
                }
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

        OnlineClientInfo thisClient = null;

        while (isConnected) {
            //Communication based on text commands goes here
            try {
                String s = inputs.readUTF();
                synchronized (clients) {
                    for (Map.Entry<Integer, OnlineClientInfo> m : clients.entrySet()) {
                        OnlineClientInfo cli = m.getValue();
                    /*}
                    Iterator i = clients.iterator(); // Must be in synchronized block
                    while (i.hasNext()) {
                        OnlineClientInfo cli = (OnlineClientInfo) i.next();*/
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
                    try {
                        thisClient.profile.setStatus("offline");
                        prfdao.updateProfile(thisClient.profile);
                    } catch (Exception ex) {
                        ex.printStackTrace();
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