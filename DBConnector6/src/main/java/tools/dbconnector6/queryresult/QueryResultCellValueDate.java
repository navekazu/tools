package tools.dbconnector6.queryresult;

import javafx.geometry.Pos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * クエリ実行結果一覧に使用するセル情報（日付型）
 */
public class QueryResultCellValueDate extends QueryResultCellValue<Date> {
    // クエリ実行結果一覧に出力する際のFormat
    private static final Format STANDARD_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    // エビデンスとして出力する際のFormat
    private static final Format EVIDENCE_MODE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    /**
     * セル値の初期化。<br>
     * ResultSet#getDateで値を取得する。
     * @param resultSet 値を取得する際のResultSet
     * @param column 値を取得する際のカラムインデックス
     * @throws SQLException 値の取得に失敗した場合
     * @see java.sql.ResultSet#getDate
     */
    @Override
    protected void initValue(ResultSet resultSet, int column) throws SQLException {
        value = resultSet.getDate(column);
    }

    /**
     * クエリ実行結果一覧に出力する際のFormatを返す。<br>
     * 形式はSimpleDateFormatを使用して "yyyy/MM/dd HH:mm:ss.SSS" として返す。<br>
     * @return クエリ実行結果一覧に出力する際のFormat
     */
    @Override
    protected Format getStandardFormat() {
        return STANDARD_FORMAT;
    }

    /**
     * エビデンスとして出力する際のFormatを返す。<br>
     * 形式はSimpleDateFormatを使用して "yyyy/MM/dd HH:mm:ss.SSS" として返す。<br>
     * @return エビデンスとして出力する際のFormat
     */
    @Override
    protected Format getEvidenceModeFormat() {
        return EVIDENCE_MODE_FORMAT;
    }

    /**
     * 値がある場合（nullではない場合）のクエリ実行結果一覧に出力する際のアライメントを返す。<br>
     * 左寄せを返す。<br>
     * @return 左寄せ
     */
    @Override
    protected Pos getAlignment() {
        return Pos.CENTER_LEFT;
    }
}
