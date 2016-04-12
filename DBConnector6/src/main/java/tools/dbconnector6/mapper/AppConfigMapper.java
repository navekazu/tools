package tools.dbconnector6.mapper;

import tools.dbconnector6.entity.AppConfig;
import tools.dbconnector6.entity.AppConfigEditor;
import tools.dbconnector6.entity.AppConfigEvidenceMode;
import tools.dbconnector6.entity.AppConfigMainStage;

/**
 * アプリケーション設定エンティティの永続化を行う。<br>
 * 内容はフラットファイルとして永続化され、「~/.DBConnector6/config/app_config」として保存する。<br>
 * アプリケーション設定ファイルは行の先頭にラベルがあり、そのラベルによってその行が何の情報を保持するかが決定する。<br>
 * <br>
 * ラベルの例：<br>
 * ・Editor -&gt; エディタの設定を保持する<br>
 * ・EvidenceMode -&gt; エビデンス取得モードの設定を保持する<br>
 * ・MainStage -&gt; メインステージの設定を保持する<br>
 */
public class AppConfigMapper extends MapperBase<AppConfig> {
    /**
     * 永続化時の（拡張子を除く）ファイル名を返す。<br>
     * アプリケーション設定は「app_config」を返す。
     * @return 永続化時のファイル名
     */
    @Override
    protected String getArchiveFileName() {
        return "app_config";
    }

    /**
     * 永続化時のファイルの拡張子を返す。
     * アプリケーション設定は拡張子はないので空文字を返す。
     * @return 永続化時のファイルの拡張子
     */
    @Override
    protected String getArchiveFileSuffix() {
        return "";
    }

    /**
     * 永続化結果（テキストファイルの1行）からエンティティを復元する
     * @param line テキストファイルの1行
     * @return 復元したエンティティインスタンス
     */
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
        } else
        if (AppConfigEditor.getLabel().equals(data[0])) {
            int index = 1;
            appConfig = AppConfigEditor.builder()
                    .editorPath(data[index++])
                    .build();
        }

        return appConfig;
    }

    /**
     * エンティティから永続化を行う
     * @param appConfig エンティティインスタンス
     * @return 永続化結果 （テキストファイルの1行）
     */
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
        } else
        if (appConfig instanceof AppConfigEditor) {
            AppConfigEditor appConfigEditor = (AppConfigEditor)appConfig;
            return String.format("%s\t%s"
                    , blankToOneSpace(AppConfigEditor.getLabel())
                    , blankToOneSpace(appConfigEditor.getEditorPath())
            );
        }
        return "";
    }

}
