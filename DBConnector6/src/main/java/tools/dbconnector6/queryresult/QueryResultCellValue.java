package tools.dbconnector6.queryresult;

import javafx.geometry.Pos;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.Format;

/**
 * クエリ実行結果一覧に使用するセル情報の基底クラス
 */
public abstract class QueryResultCellValue<V> {

    /**
     * セルの値<br>
     */
    protected V value;

    /**
     * 値がnullの場合に一覧へ表示する値「(null)」<br>
     */
    protected static final String NULL_PROMPT = "(null)";

    /**
     * セル値の初期化。<br>
     * 与えられたResultSetから所定のカラムインデックスの値を取得する際の方法は、各具象クラスで決定する。
     * @param resultSet 値を取得する際のResultSet
     * @param column 値を取得する際のカラムインデックス
     * @throws SQLException 値の取得に失敗した場合
     */
    protected abstract void initValue(ResultSet resultSet, int column) throws SQLException;

    /**
     * クエリ実行結果一覧に出力する際のFormatを返す。<br>
     * （数値の場合カンマ編集をする、等）
     * @return クエリ実行結果一覧に出力する際のFormat
     */
    protected abstract Format getStandardFormat();

    /**
     * エビデンスとして出力する際のFormatを返す。<br>
     * （数値の場合でもカンマ編集しない、等）
     * @return エビデンスとして出力する際のFormat
     */
    protected abstract Format getEvidenceModeFormat();

    /**
     * 値がある場合（nullではない場合）のクエリ実行結果一覧に出力する際のアライメントを返す。<br>
     * （数値の場合は右寄せ、それ以外は左寄せ）
     * @return クエリ実行結果一覧に出力する際のアライメント
     */
    protected abstract Pos getAlignment();

    /**
     * 値の文字列表現を返す。<br>
     * クエリ実行結果一覧に出力する際のFormatを使用する。<br>
     * @return 値の文字列表現
     */
    @Override
    public String toString() {
        if (value==null) {
            return NULL_PROMPT;
        }
        return getFormattedString();
    }

    /**
     * 値がnullの場合は true を、それ以外は false を返す。<br>
     * @return 値がnullの場合は true を、それ以外は false
     */
    public boolean isNullValue() {
        return value==null;
    }

    /**
     * クエリ実行結果一覧に出力する際のFormatを使用して値の文字列表現を作成する。<br>
     * 値がnullの場合はNULL_PROMPTを返す。<br>
     * @return 値の文字列表現
     */
    public String getFormattedString() {
        return value==null? NULL_PROMPT: getValueString(getStandardFormat());
    }

    /**
     * エビデンスとして値の文字列表現を作成する。<br>
     * 値がnullの場合は空文字を返す。<br>
     * @return エビデンスとしての値の文字列表現
     */
    public String getEvidenceModeString() {
        return getValueString(getEvidenceModeFormat());
    }

    /**
     * 指定されたFormatを使用して値の文字列表現を作成する。<br>
     * 値がnullの場合は空文字を返す。<br>
     * @param f 文字列表現を作成する際のFormat
     * @return 値の文字列表現
     */
    protected String getValueString(Format f) {
        return value==null? "": (f==null? value.toString(): f.format(value));
    }

    /**
     * 指定されたResultSetMetaDataのカラムインデックスのアライメントを返す。<br>
     * @param meta クエリ実行結果のResultSetMetaData
     * @param column カラムインデックス
     * @return アライメント
     * @throws SQLException ResultSetMetaDataに対する操作に失敗した場合
     */
    public static Pos getAlignment(ResultSetMetaData meta, int column) throws SQLException {
        QueryResultCellValue queryResultCellValue = QueryResultCellValueCreator.createQueryResultCellValue(meta, column);
        return queryResultCellValue.getAlignment();
    }

    /**
     * 指定されたResultSetMetaDataのカラムインデックスからQueryResultCellValueを作成し、値を初期化したインスタンスを返す。<br>
     * @param meta クエリ実行結果のResultSetMetaData
     * @param resultSet クエリ実行結果
     * @param column カラムインデックス
     * @return 作成し値の初期化をしたQueryResultCellValue
     * @throws SQLException ResultSetMetaDataとResultSetに対する操作に失敗した場合
     */
    public static QueryResultCellValue createQueryResultCellValue(ResultSetMetaData meta, ResultSet resultSet, int column) throws SQLException {
        QueryResultCellValue queryResultCellValue = QueryResultCellValueCreator.createQueryResultCellValue(meta, column);
        queryResultCellValue.initValue(resultSet, column);
        return queryResultCellValue;
    }
}
