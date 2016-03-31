package tools.dbconnector6.serializer;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QueryHistorySerializer extends DataSerializer {
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    @Override
    protected String getArchiveFileName() {
        return "query_history_"+DATE_FORMAT.format(new Date());
    }

    @Override
    protected String getArchiveFileSuffix() {
        return ".log";
    }

    @Override
    protected Path getArchiveFilePath() throws IOException {
        return getArchiveFilePath("query_history");
    }
}
