package tools.dbconnector6.queryresult;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;

/**
 * クエリ実行結果一覧に使用する列情報
 */
public class QueryResult {
    // 列の情報
    private ObjectProperty<List<QueryResultCellValue>> record = new SimpleObjectProperty<>();

    /**
     * 列情報を設定する
     * @param data 列情報
     */
    public void setRecordData(List<QueryResultCellValue> data) {
        record.set(data);
    }

    /**
     * 列情報を取得する
     * @return 列情報
     */
    public List<QueryResultCellValue> getRecordData() {
        return record.get();
    }

    /**
     * 列情報の指定されたセル情報を取得する
     * @param index セルのインデックス
     * @return セル情報
     */
    public QueryResultCellValue getData(int index) {
        return record.get().get(index);
    }
}
