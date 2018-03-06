package no.pk.klient;

import no.pk.shutdown.IShutdownThread;
import no.pk.shutdown.ShutdownThread;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class DatagramCommunicator5000 implements Runnable, IShutdownThread {

    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private static volatile boolean keepRunning = true;

    public DatagramCommunicator5000(String address, int port) throws SocketException, UnknownHostException {
        this.port = port;
        socket = new DatagramSocket();
        this.address = InetAddress.getByName(address);
        ShutdownThread shutdownThread = new ShutdownThread(this);
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    @Override
    public void run() {
        while (keepRunning) {
            Scanner sc = new Scanner(System.in);
            System.out.println("waiting for input from user...");
            String msg = sc.nextLine();
            sendMsg(msg);
            String receive = getMsg();
            System.out.println(receive);
        }

    }

    public String getMsg() {
        byte[] tmp = new byte[1024];
        DatagramPacket packet = new DatagramPacket(tmp, tmp.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(packet.getData(), 0, packet.getLength());
    }

    public void sendMsg(String msg) {
        byte[] tmp = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(tmp, tmp.length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void shutdown() {
        System.out.println("Shutting down socket...");
        keepRunning = false;

        socket.close();
    }
}
