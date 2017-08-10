package com.programmer74.jtelephony;

public class Main {

    public static void main(String[] args) {
        TelephonyServer ts = new TelephonyServer();
        ts.start(7000);
    }

    //TODO: Превратить list в map чтобы все индексы оставались рабочими
}
