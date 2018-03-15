package no.pk.http;

/**
 * Klasse for å representere en http status kode
 */
public class Status {
    private String version;
    private String statusCode;
    private String phrase;

    public Status() {
        this("", "", "");
    }

    public Status(String br) {
        String[] arr = br.split(" ");
        version = arr[0];
        statusCode = arr[1];
        phrase = arr[2];
    }

    public Status(String version, String statusCode, String phrase) {
        this.version = version;
        this.statusCode = statusCode;
        this.phrase = phrase;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    @Override
    public String toString() {
        return version + " " + statusCode + " " + phrase;
    }
}