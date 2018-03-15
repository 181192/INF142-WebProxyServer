package no.pk.webproxyserver;

import no.pk.http.HttpHeader;
import no.pk.http.Status;
import org.apache.commons.validator.routines.UrlValidator;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebProxyUtil {
    private static WebProxyUtil instance;
    private static final int HTTPS = 443;
    private static final int HTTP = 80;

    /**
     * Instanserer klassen via en statisk tilnaerming.
     *
     * @return Instans av WebProxyUtil.
     */
    public static WebProxyUtil getInstance() {
        if (instance == null)
            instance = new WebProxyUtil();
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
        pw.println("GET /" + path + " HTTP/1.1");
        pw.println("Host: " + hostname);
        pw.println("Connection: close");
        pw.println("");
        pw.flush();
    }

    /**
     * Metoden setter opp headerene
     *
     * @param httpHeader HttpHeader
     * @param br         BufferedReader
     * @throws IOException
     */
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

    /**
     * Metoden lager en socket basert paa om det er HTTP eller HTTPS (SSL)
     *
     * @param hostname Hostname
     * @return Socket
     * @throws IOException
     */
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

    /**
     * Metode for å motta en pakke med default stoerrelse paa 1024 bytes.
     *
     * @param server UDP socket som skal hente pakken
     * @return Returnerer pakken
     */
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

    /**
     * Metode for å sende en pakke med UDP socket
     *
     * @param server Socket "server"
     * @param packet Packet som skal sendes
     */
    void sendPacket(DatagramSocket server, DatagramPacket packet) {
        try {
            server.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Metoden validerer URL, og henter ut hostname og path.
     *
     * @param content URL'en som skal kobles til.
     * @return En melding med informasjon fra webserveren som ble koblet til.
     * @throws IOException
     */
    private byte[] makeUrlConnection(String content) throws IOException {
        byte[] melding;

        String hostname = "";
        String path = "";
        if (content.matches("http(s|):\\/\\/.+\\.\\w+\\w.*")) {
            String[] arr = content.split("//", 2)[1].split("/", 2);
            hostname = arr[0];
            if (arr.length > 1)
                path = arr[1];
        }

        HttpHeader httpHeader = connectToHostname(hostname, path);

        // Haandterer statuskodene 301 og 302 og redirekter til hva som staar i "Location" headeren.
        if (httpHeader.getStatus().getStatusCode().equals("302") || httpHeader.getStatus().getStatusCode().equals("301")) {
            String location = httpHeader.getHeaders().get("Location");

            System.out.println("Got a " + httpHeader.getStatus().getStatusCode() + " redirecting to " + location);

            Pattern pt = Pattern.compile("(http(s|):\\/\\/.+\\/)");
            Matcher matcher = pt.matcher(location);
            if (matcher.find()) {
                path = location.substring(matcher.group(1).length(), location.length());
            }
            String[] arr = location.split("//", 2)[1].split("/", 2);
            hostname = arr[0];
            httpHeader = connectToHostname(hostname, path);
        }

        melding = httpHeader.toString().getBytes();

        if (httpHeader.getStatus().getStatusCode().equals("400") || httpHeader.getStatus().getStatusCode().equals("404")) {
            melding = ("Sorry mate, " + httpHeader.getStatus().getStatusCode()).getBytes();
        }

        return melding;
    }

    /**
     * Hjelpemetode for makeUrlConnection. Lager en TCP Socket, sender en raw http forespoersel,
     * leser tilbake informasjonen fra webserveren. Hente ut statuskoden og headerene.
     *
     * @param hostname Hostname etc "hvl.no"
     * @param path     Path etc "/studier/studieprogram/2018h/phdcs/"
     * @return HttpHeader objekt med statuskoden og alle headerene.
     * @throws IOException
     */
    private HttpHeader connectToHostname(String hostname, String path) throws IOException {
        Socket client = createSocket(hostname);

        sendHTTPRequest(client, hostname, path);

        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

        Status st = new Status(br.readLine());
        HttpHeader httpHeader = new HttpHeader(st);
        setupHeaders(httpHeader, br);
        br.close();

        return httpHeader;
    }

    /**
     * Validator for input fra klient. Validerer om det er en URL eller en filsti lokalt på WPS.
     * Og deretter lager en melding basert på input
     *
     * @param content Input fra brukeren
     * @return En melding basert paa hvordan informasjon en fikk fra brukeren
     */
    byte[] validateInput(String content) {
        byte[] msg = new byte[1024];

        // Validerer om det er en gyldig URL
        if (UrlValidator.getInstance().isValid(content)) {
            try {
                msg = makeUrlConnection(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Matcher mapper
        else if (content.matches("^(([\\\\/])[a-zA-ZæøåÆØÅ0-9\\s_@\\-.^!#$%&+={}\\[\\]]+)*([\\\\/])$")) {
            msg = getFilesInDirectory(content);


            // Matcher filer
        } else if (content.matches("^(([\\\\/])[a-zA-ZæøåÆØÅ0-9\\s_@\\-.^!#$%&+={}\\[\\]]+)*$")) {
            msg = getFile(content);

        } else {
            msg = "Ups, did you forget http(s) for URL or '/' for filepath".getBytes();
        }

        return msg;
    }

    /**
     * Henter ut alle filene i en mappe paa WPS
     *
     * @param path Stien til mappen
     * @return Byte array med informasjon om filene i mappen
     */
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

    /**
     * Henter informasjon om en fil paa WPS
     *
     * @param path Stien til filen
     * @return Byte array med informasjon om filen
     */
    private byte[] getFile(String path) {
        StringBuilder sb = new StringBuilder();
        Path file = Paths.get(path);
        sb.append(file.getFileName());
        return sb.toString().getBytes();
    }
}
