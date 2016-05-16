package tools.dbconnector6.serializer;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 一時クエリのシリアライザ。<br>
 * SQLエディタとの間でSQLの編集内容をやり取りするためのファイルを読み書きする。<br>
 * ファイルはVM終了時に削除される。<br>
 */
public class TemporaryQuerySerializer extends DataSerializer {
    /**
     * 永続化時の（拡張子を除く）ファイル名を返す。<br>
     * 一時クエリは「TemporaryQuery_」を返す。<br>
     * @return 永続化時のファイル名
     */
    @Override
    protected String getArchiveFileName() {
        return "TemporaryQuery_";
    }

    /**
     * 永続化時のファイルの拡張子を返す。<br>
     * 一時クエリは「.sql」を返す。<br>
     * @return 永続化時のファイルの拡張子
     */
    @Override
    protected String getArchiveFileSuffix() {
        return ".sql";
    }

    /**
     * 永続化する際のファイル名を含むパスを取得する<br>
     * @return 永続化パス（一時ファイル）
     */
    @Override
    protected Path getArchiveFilePath() throws IOException {
        return getTempFilePath("temp");
    }
}
