package no.pk;

import no.pk.klient.DatagramCommunicator5000;
import no.pk.webproxyserver.WebProxyServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UDPTest {
    private DatagramCommunicator5000 client;

    @BeforeEach
    void setup() throws SocketException, UnknownHostException {
        int port = 4545;
        String address = "192.168.8.4";

        Thread r1 = new Thread(new WebProxyServer(address, port));
        r1.start();
        client = new DatagramCommunicator5000(address, port);
    }

    @Test
    void httpMakeConnection(){
        client.sendMsg("https://www.google.no/");
        String msg = client.getMsg();
        assertEquals("OK 200", msg);

    }
    @Test
    void HentFilObject() {
        String path = "/home/k/index.html";
        String filename = "index.html";
        client.sendMsg(path);
        String msg = client.getMsg();
        assertEquals(filename, msg);

    }

    @AfterEach
    void tearDown() {
        client.shutdown();
    }
}
