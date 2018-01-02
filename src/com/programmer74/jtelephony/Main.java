package com.programmer74.jtelephony;

import com.programmer74.jtdb.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.rmi.CORBA.Util;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        CredentialsDAO cd = new CredentialsDAO();
        ProfilesDAO pd = new ProfilesDAO();
        ContactDAO cond = new ContactDAO();
        System.out.println("Checking db connection...");
        System.out.println("Registered users: ");
        boolean isEmpty = false;
        boolean shouldRegisterUsers = false;
        boolean shouldRegisterContacts = false;
        try {
            List<Credential> cdl = cd.getCredentials();
            for (Credential crd : cdl) {
                System.out.println(crd.getUsername());
            }
            List<Profile> pdl = pd.getProfiles();
            for (Profile prd : pdl) {
                System.out.println(prd.toString());
            }
            isEmpty = cdl.isEmpty() || pdl.isEmpty();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        if (isEmpty) {
            System.err.println("==================");
            System.err.println("Looks like the DB is empty.");
        }

        for (String arg : args) {
            if (arg.equals("--register-users")) {
                shouldRegisterUsers = true;
            }
            if (arg.equals("--register-contacts")) {
                shouldRegisterContacts = true;
            }
        }

        if (isEmpty || shouldRegisterUsers) {
            System.out.println("User registration mode.");
            System.out.println("Please, enter the information in the following format or EOF:");
            System.out.println("Username:Password:E-Mail:FirstName:LastName:City");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                try {
                    String input = br.readLine();
                    if (input == null) {
                        System.out.println("Completed. Goodbye...");
                        System.exit(0);
                    }
                    if (input.equals("")) continue;
                    System.out.println("You entered " + input);
                    String[] entries = input.split(":");

                    Credential crd = new Credential();
                    Profile prf = new Profile();

                    crd.setUsername(entries[0]);
                    crd.setPasswordHash(Utils.stringToMD5(entries[1]));
                    crd.setEmail(entries[2]);
                    cd.addCredential(crd);
                    //assuming that there are no triggers that may change our beloved freshly-added crd's id
                    //other daos have this protection where this is needed
                    System.out.println("Added credential " + crd.toString());
                    prf.setCredentialsID(crd.getId());
                    prf.setFirstName(entries[3]);
                    prf.setLastName(entries[4]);
                    prf.setCity(entries[5]);
                    prf.setStatus("offline");
                    prf.setPictureID(null);
                    pd.addProfile(prf);
                    System.out.println("Added profile " + prf.toString());

                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Please, enter the information in the following format or EOF:");
                    System.out.println("Username:Password:E-Mail:FirstName:LastName:City");
                }
            }
        }

        if (shouldRegisterContacts) {
            System.out.println("Contacts registration mode.");
            System.out.println("Please, enter the information in the following format or EOF:");
            System.out.println("FirstID:SecondID");
            System.out.println("Available IDs:");
            try {
                List<Profile> pdl = pd.getProfiles();
                for (Profile prd : pdl) {
                    System.out.println(prd.toString());
                }
            } catch (Exception ex) {}
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                try {
                    String input = br.readLine();
                    if (input == null) {
                        System.out.println("Completed. Goodbye...");
                        System.exit(0);
                    }
                    if (input.equals("")) continue;
                    System.out.println("You entered " + input);
                    String[] entries = input.split(":");

                    Contact ct = new Contact();
                    ct.setFromID(Integer.parseInt(entries[0]));
                    ct.setToID(Integer.parseInt(entries[1]));
                    ct.setIsAccepted(1);
                    cond.addContact(ct);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Please, enter the information in the following format or EOF:");
                    System.out.println("FirstID:SecondID");
                }
            }
        }

        TelephonyServer ts = new TelephonyServer();
        ts.start(7000);
    }
}
