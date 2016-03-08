package tools.dbconnector6.serializer;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;

public abstract class DataSerializer {
    protected abstract String getArchiveFileName();
    protected abstract Path getArchiveFilePath();

    private static boolean utMode = false;
    public static void setUtMode(boolean utMode) {
        DataSerializer.utMode = utMode;
    }

    protected Path getArchiveFilePath(String kind) {
        return Paths.get(System.getProperty("user.home"), ".DBConnector6", kind, getArchiveFileName() + (utMode? "_test": ""));
    }

    public void appendText(String text) throws IOException {
        writeText(text, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    public void updateText(String text) throws IOException {
        writeText(text, StandardOpenOption.WRITE);
    }
    private void writeText(String text, OpenOption... options) throws IOException {
        Path path = getArchiveFilePath();
        Files.createDirectories(path.getParent());

        try {
            try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path, options))) {
                for (int loop = 0; loop < text.length(); loop++) {
                    if (text.charAt(loop) == '\n') {
                        out.println();
                    } else {
                        out.print(text.charAt(loop));
                    }
                }
                out.println();
            }
        } catch(IOException e) {
            e.printStackTrace();
            throw e;
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
