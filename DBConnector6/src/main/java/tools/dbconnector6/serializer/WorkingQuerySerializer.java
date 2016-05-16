package tools.dbconnector6.serializer;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SQL編集領域ログのシリアライザ。<br>
 * 当該ツールを終了する時にこのシリアライザでSQL編集領域の内容を保存し、再度起動する際にこのシリアライザで内容を復元する。<br>
 * 永遠と貯め続けるのを避けるため、ファイルは月ごとにローテーションする。
 */
public class WorkingQuerySerializer extends DataSerializer {

    /**
     * ファイル名に使用する日付文字列フォーマット。<br>
     * 月ごとにローテーションする。
     */
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMM");

    /**
     * 永続化時の（拡張子を除く）ファイル名を返す。<br>
     * SQL編集領域ログは「working_history_年月」を返す。<br>
     * 年月には現在年月が入る。<br>
     * @return 永続化時のファイル名
     */
    @Override
    protected String getArchiveFileName() {
        return "working_history_"+DATE_FORMAT.format(new Date());
    }

    /**
     * 永続化時のファイルの拡張子を返す。<br>
     * SQL編集領域ログは「.log」を返す。<br>
     * @return 永続化時のファイルの拡張子
     */
    @Override
    protected String getArchiveFileSuffix() {
        return ".sql";
    }

    /**
     * 永続化する際のファイル名を含むパスを取得する<br>
     * @return 永続化パス
     */
    @Override
    protected Path getArchiveFilePath() throws IOException {
        return getArchiveFilePath("working_history");
    }
}
