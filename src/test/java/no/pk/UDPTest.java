package no.pk;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UDPTest {
    Klient client;

    @BeforeEach
    public void setup() throws SocketException, UnknownHostException {
        Thread r1 = new Thread(new WebProxyServer());
        r1.start();
        client = new Klient();
    }

    @Test
    public void httpMakeConnection(){
        String url = client.sendMsg("https://www.google.no/");
        assertEquals("OK", url);

    }

    @AfterEach
    public void tearDown() {
        client.sendMsg("end");
        client.close();
    }
}
