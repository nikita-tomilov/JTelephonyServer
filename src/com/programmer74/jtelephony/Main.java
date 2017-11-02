package com.programmer74.jtelephony;

import com.programmer74.jtdb.*;

import javax.rmi.CORBA.Util;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        CredentialsDAO cd = new CredentialsDAO();
        ProfilesDAO pd = new ProfilesDAO();
        System.out.println("Checking db connection...");
        System.out.println("Registered users: ");
        try {
            List<Credential> cdl = cd.getCredentials();
            for (Credential crd : cdl) {
                System.out.println(crd.getUsername());
            }

            List<Profile> pdl = pd.getProfiles();
            for (Profile prd : pdl) {
                System.out.println(prd.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        TelephonyServer ts = new TelephonyServer();
        ts.start(7000);
    }

    //TODO: Превратить list в map чтобы все индексы оставались рабочими
}
