package no.pk;

import no.pk.client.DatagramCommunicator5000;
import no.pk.util.UDPUtil;
import no.pk.webproxyserver.WebProxyServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UDPTest {
    private DatagramCommunicator5000 client;
    private UDPUtil udp;

    @BeforeAll
    void setup() throws SocketException, UnknownHostException {
        int port = 4546;
        String address = "192.168.8.11";

        WebProxyServer r1 = new WebProxyServer(address, port);
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
