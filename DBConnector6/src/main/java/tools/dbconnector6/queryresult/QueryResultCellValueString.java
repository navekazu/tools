package tools.dbconnector6.queryresult;

import javafx.geometry.Pos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;

/**
 * クエリ実行結果一覧に使用するセル情報（文字列型）
 */
public class QueryResultCellValueString extends QueryResultCellValue<String> {

    /**
     * セル値の初期化。<br>
     * ResultSet#getStringで値を取得する。
     * @param resultSet 値を取得する際のResultSet
     * @param column 値を取得する際のカラムインデックス
     * @throws SQLException 値の取得に失敗した場合
     * @see java.sql.ResultSet#getString
     */
    @Override
    protected void initValue(ResultSet resultSet, int column) throws SQLException {
        value = resultSet.getString(column);
    }

    /**
     * クエリ実行結果一覧に出力する際のFormatを返す。<br>
     * 形式を使用しないので null を返す。<br>
     * @return null
     */
    @Override
    protected Format getStandardFormat() {
        return null;
    }

    /**
     * エビデンスとして出力する際のFormatを返す。<br>
     * 形式を使用しないので null を返す。<br>
     * @return null
     */
    @Override
    protected Format getEvidenceModeFormat() {
        return null;
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
