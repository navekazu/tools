package tools.filerenametool;

import java.io.File;

public interface FileRenamerInterface {
    public String getName();
    public File execute(File file);
}
