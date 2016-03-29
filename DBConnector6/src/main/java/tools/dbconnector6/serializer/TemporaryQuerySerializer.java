package tools.dbconnector6.serializer;

import java.io.IOException;
import java.nio.file.Path;

public class TemporaryQuerySerializer extends DataSerializer {
    @Override
    protected String getArchiveFileName() {
        return "TemporaryQuery_";
    }

    @Override
    protected String getArchiveFileSuffix() {
        return ".sql";
    }

    @Override
    protected Path getArchiveFilePath() throws IOException {
        return getTempFilePath("temp");
    }
}
