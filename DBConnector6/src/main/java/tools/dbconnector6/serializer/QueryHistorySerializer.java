package tools.dbconnector6.serializer;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * クエリ実行履歴のシリアライザ。<br>
 * SQLを実行するたびにそのSQLを実行履歴として保存するためのシリアライザ。<br>
 * 日ごとにローテーションする。<br>
 */
public class QueryHistorySerializer extends DataSerializer {

    /**
     * ファイル名に使用する日付文字列フォーマット。<br>
     * 日ごとにローテーションする。
     */
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    /**
     * 永続化時の（拡張子を除く）ファイル名を返す。<br>
     * クエリ実行履歴は「query_history_年月日」を返す。<br>
     * 年月日には現在年月日が入る。<br>
     * @return 永続化時のファイル名
     */
    @Override
    protected String getArchiveFileName() {
        return "query_history_"+DATE_FORMAT.format(new Date());
    }

    /**
     * 永続化時のファイルの拡張子を返す。<br>
     * クエリ実行履歴は「.sql」を返す。<br>
     * @return 永続化時のファイルの拡張子
     */
    @Override
    protected String getArchiveFileSuffix() {
        return ".log";
    }

    /**
     * 永続化する際のファイル名を含むパスを取得する<br>
     * @return 永続化パス
     */
    @Override
    protected Path getArchiveFilePath() throws IOException {
        return getArchiveFilePath("query_history");
    }
}
