package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

import java.text.SimpleDateFormat;
import java.util.Date;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ConnectHistory {
    private Date connectedDate;
    private String libraryPath;
    private String driver;
    private String url;
    private String user;
    private String password;

    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    public String toString() {
        return String.format("%s Lib[%s] Dir[%s] URL[%s] Use[%s]"
                , (connectedDate==null? "": DATE_FORMAT.format(connectedDate))
                , (libraryPath==null? "": libraryPath)
                , (driver==null? "": driver)
                , (url==null? "": url)
                , (user==null? "": user)
        );
    }
}
