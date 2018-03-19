package no.pk.client;

import no.pk.shutdown.IShutdownThread;
import no.pk.shutdown.ShutdownThread;
import no.pk.util.UDPUtil;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Klienten DatagramCommunicator5000
 */
public class DatagramCommunicator5000 implements Runnable, IShutdownThread {

    private DatagramSocket socket;
    private UDPUtil udp;
    private InetAddress address;
    private int port;
    private static volatile boolean keepRunning = true;

    public DatagramCommunicator5000(String address, int port) throws SocketException, UnknownHostException {
        this.port = port;
        this.address = InetAddress.getByName(address);
        socket = new DatagramSocket();
        udp = UDPUtil.getInstance();
        ShutdownThread shutdownThread = new ShutdownThread(this);
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    /**
     * Thread som kjoerer hele tiden. Den tar imot input fra brukeren, sender det til WPS,
     * og mottar resultatet og printer det ut til brukeren.
     */
    @Override
    public void run() {
        while (keepRunning) {
            Scanner sc = new Scanner(System.in);
            System.out.println("waiting for input from user...");
            byte[] msg = sc.nextLine().getBytes();
            udp.sendMsg(msg, socket, address, port);
            String receive = udp.getMsg(socket);
            System.out.println(receive);
        }
    }

    /**
     * Shutdown kode for ctrl + c
     */
    @Override
    public void shutdown() {
        System.out.println("\n\nShutting down client...");
        keepRunning = false;
        socket.close();
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
