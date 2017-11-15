package tools.filerenametool.old;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class KeywordFileGenerator {
    public static void main(String[] args) throws IOException {
        if (args.length!=2) {
            System.out.println("usage: java tools.filerenametool.old.KeywordFileGenerator [inFile] [outFile (in app directory)]");
            return ;
        }
        KeywordFileGenerator app = new KeywordFileGenerator();
        app.generate(args[0], args[1]);
    }

    public void generate(String srcPath, String destPath) throws IOException {
        FileRenameToolUtil.createToolDirectory();
        Path targetFile = Paths.get(FileRenameToolUtil.getToolDirectory().toString(), destPath);

        List<String> srcList = Files.readAllLines(Paths.get(srcPath), Charset.forName("MS932"));

        try (PrintWriter out = new PrintWriter(new FileOutputStream(targetFile.toFile()))) {
            srcList.stream()
                    .map(src -> src.trim())                         // 前後のスペースを削除
                    .filter(src -> src.startsWith("src"))           // "src"から始まる行だけを抽出
                    .filter(src -> src.indexOf("=")!=-1)            // "="が含まれていなかったら除外
                    .map(src -> src.substring(src.indexOf("=")+1))  // "="より後ろを抜き出す
                    .forEach(out::println);
        }
    }
}
