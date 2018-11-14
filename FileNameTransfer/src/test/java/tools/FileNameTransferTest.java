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

    	FileNameTransfer.main(new String[]{startDir.toString(), testTargetDir.toString(), endDir.toString()});
    }

}
