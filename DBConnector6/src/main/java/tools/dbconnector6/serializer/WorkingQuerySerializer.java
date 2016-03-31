package tools.dbconnector6.serializer;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkingQuerySerializer extends DataSerializer {
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMM");

    @Override
    protected String getArchiveFileName() {
        return "working_history_"+DATE_FORMAT.format(new Date());
    }

    @Override
    protected String getArchiveFileSuffix() {
        return ".sql";
    }

    @Override
    protected Path getArchiveFilePath() throws IOException {
        return getArchiveFilePath("working_history");
    }
}
