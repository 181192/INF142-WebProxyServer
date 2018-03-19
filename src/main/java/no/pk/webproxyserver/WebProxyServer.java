package no.pk.webproxyserver;

import no.pk.shutdown.IShutdownThread;
import no.pk.shutdown.ShutdownThread;
import no.pk.util.FileUtil;
import no.pk.util.TCPUtil;
import no.pk.util.UDPUtil;
import org.apache.commons.validator.routines.UrlValidator;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebProxyServer implements IShutdownThread {
    private DatagramSocket server;
    private ExecutorService pool;
    private TCPUtil tcp;
    private UDPUtil udp;
    private FileUtil files;
    private int port;
    private String address;
    private static volatile boolean keepRunning = true;

    public WebProxyServer(String address, int port) {
        this.port = port;
        this.address = address;
        ShutdownThread shutdownThread = new ShutdownThread(this);
        Runtime.getRuntime().addShutdownHook(shutdownThread);

        try {
            server = new DatagramSocket(null);
            InetSocketAddress sa = new InetSocketAddress(address, port);
            server.bind(sa);
            tcp = TCPUtil.getInstance();
            udp = UDPUtil.getInstance();
            files = FileUtil.getInstance();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        /*
            Bruker ett thread pool for aa sende hver forespoersel inn i en "traad koe".
            Slik at det er 10 traader som kjoerer hele tiden, men kommer det flere enn 10 forespoersler paa engang vil
            de automatisk bli satt i koe.
        */
        this.pool = Executors.newFixedThreadPool(10);
    }

    /**
     * Metode som kjører heletiden. Den tar i mot forespoersler via UDP,
     * og sender informasjon tilbake til brukeren basert paa hva input var.
     */
    public void start() {
        while (keepRunning) {

            DatagramPacket packet = udp.receivePacket(server);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            System.out.println("Got packet from " + address + " at port " + port + "!");
            String content = new String(packet.getData(), 0, packet.getLength());

            pool.execute(() -> udp.sendMsg(validateInput(content), server, address, port));
        }
    }

    /**
     * Validator for input fra client. Validerer om det er en URL eller en filsti lokalt på WPS.
     * Og deretter lager en melding basert på input
     *
     * @param content Input fra brukeren
     * @return En melding basert paa hvordan informasjon en fikk fra brukeren
     */
    private byte[] validateInput(String content) {
        byte[] msg = new byte[1024];

        // Validerer om det er en gyldig URL
        if (UrlValidator.getInstance().isValid(content))
            try {
                msg = tcp.makeUrlConnection(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Matcher mapper
        else if (content.matches("^(([\\\\/])[a-zA-ZæøåÆØÅ0-9\\s_@\\-.^!#$%&+={}\\[\\]]+)*([\\\\/])$"))
            msg = files.getFilesInDirectory(content);

            // Matcher filer
        else if (content.matches("^(([\\\\/])[a-zA-ZæøåÆØÅ0-9\\s_@\\-.^!#$%&+={}\\[\\]]+)*$"))
            msg = files.getFile(content);

        else
            msg = "Ups, did you forget http(s) for URL or '/' for filepath".getBytes();

        return msg;
    }

    /**
     * Avslutter programmet når man trykker ctrl + c
     */
    @Override
    public void shutdown() {
        System.out.println("\n\nServer shutting down!!");
        keepRunning = false;
        pool.shutdown();
        server.close();

    }

    public DatagramSocket getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }
}