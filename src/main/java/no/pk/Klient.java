package no.pk;

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

    public void sendMsg(String msg) {
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveMsg()  {
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String received = new String(packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close() {
        socket.close();
    }

}
