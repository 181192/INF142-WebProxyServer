package no.kalli;

import java.io.IOException;
import java.net.*;

public class Klient {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public Klient() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }

    public void sendMsg(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);
    }

    public String receiveMsg() throws IOException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close() {
        socket.close();
    }

}
