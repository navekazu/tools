package tools.dbconnector6.serializer;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;

public abstract class DataSerializer {
    protected abstract String getArchiveFileName();
    protected abstract String getArchiveFileSuffix();
    protected abstract Path getArchiveFilePath();

    private static boolean utMode = false;
    public static void setUtMode(boolean utMode) {
        DataSerializer.utMode = utMode;
    }

    protected Path getArchiveFilePath(String kind) {
        return Paths.get(System.getProperty("user.home"), ".DBConnector6", kind, getArchiveFileName() + getArchiveFileSuffix() + (utMode? "_test": ""));
    }
    protected Path getTempFilePath(String kind) {
        return Files.createTempFile(
                Paths.get(System.getProperty("user.home"), ".DBConnector6", kind),
                getArchiveFileName(), getArchiveFileSuffix() + (utMode? "_test": ""),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    public void appendText(String text) throws IOException {
        writeText(getArchiveFilePath(), text, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    public void updateText(String text) throws IOException {
        writeText(getArchiveFilePath(), text, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }
    public void updateTempText(String text) throws IOException {
        writeText(getArchiveFilePath(), text, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }
    private void writeText(Path path, String text, OpenOption... options) throws IOException {
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

                // 最後の文字が改行でないなら、改行を付加する
                if (!text.endsWith("\n")) {
                    out.println();
                }
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

        if (!Files.exists(path)) {
            return "";
        }

        Files.readAllLines(path).stream()
                .forEach(s -> {
                    stringBuilder.append(s);
                    stringBuilder.append("\n");
                });

        return stringBuilder.toString();
    }
}
