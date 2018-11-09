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
    	Files.createDirectories(startDir);

    	FileNameTransfer.main(new String[]{testTargetDir.toString(), startDir.toString()});
    }

}
