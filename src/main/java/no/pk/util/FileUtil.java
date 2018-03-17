package no.pk.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    private static FileUtil instance;

    public static FileUtil getInstance() {
        if (instance == null)
            instance = new FileUtil();
        return instance;
    }

    /**
     * Henter ut alle filene i en mappe paa WPS
     *
     * @param path Stien til mappen
     * @return Byte array med informasjon om filene i mappen
     */
    public byte[] getFilesInDirectory(String path) {
        StringBuilder sb = new StringBuilder();

        try {
            Files.walk(Paths.get(path))
                    .filter(Files::isRegularFile)
                    .forEach(file -> sb.append(file.toString()).append(", \n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString().getBytes();
    }

    /**
     * Henter informasjon om en fil paa WPS
     *
     * @param path Stien til filen
     * @return Byte array med informasjon om filen
     */
    public byte[] getFile(String path) {
        StringBuilder sb = new StringBuilder();
        Path file = Paths.get(path);
        sb.append(file.getFileName());
        return sb.toString().getBytes();
    }
}
