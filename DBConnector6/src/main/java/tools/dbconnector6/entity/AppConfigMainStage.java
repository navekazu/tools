package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class AppConfigMainStage extends AppConfig {
    private boolean maximized;
    private int x;
    private int y;
    private int width;
    private int height;
    private double primaryDividerPosition;
    private double leftDividerPosition;
    private double rightDivider1Position;
    private double rightDivider2Position;

    public static String getLabel() {
        return "MainStage";
    }
}
