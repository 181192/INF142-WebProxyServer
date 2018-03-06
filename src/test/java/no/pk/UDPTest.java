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
        new WebProxyServer().run();
        client = new Klient();
    }

    @Test
    public void whenCanSendAndReceivePacket_thenCorrect() {
        client.sendMsg("hello server");
        String meld = client.receiveMsg();
        assertEquals("hello server", meld);
        client.sendMsg("server is working");
        meld = client.receiveMsg();
        assertFalse(meld.equals("hello server"));
    }

    @AfterEach
    public void tearDown() {
        client.sendMsg("end");
        client.close();
    }
}
