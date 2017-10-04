package tools.filerenametool;

import java.io.File;
import java.util.Arrays;

public class FileRenameSuite {
    private FileRenamerInterface[] fileRenamerInterfaces = new FileRenamerInterface[]{

    };

    public FileRenameSuite() {
    }

    public void execute(String[] args) {
        Arrays.asList(args).stream()
                .map(arg -> new File(arg))
                .forEach(arg -> {
                    Arrays.asList(fileRenamerInterfaces).stream()
                            // いまは全部実行
//                .filter(f -> Arrays.asList(args).stream().anyMatch(arg -> arg.equals(f.getName())))
                            .forEach(f -> f.execute(arg));
                });
    }

    public static void main(String[] args) {
        FileRenameSuite suite = new FileRenameSuite();
        suite.execute(args);
    }
}
