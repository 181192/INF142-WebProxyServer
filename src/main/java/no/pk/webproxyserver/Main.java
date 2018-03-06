package no.pk.webproxyserver;

public class Main {
    public static void main(String[] args) {
        WebProxyServer server = new WebProxyServer("192.168.8.4", 4545);
        System.out.println("Server starts at: " + server.getPort());
        Thread t1 = new Thread(server);
        t1.start();
    }
}
