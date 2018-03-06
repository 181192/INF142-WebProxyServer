package no.pk.klient;

import no.pk.klient.shutdown.IShutdownThreadParent;
import no.pk.klient.shutdown.ShutdownThread;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

public class DatagramCommunicator5000 implements Runnable, IShutdownThreadParent {

    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private static volatile boolean keepRunning = true;


    private byte[] buf;

    public DatagramCommunicator5000(int port) throws SocketException, UnknownHostException {
        this.port = port;
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
        ShutdownThread fShutdownThread = new ShutdownThread(this);
        Runtime.getRuntime().addShutdownHook(fShutdownThread);
        buf = new byte[1024];
    }

    @Override
    public void run() {
        while (keepRunning) {
            Scanner sc = new Scanner(System.in);
            System.out.println("waiting for input from user...");
            String msg = sc.nextLine();
            sendMsg(msg);
            String recieve = getMsg();
            System.out.println(recieve);
        }

    }

    public String getMsg() {
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(packet.getData(), 0, packet.getLength());
    }

    public void sendMsg(String msg) {
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void shutdown() {
        System.out.println("shutting down socket...");
        keepRunning = false;

        socket.close();
    }
}
