package tools.dbconnector6.mapper;

import tools.dbconnector6.entity.Connect;
import tools.dbconnector6.entity.ConnectHistory;

public class ConnectHistoryMapper extends MapperBase<ConnectHistory> {
    @Override
    protected String getArchiveFileName() {
        return "connection_history";
    }

    @Override
    protected String getArchiveFileSuffix() {
        return "";
    }

    @Override
    protected ConnectHistory unboxing(String line) {
        String[] data = line.split("\t");
        return ConnectHistory.builder()
                .connectedDate(stringToDate(data[0].trim()))
                .libraryPath(data[1].trim())
                .driver(data[2].trim())
                .url(data[3].trim())
                .user(data[4].trim())
                .password(data[5].trim())
                .build();
    }

    @Override
    protected String autoboxing(ConnectHistory connect) {
        return String.format("%s\t%s\t%s\t%s\t%s\t%s"
                , blankToOneSpace(dateToString(connect.getConnectedDate()))
                , blankToOneSpace(connect.getLibraryPath())
                , blankToOneSpace(connect.getDriver())
                , blankToOneSpace(connect.getUrl())
                , blankToOneSpace(connect.getUser())
                , blankToOneSpace(connect.getPassword())
        );
    }
}
