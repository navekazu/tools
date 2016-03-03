package tools.dbconnector6.entity;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Map;

public class QueryResult {
    private ObjectProperty<Map<String, String>> record = new SimpleObjectProperty<Map<String, String>>();

    public void setData(Map<String, String> data) {
        record.set(data);
    }
    public String getData(String key) {
        return record.get().get(key);
    }
}
