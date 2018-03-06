package no.pk.webproxyserver;

import no.pk.shutdown.IShutdownThread;

import java.net.*;

public class WebProxyServer implements Runnable, IShutdownThread {
    private DatagramSocket server;
    private WebProxyUtil util;
    private int port;
    private static volatile boolean keepRunning = true;

    public WebProxyServer(String address, int port) {
        this.port = port;
        try {
            server = new DatagramSocket(null);
            InetSocketAddress sa = new InetSocketAddress(address, port);
            server.bind(sa);
            util = WebProxyUtil.getInstance();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (keepRunning) {

            DatagramPacket packet = util.receivePacket(server);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            String content = new String(packet.getData(), 0, packet.getLength());

            byte[] msg = util.validateInput(content);

            packet = new DatagramPacket(msg, msg.length, address, port);

            util.sendPacket(server, packet);
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void shutdown() {
        keepRunning = false;
        try {
            server.bind(null);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        server.close();
    }
}