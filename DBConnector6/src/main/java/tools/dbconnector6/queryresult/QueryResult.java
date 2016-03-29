package tools.dbconnector6.queryresult;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;

public class QueryResult {
    private ObjectProperty<List<QueryResultCellValue>> record = new SimpleObjectProperty<>();

    public void setData(List<QueryResultCellValue> data) {
        record.set(data);
    }
    public QueryResultCellValue getData(int index) {
        return record.get().get(index);
    }
}
