package tools.dbconnector6.serializer;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ApplicationLogSerializer extends DataSerializer {
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMM");

    @Override
    protected String getArchiveFileName() {
        return "application_"+DATE_FORMAT.format(new Date())+".log";
    }

    @Override
    protected Path getArchiveFilePath() {
        return getArchiveFilePath("log");
    }
}
