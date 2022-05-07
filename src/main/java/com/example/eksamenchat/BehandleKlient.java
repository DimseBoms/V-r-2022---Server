package com.example.eksamenchat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BehandleKlient implements Runnable {
    Socket socket;
    Date tid;
    String navn;
    String ip;
    String rom;
    String melding;
    ObjectInputStream innStrøm;
    ObjectOutputStream utStrøm;
    public BehandleKlient(Socket socket) {
        this.socket = socket;
        System.out.println("Opprettet ny tilkobling");
    }

    @Override
    public void run() {
        try {
            // Etablerer datastrøm for lesing fra klient og skriving til klient
            innStrøm = new ObjectInputStream(socket.getInputStream());
            utStrøm = new ObjectOutputStream(socket.getOutputStream());
            // Leser initiell input når bruker logges inn.
            Map input = (Map) innStrøm.readObject();
            if (input != null) {
                this.tid = new Date(System.currentTimeMillis());
                this.navn = (String) input.get("brukernavn");
                this.ip = socket.getInetAddress().getHostAddress();
                this.rom = (String) input.get("rom");
                this.melding = (String) input.get("melding");
                System.out.println("Data inn "+navn);
                // Skal feste kall på adaptor/database her

                // Alle aktuelle kall på Adaptor:
                //     Adaptor.insertLogg(tid, navn, ip, null, null);
                //     Adaptor.insertLogg(tid, navn, ip, rom, null);
                //     Adaptor.insertLogg(tid, navn, ip, rom, melding);

                Adaptor.insertLogg(tid,navn, ip, rom, melding);

            }
            ArrayList<String> romliste = new ArrayList<>();
            romliste.add("TestRom1");
            HashMap<Object, Object> svar = new HashMap<>();
            svar.put("status", 1);
            svar.put("romliste", romliste);
            utStrøm.writeObject(svar);
            innStrøm.close();
            utStrøm.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("FEIL "+e.getMessage());
          //  System.out.println();
        }
    }

}
