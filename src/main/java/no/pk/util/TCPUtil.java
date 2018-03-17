package no.pk.util;

import no.pk.http.Headers;
import no.pk.http.Status;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCPUtil {
    private static TCPUtil instance;
    private static final int HTTPS = 443;
    private static final int HTTP = 80;

    /**
     * Instanserer klassen via en statisk tilnaerming.
     *
     * @return Instans av TCPUtil.
     */
    public static TCPUtil getInstance() {
        if (instance == null)
            instance = new TCPUtil();
        return instance;
    }

    /**
     * Metoden sender en HTTP request med hostname og path som input.
     *
     * @param client   Socket
     * @param hostname Hostname
     * @param path     Path
     */
    private void sendHTTPRequest(Socket client, String hostname, String path) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (pw != null) {
            pw.println("GET /" + path + " HTTP/1.1");
            pw.println("Host: " + hostname);
            pw.println("Connection: close");
            pw.println("");
            pw.flush();
        }
    }


    /**
     * Metoden lager en socket basert paa om det er HTTP eller HTTPS (SSL)
     *
     * @param hostname Hostname
     * @return Socket
     * @throws IOException IOException
     */
    private Socket createSocket(String hostname) throws IOException {
        SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket client;
        try {
            client = ssf.createSocket(InetAddress.getByName(hostname), HTTPS);
        } catch (ConnectException e) {
            client = new Socket(hostname, HTTP);
        }
        return client;
    }

    /**
     * Metoden validerer URL, og henter ut hostname og path.
     *
     * @param content URL'en som skal kobles til.
     * @return En melding med informasjon fra webserveren som ble koblet til.
     * @throws IOException IOException
     */
    public byte[] makeUrlConnection(String content) throws IOException {
        byte[] melding;

        String hostname = "";
        String path = "";
        if (content.matches("http(s|)://.+\\.\\w+\\w.*")) {
            String[] arr = content.split("//", 2)[1].split("/", 2);
            hostname = arr[0];
            if (arr.length > 1)
                path = arr[1];
        }

        Headers headers = connectToHostname(hostname, path);

        // Haandterer statuskodene 301 og 302 og redirekter til hva som staar i "Location" headeren.
        if (headers.getStatus().getStatusCode().equals("302") || headers.getStatus().getStatusCode().equals("301")) {
            String location = headers.getHeaders().get("Location");

            System.out.println("Got a " + headers.getStatus().getStatusCode() + " redirecting to " + location);

            Pattern pt = Pattern.compile("(http(s|)://.+/)");
            Matcher matcher = pt.matcher(location);
            if (matcher.find()) {
                path = location.substring(matcher.group(1).length(), location.length());
            }
            String[] arr = location.split("//", 2)[1].split("/", 2);
            hostname = arr[0];
            headers = connectToHostname(hostname, path);
        }

        melding = headers.toString().getBytes();

        if (headers.getStatus().getStatusCode().equals("400") || headers.getStatus().getStatusCode().equals("404")) {
            melding = ("Sorry mate, " + headers.getStatus().getStatusCode()).getBytes();
        }

        return melding;
    }

    /**
     * Hjelpemetode for makeUrlConnection. Lager en TCP Socket, sender en raw http forespoersel,
     * leser tilbake informasjonen fra webserveren. Hente ut statuskoden og headerene.
     *
     * @param hostname Hostname etc "hvl.no"
     * @param path     Path etc "/studier/studieprogram/2018h/phdcs/"
     * @return Headers objekt med statuskoden og alle headerene.
     * @throws IOException IOException
     */
    private Headers connectToHostname(String hostname, String path) throws IOException {
        Socket client = createSocket(hostname);

        sendHTTPRequest(client, hostname, path);

        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

        Status st = new Status(br.readLine());
        Headers headers = new Headers(st);
        headers.setupHeaders(headers, br);
        br.close();

        return headers;
    }

}
