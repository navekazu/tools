package tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import tools.filenametransfer.FileNameTransfer;

public class FileNameTransferTest {
    @Test
    public void launchTest() throws IOException {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
    	Path testBaseDir = Paths.get(".\\test");
    	Path testTargetDir = Paths.get(testBaseDir.toString(), sdf.format(new Date()));
    	Path startDir = Paths.get(testTargetDir.toString(), "00_元");
    	Path endDir = Paths.get(testTargetDir.toString(), "99_完了");
    	Files.createDirectories(startDir);
    	Files.createDirectories(endDir);

        Files.createFile(Paths.get(startDir.toString(), "xxxxx(20180101-0001).ts"));
        Files.createFile(Paths.get(startDir.toString(), "xxxxx yyyyy(20180101-0002).ts"));
        Files.createFile(Paths.get(startDir.toString(), "xxxxx yyyyy zzzzz(20180101-0003).ts"));
        Files.createFile(Paths.get(startDir.toString(), "ｘｘｘｘｘ　ｙｙｙｙｙ　ｚｚｚｚｚ(20180101-0004).ts"));

    	FileNameTransfer.main(new String[]{startDir.toString(), endDir.toString(), testTargetDir.toString()});
    }

}
