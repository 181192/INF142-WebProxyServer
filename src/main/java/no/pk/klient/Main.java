package no.pk.klient;


import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) throws SocketException, UnknownHostException {
//        String adresse = (args[0] != null) ? args[0] : "127.0.0.1";
//        int port;
//        try {
//            port = (args[1] != null) ? Integer.parseInt(args[1]) : 4545;
//        } catch (NumberFormatException e) {
//            port = 4545;
//        }

        String adresse = "158.37.230.110";
        int port = 4545;

        DatagramCommunicator5000 client = new DatagramCommunicator5000(adresse, port);
        Thread t1 = new Thread(client);
        t1.start();
    }

}
