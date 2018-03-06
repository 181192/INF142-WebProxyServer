package no.pk;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class WebProxyServer implements Runnable {
    private DatagramSocket server;
    private WebProxyUtil util;

    WebProxyServer() {
        try {
            server = new DatagramSocket(4445);
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


}