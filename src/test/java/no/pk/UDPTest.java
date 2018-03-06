package no.pk;

import no.pk.webproxyserver.WebProxyServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UDPTest {
    DatagramCommunicator5000 client;

    @BeforeEach
    public void setup() throws SocketException, UnknownHostException {
        Thread r1 = new Thread(new WebProxyServer());
        r1.start();
        client = new DatagramCommunicator5000();
    }

    @Test
    public void httpMakeConnection(){
        String url = client.sendMsg("https://www.google.no/");
        assertEquals("OK", url);

    }

    @Test
    public void hentFil() {
        String filnavn = "/home/pederyo/index.html";
        String status = client.sendMsg(filnavn);
        assertEquals(status, filnavn);
        filnavn = "/home/pederyo/inftest/hei";
        status = client.sendMsg(filnavn);
        assertEquals(filnavn, status);
    }
    @Test
    public void hentFilerIMappe(){
        String filnavn = "/home/pederyo/inftest/";
        String status = client.sendMsg(filnavn);
        System.out.println(status);
    }

    @AfterEach
    public void tearDown() {
        client.sendMsg("end");
        client.close();
    }
}
