package tools.dbconnector6.serializer;

import tools.dbconnector6.serializer.DataSerializer;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkingQuerySerializer extends DataSerializer {
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    @Override
    protected String getArchiveFileName() {
        return "query_history_"+DATE_FORMAT.format(new Date());
    }

    @Override
    protected Path getArchiveFilePath() {
        return getArchiveFilePath("working_history");
    }
}
