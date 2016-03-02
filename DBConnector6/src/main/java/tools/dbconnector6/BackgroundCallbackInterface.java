package tools.dbconnector6;

import java.sql.SQLException;

public interface BackgroundCallbackInterface {
    public void run() throws SQLException;
}
