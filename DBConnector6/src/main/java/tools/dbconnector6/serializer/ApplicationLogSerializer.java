package tools.dbconnector6.serializer;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * アプリケーションログ用のシリアライザ。
 */
public class ApplicationLogSerializer extends DataSerializer {

    /**
     * ファイル名に使用する日付文字列フォーマット。<br>
     * 月ごとにローテーションする。
     */
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMM");

    /**
     * 永続化時の（拡張子を除く）ファイル名を返す。<br>
     * アプリケーションログは「application_年月」を返す。<br>
     * 年月には現在年月が入る。<br>
     * @return 永続化時のファイル名
     */
    @Override
    protected String getArchiveFileName() {
        return "application_"+DATE_FORMAT.format(new Date());
    }

    /**
     * 永続化時のファイルの拡張子を返す。
     * アプリケーションログは「.log」を返す。<br>
     * @return 永続化時のファイルの拡張子
     */
    @Override
    protected String getArchiveFileSuffix() {
        return ".log";
    }

    /**
     * 永続化する際のファイル名を含むパスを取得する
     * @return 永続化パス
     */
    @Override
    protected Path getArchiveFilePath() throws IOException {
        return getArchiveFilePath("log");
    }
}
