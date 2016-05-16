package tools.dbconnector6.queryresult;

import javafx.geometry.Pos;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.Format;

/**
 * クエリ実行結果一覧に使用するセル情報（整数型）
 */
public class QueryResultCellValueNumber extends QueryResultCellValue<BigDecimal> {
    // クエリ実行結果一覧に出力する際のFormat
    private static final Format STANDARD_FORMAT = new DecimalFormat("#,##0");

    // エビデンスとして出力する際のFormat
    private static final Format EVIDENCE_MODE_FORMAT = new DecimalFormat("0");

    /**
     * セル値の初期化。<br>
     * ResultSet#getBigDecimalで値を取得する。
     * @param resultSet 値を取得する際のResultSet
     * @param column 値を取得する際のカラムインデックス
     * @throws SQLException 値の取得に失敗した場合
     * @see java.sql.ResultSet#getBigDecimal
     */
    @Override
    protected void initValue(ResultSet resultSet, int column) throws SQLException {
        value = resultSet.getBigDecimal(column);
    }

    /**
     * クエリ実行結果一覧に出力する際のFormatを返す。<br>
     * 形式はDecimalFormatを使用して "#,##0" として返す。<br>
     * @return クエリ実行結果一覧に出力する際のFormat
     */
    @Override
    protected Format getStandardFormat() {
        return STANDARD_FORMAT;
    }

    /**
     * エビデンスとして出力する際のFormatを返す。<br>
     * 形式はDecimalFormatを使用して "0" として返す。<br>
     * @return エビデンスとして出力する際のFormat
     */
    @Override
    protected Format getEvidenceModeFormat() {
        return EVIDENCE_MODE_FORMAT;
    }

    /**
     * 値がある場合（nullではない場合）のクエリ実行結果一覧に出力する際のアライメントを返す。<br>
     * 右寄せを返す。<br>
     * @return 右寄せ
     */
    @Override
    protected Pos getAlignment() {
        return Pos.CENTER_RIGHT;
    }
}
