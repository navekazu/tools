package tools.dbconnector6.entity;

/**
 * アプリケーション設定ファイルのentity基底クラス。<br>
 * ファイルは「~/.DBConnector6/config/app_config」として保存する。<br>
 * アプリケーション設定ファイルは行の先頭にラベルがあり、そのラベルによってその行が何の情報を保持するかが決定する。<br>
 */
public abstract class AppConfig {

    /**
     * 何のアプリケーション設定ファイルかを表すラベル。<br>
     * このクラスを拡張する具象クラスが実装する必要がある。<br>
     * @return ラベル文字列
     */
    public static String getLabel() {
        return "";
    }
}
