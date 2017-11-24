package tools.filerenametool;

import java.io.File;
import java.util.Arrays;

public class FileRenameSuite {
    private FileRenamerInterface[] fileRenamerInterfaces = new FileRenamerInterface[]{
        new SmallCharacterRenamer(),
    };

    public FileRenameSuite() {
    }

    public void execute(String[] args) {
        Arrays.asList(args).stream()
                .map(arg -> new File(arg))
                .forEach(file -> {




//                    Arrays.asList(fileRenamerInterfaces).stream()
//                            // いまは全部実行
////                .filter(f -> Arrays.asList(args).stream().anyMatch(arg -> arg.equals(f.getName())))
//                            .map(f -> f.execute(arg))
//                            .forEach(f -> System.out.println(f.toString()));
                });
    }

    public static void main(String[] args) {
        FileRenameSuite suite = new FileRenameSuite();
        suite.execute(args);
    }
}
