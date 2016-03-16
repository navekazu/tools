package tools.dbconnector6.mapper;

import tools.dbconnector6.entity.AppConfig;
import tools.dbconnector6.entity.AppConfigEvidenceMode;
import tools.dbconnector6.entity.AppConfigMainStage;

public class AppConfigMapper extends MapperBase<AppConfig> {
    @Override
    protected String getArchiveFileName() {
        return "app_config";
    }

    @Override
    protected AppConfig unboxing(String line) {
        String[] data = line.split("\t");

        AppConfig appConfig = null;

        if (AppConfigMainStage.getLabel().equals(data[0])) {
            int index = 1;
            appConfig = AppConfigMainStage.builder()
                    .maximized(Boolean.parseBoolean(data[index++]))
                    .x(Double.parseDouble(data[index++]))
                    .y(Double.parseDouble(data[index++]))
                    .width(Double.parseDouble(data[index++]))
                    .height(Double.parseDouble(data[index++]))
                    .primaryDividerPosition(Double.parseDouble(data[index++]))
                    .leftDividerPosition(Double.parseDouble(data[index++]))
                    .rightDivider1Position(Double.parseDouble(data[index++]))
                    .rightDivider2Position(Double.parseDouble(data[index++]))
                    .build();
        } else
        if (AppConfigEvidenceMode.getLabel().equals(data[0])) {
            int index = 1;
            appConfig = AppConfigEvidenceMode.builder()
                    .evidenceMode(Boolean.parseBoolean(data[index++]))
                    .includeHeader(Boolean.parseBoolean(data[index++]))
                    .evidenceDelimiter(Integer.parseInt(data[index++]))
                    .build();
        }

        return appConfig;
    }

    @Override
    protected String autoboxing(AppConfig appConfig) {
        if (appConfig instanceof AppConfigMainStage) {
            AppConfigMainStage appConfigMainStage = (AppConfigMainStage)appConfig;
            return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s"
                    , blankToOneSpace(AppConfigMainStage.getLabel())
                    , blankToOneSpace(Boolean.toString(appConfigMainStage.isMaximized()))
                    , blankToOneSpace(Double.toString(appConfigMainStage.getX()))
                    , blankToOneSpace(Double.toString(appConfigMainStage.getY()))
                    , blankToOneSpace(Double.toString(appConfigMainStage.getWidth()))
                    , blankToOneSpace(Double.toString(appConfigMainStage.getHeight()))
                    , blankToOneSpace(Double.toString(appConfigMainStage.getPrimaryDividerPosition()))
                    , blankToOneSpace(Double.toString(appConfigMainStage.getLeftDividerPosition()))
                    , blankToOneSpace(Double.toString(appConfigMainStage.getRightDivider1Position()))
                    , blankToOneSpace(Double.toString(appConfigMainStage.getRightDivider2Position()))
            );
        } else
        if (appConfig instanceof AppConfigEvidenceMode) {
            AppConfigEvidenceMode appConfigEvidenceMode = (AppConfigEvidenceMode)appConfig;
            return String.format("%s\t%s\t%s\t%s"
                    , blankToOneSpace(AppConfigEvidenceMode.getLabel())
                    , blankToOneSpace(Boolean.toString(appConfigEvidenceMode.isEvidenceMode()))
                    , blankToOneSpace(Boolean.toString(appConfigEvidenceMode.isIncludeHeader()))
                    , blankToOneSpace(Integer.toString(appConfigEvidenceMode.getEvidenceDelimiter()))
            );
        }
        return "";
    }

}
