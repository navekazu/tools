package tools.dbconnector6.mapper;

import tools.dbconnector6.serializer.DataSerializer;

import java.io.IOException;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MapperBase<T> extends DataSerializer {
    protected abstract T unboxing(String line);
    protected abstract String autoboxing(T t);
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

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

        return Files.readAllLines(path).stream()
                .map(s -> unboxing(s))
                .collect(Collectors.toList());
    }

    public void save(List<T> list) throws IOException {
        Path path = getArchiveFilePath();
        List<String> stringList = list.stream()
                .map(t -> autoboxing(t))
                .collect(Collectors.toList());
        Files.createDirectories(path.getParent());
        Files.write(path, stringList);
    }

    public void clear() throws IOException {
        Path path = getArchiveFilePath();
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    protected Path getArchiveFilePath() throws IOException {
        return getArchiveFilePath("config");
    }

    protected String dateToString(Date date) {
        if (date==null) {
            return null;
        }
        return DATE_FORMAT.format(date);
    }

    protected Date stringToDate(String value) {
        try {
            return DATE_FORMAT.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}