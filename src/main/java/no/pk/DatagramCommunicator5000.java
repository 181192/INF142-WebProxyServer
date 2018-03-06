package no.pk;

import java.io.IOException;
import java.net.*;

public class DatagramCommunicator5000 {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public DatagramCommunicator5000() throws SocketException, UnknownHostException {

        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }

    public String sendMsg(String msg) {

        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        try {
            socket.send(packet);
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(packet.getData(), 0, packet.getLength());
    }


    public void close() {
        socket.close();
    }

}
