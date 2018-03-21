package no.pk.spam;

import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) throws SocketException, UnknownHostException {
        String adresse = (args.length > 0) ? args[0] : "127.0.0.1";
        int port;
        try {
            port = (args.length > 0) ? Integer.parseInt(args[1]) : 4545;
        } catch (NumberFormatException e) {
            port = 4545;
        }

        String url = (args.length > 1) ? args[2] : "https://hvl.no";
        System.out.println("Spammer5000 connect to " + adresse + ":" + port + ", url: " + url);

        Spammer5000 client = new Spammer5000(adresse, port, url);
        Thread t1 = new Thread(client);
        t1.start();

    }
}
