package no.kalli;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.IOException;
import java.net.*;

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
            this.server = new DatagramSocket(4545);
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

            if (UrlValidator.getInstance().isValid(content))
                makeUrlConnection(content);

                // Matcher mapper
            else if (content.matches("^(([\\\\/])[a-zA-ZæøåÆØÅ0-9\\s_@\\-.^!#$%&+={}\\[\\]]+)*([\\\\/])$")) {


                // Matcher filer
            } else if (content.matches("^(([\\\\/])[a-zA-ZæøåÆØÅ0-9\\s_@\\-.^!#$%&+={}\\[\\]]+)*$")) {
                // TODO er filsti, hent filnavn etc..


            } else {
                // TODO Strengen er crap

            }


            // Lager en ny pakke
            // TODO 4. Sende tilbake respons til klienten, enten HTTP header eller filnavn
            packet = new DatagramPacket(buffer, buffer.length, address, port);
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

    private void makeUrlConnection(String content) {
        try {
            HttpURLConnection httpConnection;httpConnection = (HttpURLConnection) new URL(content).openConnection();
            httpConnection.setRequestMethod("GET");
            buffer = httpConnection.getResponseMessage().getBytes();
            System.out.println(httpConnection.getResponseMessage());
            System.out.println(httpConnection.getRequestMethod());
            System.out.println(httpConnection.getHeaderField(1)); // Det du vil ha ut.
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}