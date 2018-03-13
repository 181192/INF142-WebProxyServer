package no.pk.webproxyserver;

public class Main {
    public static void main(String[] args) {
//        String adresse = (args[0] != null) ? args[0] : "127.0.0.1";
//        int port;
//        try {
//            port = (args[1] != null) ? Integer.parseInt(args[1]) : 4545;
//        } catch (NumberFormatException e) {
//            port = 4545;
//        }
        String adresse = "158.37.230.110";
        int port = 4545;

        WebProxyServer server = new WebProxyServer(adresse, port);
        System.out.println("Server starts at " + server.getAddress() + ":" + server.getPort());
        Thread t1 = new Thread(server);
        t1.start();
    }
}
