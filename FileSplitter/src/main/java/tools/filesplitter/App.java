package tools.filesplitter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    private static final long DEFAULT_SPLIT_SIZE = 1024*1024*100;  // 100MB

    static final Map<String, Long> ORDERS_OF_MAGNITUDE = new HashMap<>();
    static {
        ORDERS_OF_MAGNITUDE.put("k", 1024L);                                               // kilo byte
        ORDERS_OF_MAGNITUDE.put("m", 1024L*1024L);                                         // mega byte
        ORDERS_OF_MAGNITUDE.put("g", 1024L*1024L*1024L);                                   // giga byte
        ORDERS_OF_MAGNITUDE.put("t", 1024L*1024L*1024L*1024L);                             // tera byte
        ORDERS_OF_MAGNITUDE.put("p", 1024L*1024L*1024L*1024L*1024L);                       // peta byte
        ORDERS_OF_MAGNITUDE.put("e", 1024L*1024L*1024L*1024L*1024L*1024L);                 // exa byte
//        ORDERS_OF_MAGNITUDE.put("z", 1024L*1024L*1024L*1024L*1024L*1024L*1024L);           // zetta byte
//        ORDERS_OF_MAGNITUDE.put("y", 1024L*1024L*1024L*1024L*1024L*1024L*1024L*1024L);     // yotta byte
    }

    long getOrderSize(String[] args) {
        for (int i=0; i<args.length; i++) {
            if ("-s".equals(args[i]) && i<=args.length) {
                i++;
                String size = args[i].toLowerCase();

                // 正規表現チェック(数値のみ？)
                if (isMatch("^\\d$", size)) {
                    return Long.parseLong(size);
                }

                // 正規表現チェック(単位指定？)
                String patternSize = String.join("|", ORDERS_OF_MAGNITUDE.keySet());
                if (isMatch("^\\d*("+patternSize+")$", size)) {
                    return Long.parseLong(size.substring(0, size.length()-1))*ORDERS_OF_MAGNITUDE.get(size.substring(size.length()-1));
                }

                throw new IllegalArgumentException();
            }
        }

        return DEFAULT_SPLIT_SIZE;
    }
    private boolean isMatch(String pattern, String data) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(data);
        return m.matches();
    }

    List<String> getTargetFileList(String[] args) {
        List<String> list = new ArrayList<>();

        for (int i=0; i<args.length; i++) {
            if ("-s".equals(args[i])) {
                i++;
                continue;
            }
            if ("-bat".equals(args[i]) || "-sh".equals(args[i])) {
                continue;
            }
            list.add(args[i]);
        }

        return list;
    }

    public static void main(String[] args) {
        App app = new App();
        long orderSize = app.getOrderSize(args);
        boolean createBatchFile = app.getCreateBatchFileFlag(args);
        boolean createShellFile = app.getCreateShellFileFlag(args);
        List<String> list = app.getTargetFileList(args);

        list.parallelStream()
                .forEach(f -> app.splitFile(f, orderSize, createBatchFile, createShellFile));
    }

    boolean getCreateBatchFileFlag(String[] args) {
        return Arrays.asList(args).stream()
                .anyMatch(p -> "-bat".equals(p.toLowerCase()));
    }
    boolean getCreateShellFileFlag(String[] args) {
        return Arrays.asList(args).stream()
                .anyMatch(p -> "-sh".equals(p.toLowerCase()));
    }

    void splitFile(String inputFile, long orderSize, boolean createBatchFile, boolean createShellFile) {
        List<Path> splittedFileList = new ArrayList<>();

        try {
            // ファイルのチェック
            Path inputFilePath = Paths.get(inputFile);
            if (!Files.exists(inputFilePath) ||
                    Files.size(inputFilePath) == 0L) {
                System.err.println("Not exist or empty -> "+inputFilePath.toString());
                return ;
            }

            // ファイルの分割
            System.out.println("Start split -> "+inputFilePath.toString());
            long readSize = 0L;
            int outputCount = 1;
            int data;

            try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(inputFilePath))) {
                while (true) {
                    Path outputFilePath = Paths.get(inputFilePath.toString() + String.format("_%06d", outputCount));
                    splittedFileList.add(outputFilePath);
                    outputCount++;
                    try (BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(outputFilePath))) {
                        while ((data = in.read()) != -1) {
                            readSize++;
                            out.write(data);
                            if (readSize % orderSize == 0) {
                                break;
                            }
                        }
                    }
                    if (readSize >= Files.size(inputFilePath)) {
                        break;
                    }
                }
            }

            // ファイル結合バッチ・シェルの作成
            if (createBatchFile) {
                try (PrintWriter out = new PrintWriter(Files.newOutputStream(Paths.get(inputFilePath.toString() + ".bat")))) {
                    out.println("@rem ");
                    out.println("copy /b ^");
                    for (Path splittedFile : splittedFileList) {
                        out.println("    \"" + splittedFile.getFileName() + "\"" + (splittedFile.equals(splittedFileList.get(splittedFileList.size() - 1)) ? "" : " +") + " ^");
                    }
                    out.println("    \"" + inputFilePath.getFileName() + "\"");
                }
            }
            if (createShellFile) {
                try (PrintWriter out = new PrintWriter(Files.newOutputStream(Paths.get(inputFilePath.toString() + ".sh")))) {
                    out.println("# ");
                    out.println("cat \\");
                    for (Path splittedFile : splittedFileList) {
                        out.println("    \"" + splittedFile.getFileName() + "\" \\");
                    }
                    out.println("    > \"" + inputFilePath.getFileName()+"\"");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
