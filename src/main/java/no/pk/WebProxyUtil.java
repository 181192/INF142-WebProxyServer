package no.pk;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class WebProxyUtil {
    private static WebProxyUtil instance;

    public static WebProxyUtil getInstance() {
        if (instance == null)
            instance = new WebProxyUtil();
        return instance;
    }

    DatagramPacket receivePacket(DatagramSocket server) {
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

    void sendPacket(DatagramSocket server, DatagramPacket packet) {
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

            for(Map.Entry<String, List<String>> field : httpConnection.getHeaderFields().entrySet()){
                System.out.println(field.getValue());
            }

            melding = httpConnection.getResponseMessage().getBytes();
/*
            System.out.println("Response Message " + httpConnection.getResponseMessage());
            System.out.println("Response Code " + httpConnection.getResponseCode());
            System.out.println("Request Method " + httpConnection.getRequestMethod());
            System.out.println("Date "+ httpConnection.getHeaderField(1)); // Det du vil ha ut.*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return melding;

    }

    byte[] validateInput(String content) {
        byte[] msg;

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
