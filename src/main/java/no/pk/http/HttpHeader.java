package no.pk.http;

import java.util.HashMap;

public class HttpHeader {
    private Status status;
    private HashMap<String, String> headers;

    public HttpHeader(Status st){
        status = st;
    }

    public HttpHeader(){
        this(null,null);
    }

    public HttpHeader(Status status, HashMap<String, String> headers) {
        this.status = status;
        this.headers = headers;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {

        status.toString();
        headers.forEach((k, v) -> {
            System.out.println(k + " " + v);
        });
        return null;
    }
}
