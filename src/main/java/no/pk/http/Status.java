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

    public String getStatusCode() {
        return statusCode;
    }

    public String getPhrase() {
        return phrase;
    }

    @Override
    public String toString() {
        return version + " " + statusCode + " " + phrase;
    }
}