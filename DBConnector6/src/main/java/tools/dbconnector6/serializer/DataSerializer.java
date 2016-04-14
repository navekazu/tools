package tools.dbconnector6.serializer;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;

public abstract class DataSerializer {
    protected abstract String getArchiveFileName();
    protected abstract String getArchiveFileSuffix();
    protected abstract Path getArchiveFilePath() throws IOException ;

    private static boolean utMode = false;
    public static void setUtMode(boolean utMode) {
        DataSerializer.utMode = utMode;
    }

    protected Path getArchiveFilePath(String kind) {
        return Paths.get(System.getProperty("user.home"), ".DBConnector6", kind, getArchiveFileName() + getArchiveFileSuffix() + (utMode? "_test": ""));
    }
    protected Path getTempFilePath(String kind) throws IOException {
        Path parent = Paths.get(System.getProperty("user.home"), ".DBConnector6", kind);
        Files.createDirectories(parent);
        return Files.createTempFile(parent, getArchiveFileName(), getArchiveFileSuffix() + (utMode? "_test": ""));
    }

    public Path appendText(String text) throws IOException {
        return writeText(getArchiveFilePath(), text, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    public Path updateText(String text) throws IOException {
        return writeText(getArchiveFilePath(), text, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    public Path createTempolaryFile(String text) throws IOException {
        Path path = writeText(getArchiveFilePath(), text, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        path.toFile().deleteOnExit();   // VM終了時に削除するようマークする
        return path;
    }
    private Path writeText(Path path, String text, OpenOption... options) throws IOException {
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

        return path;
    }

    public String readText() throws IOException {
        return readText(getArchiveFilePath());
    }
    public String readText(Path path) throws IOException {
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
