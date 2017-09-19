package tools.filerenametool;

import java.util.Arrays;

public class FileRenameSuite {
    private FileRenamerInterface[] fileRenamerInterfaces = new FileRenamerInterface[]{

    };

    public FileRenameSuite() {
    }

    public void execute() {
        Arrays.asList(fileRenamerInterfaces)
                .forEach(f -> f.toString());
    }

    public static void main(String[] args) {
        FileRenameSuite suite = new FileRenameSuite();
        suite.execute();
    }
}
