package no.pk.klient;

import no.pk.klient.shutdown.IShutdownThreadParent;
import no.pk.klient.shutdown.ShutdownThread;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class DatagramCommunicator5000 implements Runnable, IShutdownThreadParent {

    private DatagramSocket socket;
    private InetAddress address;
    private ShutdownThread fShutdownThread;
    static volatile boolean keepRunning = true;

    private byte[] buf;

    public DatagramCommunicator5000() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
        fShutdownThread = new ShutdownThread(this);
        Runtime.getRuntime().addShutdownHook(fShutdownThread);
    }

    @Override
    public void run() {
        while(keepRunning){
            Scanner sc = new Scanner(System.in);
            System.out.println("waiting for input from user...");
            String meld = sc.nextLine();
        }

    }

    public String sendMsg(String msg) {

        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        try {
            socket.send(packet);
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(packet.getData(), 0, packet.getLength());
    }


    @Override
    public void shutdown() {
        System.out.println("shutting down socket...");
        keepRunning = false;
        socket.close();
    }
}
