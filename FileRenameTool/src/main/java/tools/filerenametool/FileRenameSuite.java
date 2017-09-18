package tools.filerenametool;

import java.util.Arrays;

public class FileRenameSuite {
    private FileRenamerInterface[] fileRenamerInterfaces = new FileRenamerInterface[]{

    };

    public FileRenameSuite() {
        execute();
    }

    private void execute() {
        Arrays.asList(fileRenamerInterfaces)
                .forEach(f -> f.toString());
    }

    public static void main(String[] args) {

    }
}
