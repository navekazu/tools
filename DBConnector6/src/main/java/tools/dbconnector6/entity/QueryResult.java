package tools.dbconnector6.entity;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Map;

public class QueryResult {
    private ObjectProperty<Map<String, QueryResultCellValue>> record = new SimpleObjectProperty<Map<String, QueryResultCellValue>>();

    public void setData(Map<String, QueryResultCellValue> data) {
        record.set(data);
    }
    public QueryResultCellValue getData(String key) {
        return record.get().get(key);
    }
}
