package no.pk.spam;

import no.pk.shutdown.IShutdownThread;
import no.pk.shutdown.ShutdownThread;
import no.pk.util.UDPUtil;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * "spam" client for testing purpose Spammer5000
 */
public class Spammer5000 implements Runnable, IShutdownThread {

    private static volatile boolean keepRunning = true;
    private DatagramSocket socket;
    private UDPUtil udp;
    private InetAddress address;
    private int port;
    private String url;

    Spammer5000(String address, int port, String url) throws SocketException, UnknownHostException {
        this.port = port;
        this.address = InetAddress.getByName(address);
        this.url = url;
        socket = new DatagramSocket();
        udp = UDPUtil.getInstance();
        ShutdownThread shutdownThread = new ShutdownThread(this);
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    @Override
    public void run() {
        while (keepRunning) {
            byte[] msg = url.getBytes();
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
