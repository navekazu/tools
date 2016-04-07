package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Connect {
    private String libraryPath;
    private String driver;
    private String url;
    private String user;
    private String password;

    public String getPasswordSec() {
        if (password==null || "".equals(password)) {
            return "";
        }
        return "**********";
    }
}
