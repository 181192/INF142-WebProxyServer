package no.pk;

import no.pk.client.DatagramCommunicator5000;
import no.pk.util.UDPUtil;
import no.pk.webproxyserver.WebProxyServer;
import org.junit.jupiter.api.*;

import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

class UDPTest {
    private DatagramCommunicator5000 client;
    private UDPUtil udp;

    @BeforeEach
    void setup() throws SocketException, UnknownHostException {
        int port = 0;
        String address = "192.168.8.11";

        Thread r1 = new Thread(new WebProxyServer(address, port));
        r1.start();
        client = new DatagramCommunicator5000(address, port);
        udp = UDPUtil.getInstance();
    }

    @Test
    void httpMakeConnection(){
        byte[] path = "https://www.google.no/".getBytes();
        udp.sendMsg(path, client.getSocket(), client.getAddress(), client.getPort());
        String msg = udp.getMsg(client.getSocket());
        assertEquals("OK 200", msg);

    }
    @Test
    void HentFilObject() {
        byte[] path = "/home/k/index.html".getBytes();
        String filename = "index.html";
        udp.sendMsg(path, client.getSocket(), client.getAddress(), client.getPort());
        String msg = udp.getMsg(client.getSocket());
        assertEquals(filename, msg);

    }

    @AfterEach
    void tearDown() {
        client.shutdown();
    }
}
