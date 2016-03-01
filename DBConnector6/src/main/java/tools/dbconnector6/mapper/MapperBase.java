package tools.dbconnector6.mapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MapperBase<T> {
    protected abstract String getArchiveFileName();
    protected abstract T unboxing(String line);
    protected abstract String autoboxing(T t);
    protected static String testFileName = "";

    protected String blankToOneSpace(String s) {
        if (s==null || "".equals(s.trim())) {
            return " ";
        }
        return s;
    }

    public List<T> selectAll() throws IOException {
        Path path = getArchiveFilePath();

        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        return Files.readAllLines(path).stream().map(s -> unboxing(s)).collect(Collectors.toList());
    }

    public void save(List<T> list) throws IOException {
        Path path = getArchiveFilePath();
        List<String> stringList = list.stream().map(t -> autoboxing(t)).collect(Collectors.toList());
        Files.createDirectories(path.getParent());
        Files.write(path, stringList);
    }

    public void clear() throws IOException {
        Path path = getArchiveFilePath();
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    protected Path getArchiveFilePath() {
        return Paths.get(System.getProperty("user.home"), ".DBConnector6", "config", getArchiveFileName()+testFileName);
    }
}
