package tools.dbconnector6.mapper;

import tools.dbconnector6.entity.Connect;

public class ConnectMapper extends MapperBase<Connect> {
    @Override
    protected String getArchiveFileName() {
        return "connection";
    }

    @Override
    protected String getArchiveFileSuffix() {
        return "";
    }

    @Override
    protected Connect unboxing(String line) {
        String[] data = line.split("\t");
        return Connect.builder()
                .libraryPath(data[0].trim())
                .driver(data[1].trim())
                .url(data[2].trim())
                .user(data[3].trim())
                .password(data[4].trim())
                .build();
    }

    @Override
    protected String autoboxing(Connect connect) {
        return String.format("%s\t%s\t%s\t%s\t%s"
                , blankToOneSpace(connect.getLibraryPath())
                , blankToOneSpace(connect.getDriver())
                , blankToOneSpace(connect.getUrl())
                , blankToOneSpace(connect.getUser())
                , blankToOneSpace(connect.getPassword())
        );
    }
}
