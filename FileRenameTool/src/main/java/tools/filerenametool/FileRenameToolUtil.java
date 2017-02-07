package tools.filerenametool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileRenameToolUtil {
    public static Path getToolDirectory() throws IOException {
        return Paths.get(System.getProperty("user.home"), ".FileRenameTool");
    }
    public static void createToolDirectory() throws IOException {
        Files.createDirectories(getToolDirectory());
    }
}
