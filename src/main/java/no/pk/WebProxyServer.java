package no.pk;

import org.apache.commons.validator.routines.UrlValidator;

import javax.print.DocFlavor;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class WebProxyServer implements Runnable {
    private DatagramSocket server;
    private boolean running;
    private byte[] buffer;

    public WebProxyServer(DatagramSocket server, boolean running, byte[] buffer) {
        this.server = server;
        this.running = running;
        this.buffer = buffer;
    }

    public WebProxyServer() {
        try {
            server = new DatagramSocket(4445);
            buffer = new byte[1024];
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        running = true;

        while (running) {

            DatagramPacket packet = receivePacket();

            // Henter ut ip-adressen og port
            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            String content = new String(packet.getData(), 0, packet.getLength());
            byte[] msg = validateInput(content);

            packet = new DatagramPacket(msg, msg.length, address, port);

            // Lager en ny pakke
            String received = new String(packet.getData(), 0, packet.getLength());

            sendPacket(packet);

            if (received.equals("end")) {
                running = false;
            }
        }

        // Lukker kommunikasjonen / serveren
        server.close();
    }

    private DatagramPacket receivePacket() {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        // Mottar pakken fra klienten
        try {
            server.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet;
    }

    private void sendPacket(DatagramPacket packet) {
        try {
            server.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private byte[] makeUrlConnection(String content) {
        byte[] melding = null;
        try {
            HttpURLConnection httpConnection;

            httpConnection = (HttpURLConnection) new URL(content).openConnection();

            httpConnection.setRequestMethod("GET");

            melding = httpConnection.getResponseMessage().getBytes();

            System.out.println("Response Message " + httpConnection.getResponseMessage());
            System.out.println("Response Code " + httpConnection.getResponseCode());
            System.out.println("Request Method " + httpConnection.getRequestMethod());
            System.out.println("Date "+ httpConnection.getHeaderField(1)); // Det du vil ha ut.
        } catch (IOException e) {
            e.printStackTrace();
        }
        return melding;

    }

    private byte[] validateInput(String content) {
        byte[] msg = null;

        if (UrlValidator.getInstance().isValid(content)) {
            msg = makeUrlConnection(content);
        }
        // Matcher mapper
        else if (content.matches("^(([\\\\/])[a-zA-ZæøåÆØÅ0-9\\s_@\\-.^!#$%&+={}\\[\\]]+)*([\\\\/])$")) {
            msg = getDirectories(content);

            // Matcher filer
        } else if (content.matches("^(([\\\\/])[a-zA-ZæøåÆØÅ0-9\\s_@\\-.^!#$%&+={}\\[\\]]+)*$")) {
            msg = getFilesInDirectory(content);

        } else {
            // TODO Strengen er crap
            msg = "ayy, fuck".getBytes();
        }

        return msg;
    }

    private byte[] getFilesInDirectory(String path) {

        StringBuilder sb = new StringBuilder();

        try {
            Files.walk(Paths.get(path))
                    .filter(Files::isRegularFile)
                    .forEach(file -> sb.append(file.toString()).append(", \n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString().getBytes();
    }

    private byte[] getDirectories(String path) {
        StringBuilder sb = new StringBuilder();

        try {
            Files.walk(Paths.get(path))
                    .filter(Files::isDirectory)
                    .forEach(file -> sb.append(file.toString()).append(", \n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString().getBytes();
    }
}