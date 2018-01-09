package tools.filerenametool2;

import java.io.File;

public class ToHalfCharacter implements FileRenamerInterface {
    public String getName() {
        return "ToHalfCharacter";
    }

    public File execute(File file) {
        String name = file.getName();

        File newFile = new File(file.getParent(), name);
        file.renameTo(newFile);
    }
}
