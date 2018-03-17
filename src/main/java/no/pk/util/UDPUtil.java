package no.pk.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;

public class UDPUtil {

    private static UDPUtil instance;

    /**
     * Instanserer klassen via en statisk tilnaerming.
     *
     * @return Instans av TCPUtil.
     */
    public static UDPUtil getInstance() {
        if (instance == null)
            instance = new UDPUtil();
        return instance;
    }

    /**
     * Metode for å motta en pakke med default stoerrelse paa 1024 bytes.
     *
     * @param server UDP socket som skal hente pakken
     * @return Returnerer pakken
     */
    public DatagramPacket receivePacket(DatagramSocket server) {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        // Mottar pakken fra klienten
        try {
            server.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet;
    }

    /**
     * Metode for å sende en pakke med UDP socket
     *
     * @param server Socket "server"
     * @param packet Packet som skal sendes
     */
    public void sendPacket(DatagramSocket server, DatagramPacket packet) {
        try {
            server.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Henter en UDP melding fra socket
     *
     * @return Meldingen fra packet
     */
    public String getMsg(DatagramSocket socket) {
        byte[] tmp = new byte[1024];
        DatagramPacket packet = new DatagramPacket(tmp, tmp.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(packet.getData(), 0, packet.getLength());
    }

    /**
     * Sender en UDP melding til socket
     *
     * @param msg Melding som skal sendes
     */
    public void sendMsg(byte[] msg, DatagramSocket socket, InetAddress address, int port) {
        DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
