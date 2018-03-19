package no.pk.spam;

import no.pk.shutdown.IShutdownThread;
import no.pk.shutdown.ShutdownThread;
import no.pk.util.UDPUtil;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Klienten Client
 */
public class Client implements Runnable, IShutdownThread {

    private static volatile boolean keepRunning = true;
    private DatagramSocket socket;
    private UDPUtil udp;
    private InetAddress address;
    private int port;

    Client(String address, int port) throws SocketException, UnknownHostException {
        this.port = port;
        this.address = InetAddress.getByName(address);
        socket = new DatagramSocket();
        udp = UDPUtil.getInstance();
        ShutdownThread shutdownThread = new ShutdownThread(this);
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    @Override
    public void run() {
        while (keepRunning) {
            byte[] msg = "https://hvl.no".getBytes();
            udp.sendMsg(msg, socket, address, port);
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
}
