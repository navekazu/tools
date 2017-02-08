package tools.filldisk;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class FillTask {
    static final int WRITE_SIZE = 1024*1024;
    static final long WRITE_FILE_SIZE = 1024L*1024L*1024L*10L;
    static final int WRITE_LOOP = (int)(WRITE_FILE_SIZE/(long)WRITE_SIZE);

    public abstract FillPattern getCurrentPattern();

    public abstract byte[] getWriteData();


    public void eraseDisk(String path) throws IOException {
        Files.list(Paths.get(path))
                .filter(p -> p.getFileName().toString().endsWith("fillDiskData"))
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

    }

    public void fillDisk(String path, int repeat) {
        int loop = 0;

        try {
            while (true) {
                Path filePath = Paths.get(path, String.format("FillDisk_%06d.fillDiskData", loop));
                Date date = new Date();
                System.out.println(String.format("%tF %tT, repeat:%3d, pattern:%s, file:%s", date, date, repeat, filePath.getFileName().toString(), getCurrentPattern().name()));
                try (BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(filePath))) {
                    for (int i=0; i<WRITE_LOOP; i++) {
                        byte[] data = getWriteData();
                        out.write(data);
                    }
                }
                loop++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void dumpHead(String path) {

    }
}