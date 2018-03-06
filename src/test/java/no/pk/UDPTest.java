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

public class UDPTest {
    DatagramCommunicator5000 client;
    int port = 4545;

    @BeforeEach
    public void setup() throws SocketException, UnknownHostException {
        Thread r1 = new Thread(new WebProxyServer(port));
        r1.start();
        client = new DatagramCommunicator5000(port);
    }

    @Test
    public void httpMakeConnection(){
        client.sendMsg("https://www.google.no/");
        String msg = client.getMsg();
        assertEquals("OK", msg);

    }
    @Test
    public void HentFilObject() {
        String path = "/home/k/index.html";
        String filename = "index.html";
        client.sendMsg(path);
        String msg = client.getMsg();
        assertEquals(filename, msg);

    }

    @AfterEach
    public void tearDown() {
        client.sendMsg("end");
        client.shutdown();
    }
}
