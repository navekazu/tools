package tools.filerenametool;

import java.util.Arrays;

public class FileRenameSuite {
    private FileRenamerInterface[] fileRenamerInterfaces = new FileRenamerInterface[]{

    };

    public FileRenameSuite() {
    }

    public void execute(String[] args) {
        Arrays.asList(fileRenamerInterfaces).stream()
                .filter(f -> Arrays.asList(args).stream().anyMatch(arg -> arg.equals(f.getName())))
                .forEach(f -> f.toString());
    }

    public static void main(String[] args) {
        FileRenameSuite suite = new FileRenameSuite();
        suite.execute(args);

    }
}
