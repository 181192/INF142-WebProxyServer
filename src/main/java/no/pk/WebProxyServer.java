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
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Mottar pakken fra klienten
            try {
                server.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Henter ut ip-adressen og port
            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            // TODO 1. Lese innholdet, finn ut om det er filsti eller URL (UrlValidator)
            String content = new String(packet.getData(), 0, packet.getLength());

            if (UrlValidator.getInstance().isValid(content)) {
                byte[] files = makeUrlConnection(content);
                packet = new DatagramPacket(files, files.length, address, port);
            }

                // Matcher mapper
            else if (content.matches("^(([\\\\/])[a-zA-ZæøåÆØÅ0-9\\s_@\\-.^!#$%&+={}\\[\\]]+)*([\\\\/])$")) {
                byte[] files = getDirectories(content);
                packet = new DatagramPacket(files, files.length, address, port);

                // Matcher filer
            } else if (content.matches("^(([\\\\/])[a-zA-ZæøåÆØÅ0-9\\s_@\\-.^!#$%&+={}\\[\\]]+)*$")) {
                // TODO er filsti, hent filnavn etc..
                byte[] files = getFilesInDirectory(content);
                packet = new DatagramPacket(files, files.length, address, port);

            } else {
                // TODO Strengen er crap
                byte[] feil = "ayy, fuck".getBytes();
                packet = new DatagramPacket(feil, feil.length, address, port);
            }


            // Lager en ny pakke
            String received = new String(packet.getData(), 0, packet.getLength());

            if (received.equals("end")) {
                running = false;
                continue;
            }

            // Sender pakken til klienten
            try {
                server.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Lukker kommunikasjonen / serveren
        server.close();
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