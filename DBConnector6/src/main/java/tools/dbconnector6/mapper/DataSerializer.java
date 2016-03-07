package tools.dbconnector6.mapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

public abstract class DataSerializer {
    protected abstract String getArchiveFileName();
    protected abstract Path getArchiveFilePath();
    protected static String testFileName = "";

    protected Path getArchiveFilePath(String kind) {
        return Paths.get(System.getProperty("user.home"), ".DBConnector6", kind, getArchiveFileName() + testFileName);
    }

    public void appendText(String text) throws IOException {
        Path path = getArchiveFilePath();
        Files.createDirectories(path.getParent());

        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path, StandardOpenOption.APPEND))) {
            for (int loop = 0; loop < text.length(); loop++) {
                if (text.charAt(loop) == '\n') {
                    out.println();
                } else {
                    out.print(text.charAt(loop));
                }
            }
            out.println();
        }
    }

    public String readText() throws IOException {
        Path path = getArchiveFilePath();
        Files.createDirectories(path.getParent());
        StringBuilder stringBuilder = new StringBuilder();

        Files.readAllLines(path).stream()
                .forEach(s -> {
                    stringBuilder.append(s);
                    stringBuilder.append("\n");
                });

        return stringBuilder.toString();
    }
}
