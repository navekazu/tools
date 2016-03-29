package tools.dbconnector6.serializer;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ApplicationLogSerializer extends DataSerializer {
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMM");

    @Override
    protected String getArchiveFileName() {
        return "application_"+DATE_FORMAT.format(new Date());
    }

    @Override
    protected String getArchiveFileSuffix() {
        return ".log";
    }

    @Override
    protected Path getArchiveFilePath() throws IOException {
        return getArchiveFilePath("log");
    }
}
