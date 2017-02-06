package tools.filerenametool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileRenameToolUtil {
    public static void createToolDirectory() throws IOException {
        Path toolDirectory = Paths.get(System.getProperty("user.home"), ".FileRenameTool");
        Files.createDirectories(toolDirectory);
    }
}
