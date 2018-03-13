package no.pk.webproxyserver;

import no.pk.http.HttpHeader;
import no.pk.http.Status;
import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.UrlValidator;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WebProxyUtil {
    private static WebProxyUtil instance;
    private static final int HTTPS = 443;
    private static final int HTTP = 80;

    public static WebProxyUtil getInstance() {
        if (instance == null)
            instance = new WebProxyUtil();
        return instance;
    }

    private void printHTTPMessage(Socket client, String hostname, String path, String query) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        pw.println("GET " + path + query + " HTTP/1.1");
        pw.println("Host: " + hostname);
        pw.println("Connection: close");
        pw.println("");
        pw.flush();
    }

    private void setupHeaders(HttpHeader httpHeader, BufferedReader br) throws IOException {
        HashMap<String, String> headers = new HashMap<>();
        for (; ; ) {
            String line = br.readLine();
            if (line.equals(""))
                break;
            String[] header = line.split(":", 2);
            headers.put(header[0].trim(), header[1].trim());
        }
        httpHeader.setHeaders(headers);

    }

    private Socket createSocket(String hostname) throws IOException {
        SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket client = null;
        try {
            client = ssf.createSocket(InetAddress.getByName(hostname), HTTPS);
        } catch (ConnectException e) {
            client = new Socket(hostname, HTTP);
        }
        return client;
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

        String hostname = "google.com";
        String path = "/";
        String query = "";


        Socket client = createSocket(hostname);

        printHTTPMessage(client, hostname, path, query);

        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String t;

        Status st = new Status(br.readLine());
        HttpHeader httpHeader = new HttpHeader(st);
        setupHeaders(httpHeader, br);

        System.out.println(httpHeader.toString());
        System.out.println(" ");

        if (httpHeader.getStatus().getStatusCode().matches("302") || httpHeader.getStatus().getStatusCode().matches("301")) {
            String location = httpHeader.getHeaders().get("Location");
            System.out.println(location);
            Pattern pt = Pattern.compile("(http(s|):\\/\\/.+\\/)");
            Matcher matcher = pt.matcher(location);
            if (matcher.find()) {
                path = "/";
                query = location.substring(matcher.group(1).length(), location.length());
                System.out.println(path);
                System.out.println(query);
//                path = matcher.group(1);
            }
            client = createSocket(hostname);
            printHTTPMessage(client, hostname, path, query);

            br = new BufferedReader(new InputStreamReader(client.getInputStream()));


            st = new Status(br.readLine());
            httpHeader = new HttpHeader(st);
            setupHeaders(httpHeader, br);

            System.out.println(httpHeader.toString());
            System.out.println(" ");
        }

        br.close();




        byte[] melding = null;
        try {
            HttpURLConnection httpConnection;

            httpConnection = (HttpURLConnection) new URL(content).openConnection();
            httpConnection.setRequestMethod("GET");

            if(httpConnection.getHeaderFields() != null){
                for (Map.Entry<String, List<String>> field : httpConnection.getHeaderFields().entrySet()) {
                    if(field != null)
                        System.out.println(field.getValue());
                }
                melding = (httpConnection.getResponseMessage() + " " + httpConnection.getResponseCode()).getBytes();
            }else {
                melding = "Not Valid Domain Name".getBytes();
            }

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
            msg = getFilesInDirectory(content);


            // Matcher filer
        } else if (content.matches("^(([\\\\/])[a-zA-ZæøåÆØÅ0-9\\s_@\\-.^!#$%&+={}\\[\\]]+)*$")) {
            msg = getFile(content);

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

    private byte[] getFile(String path) {
        StringBuilder sb = new StringBuilder();
        Path file = Paths.get(path);
        sb.append(file.getFileName());
        return sb.toString().getBytes();
    }
}
