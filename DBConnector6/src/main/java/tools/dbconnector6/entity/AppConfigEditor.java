package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class AppConfigEditor extends AppConfig {
    private String editorPath;

    public static String getLabel() {
        return "Editor";
    }
}
