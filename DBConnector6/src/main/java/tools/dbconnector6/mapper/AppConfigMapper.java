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
        int index = 0;
        String[] data = line.split("\t");

        AppConfig appConfig = null;

        if (AppConfigMainStage.getLabel().equals(data[index++])) {
            appConfig = AppConfigMainStage.builder()
                    .maximized(Boolean.getBoolean(data[index++]))
                    .x(Integer.getInteger(data[index++]))
                    .y(Integer.getInteger(data[index++]))
                    .width(Integer.getInteger(data[index++]))
                    .height(Integer.getInteger(data[index++]))
                    .primaryDividerPosition(Double.parseDouble(data[index++]))
                    .leftDividerPosition(Double.parseDouble(data[index++]))
                    .rightDivider1Position(Double.parseDouble(data[index++]))
                    .rightDivider2Position(Double.parseDouble(data[index++]))
                    .build();
        } else
        if (AppConfigEvidenceMode.getLabel().equals(data[index++])) {
            appConfig = AppConfigEvidenceMode.builder()
                    .evidenceMode(Boolean.getBoolean(data[index++]))
                    .includeHeader(Boolean.getBoolean(data[index++]))
                    .evidenceDelimiter(Integer.getInteger(data[index++]))
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
                    , blankToOneSpace(Integer.toString(appConfigMainStage.getX()))
                    , blankToOneSpace(Integer.toString(appConfigMainStage.getY()))
                    , blankToOneSpace(Integer.toString(appConfigMainStage.getWidth()))
                    , blankToOneSpace(Integer.toString(appConfigMainStage.getHeight()))
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
