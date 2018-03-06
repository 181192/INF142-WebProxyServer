package no.pk.klient;


import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) throws SocketException, UnknownHostException {
        DatagramCommunicator5000 client = new DatagramCommunicator5000();
        Thread t1 = new Thread(client);
        t1.start();


    }

}
