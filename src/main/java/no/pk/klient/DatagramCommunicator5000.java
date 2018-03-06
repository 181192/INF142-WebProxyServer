package no.pk.klient;

import no.pk.klient.shutdown.IShutdownThreadParent;
import no.pk.klient.shutdown.ShutdownThread;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.file.attribute.BasicFileAttributes;

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
        buf = new byte[1024];
    }

    @Override
    public void run() {
        while(keepRunning){
            sendMsg("/home/pederyo/index.html");
           /* Scanner sc = new Scanner(System.in);
            System.out.println("waiting for input from user...");
            String meld = sc.nextLine();
            String recieve = sendMsg(meld);
            System.out.println(recieve);*/
        }

    }
        public BasicFileAttributes hentFil() {
        DatagramPacket packet;
        BasicFileAttributes attr = null;

        try {
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            byte[] data = packet.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            attr = (BasicFileAttributes) is.readObject();
            System.out.println("printer fil: " + attr.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return attr;
    }

    public String sendMsg(String msg) {

        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4545);
        try {
            socket.send(packet);
            packet = new DatagramPacket(buf, buf.length);
           /* socket.receive(packet);
            byte[] data = packet.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);

            BasicFileAttributes attr = (BasicFileAttributes) is.readObject();
            System.out.println("printer fil: " + attr.toString());*/
        } catch (IOException e) {
            e.printStackTrace();
        } //catch (ClassNotFoundException e) {
           // e.printStackTrace();
        //}
        return new String(packet.getData(), 0, packet.getLength());
    }


    @Override
    public void shutdown() {
        System.out.println("shutting down socket...");
        keepRunning = false;

        socket.close();
    }
}
