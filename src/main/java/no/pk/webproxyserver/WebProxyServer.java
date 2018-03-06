package no.pk.webproxyserver;

import java.net.*;

public class WebProxyServer implements Runnable {
    private DatagramSocket server;
    private WebProxyUtil util;
    private int port;

    public WebProxyServer(int port) {
        this.port = port;
        try {
            server = new DatagramSocket(null);
            InetSocketAddress address = new InetSocketAddress("192.168.8.4", port);
            server.bind(address);
            util = WebProxyUtil.getInstance();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        boolean running = true;

        while (running) {

            DatagramPacket packet = util.receivePacket(server);

            // Henter ut ip-adressen og port
            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            String content = new String(packet.getData(), 0, packet.getLength());

            byte[] msg = util.validateInput(content);

            packet = new DatagramPacket(msg, msg.length, address, port);

            if (content.equals("end")) running = false;

            util.sendPacket(server, packet);
        }

        server.close();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}