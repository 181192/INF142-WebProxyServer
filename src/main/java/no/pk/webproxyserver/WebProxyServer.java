package no.pk.webproxyserver;

import no.pk.shutdown.IShutdownThread;
import no.pk.shutdown.ShutdownThread;

import java.net.*;

public class WebProxyServer implements Runnable, IShutdownThread {
    private DatagramSocket server;
    private WebProxyUtil util;
    private int port;
    private String address;
    private static volatile boolean keepRunning = true;

    public WebProxyServer(String address, int port) {
        this.port = port;
        this.address = address;
        ShutdownThread shutdownThread = new ShutdownThread(this);
        Runtime.getRuntime().addShutdownHook(shutdownThread);

        try {
            server = new DatagramSocket(null);
            InetSocketAddress sa = new InetSocketAddress(address, port);
            server.bind(sa);
            util = WebProxyUtil.getInstance();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Thread som kjører heletiden. Den tar i mot forespoersler via UDP,
     * og sender informasjon tilbake til brukeren basert paa hva input var.
     */
    @Override
    public void run() {
        while (keepRunning) {

            DatagramPacket packet = util.receivePacket(server);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            System.out.println("Got packet from " + address + " at port " + port + "!");

            String content = new String(packet.getData(), 0, packet.getLength());

            byte[] msg = util.validateInput(content);

            packet = new DatagramPacket(msg, msg.length, address, port);

            util.sendPacket(server, packet);
        }
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    /**
     * Avslutter programmet når man trykker ctrl + c
     */
    @Override
    public void shutdown() {
        System.out.println("\n\nServer shutting down!!");
        keepRunning = false;
        server.close();
    }
}