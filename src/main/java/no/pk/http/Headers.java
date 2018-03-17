package no.pk.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Klasse for å representere http headere
 */
public class Headers {
    private Status status;
    private HashMap<String, String> headers;

    public Headers(Status st) {
        status = st;
        headers = new HashMap<>();
    }

    public Status getStatus() {
        return status;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    private void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        StringBuffer ss = new StringBuffer();
        ss.append(status.toString()).append("\r\n");
        headers.forEach((k, v) -> ss.append(k).append(" ").append(v).append("\r\n"));
        return ss.toString();
    }

    /**
     * Metoden setter opp headerene
     *
     * @param httpHeader Headers
     * @param br         BufferedReader
     * @throws IOException IOException on readline
     */
    public void setupHeaders(Headers httpHeader, BufferedReader br) throws IOException {
        HashMap<String, String> headers = new HashMap<>();
        for (; ; ) {
            String line = br.readLine();
            if (line.equals(""))
                break;
            String[] header = line.split(":", 2);
            headers.put(header[0].trim(), header[1].trim());
        }
        httpHeader.setHeaders(headers);
    }
}
