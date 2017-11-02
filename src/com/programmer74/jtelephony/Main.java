package com.programmer74.jtelephony;

import com.programmer74.jtdb.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        CredentialsDAO cd = new CredentialsDAO();

        try {
            List<Credential> cdl = cd.getCredentials();
            for (Credential crd : cdl) {
                System.out.println(crd.toString());
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
