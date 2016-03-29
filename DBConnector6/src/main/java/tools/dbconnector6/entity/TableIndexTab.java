package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class TableIndexTab {
    private String indexName;
    private boolean primaryKey;
    private boolean uniqueKey;
    private List<String> columnList;

    @Override
    public String toString() {
        return indexName;
    }
}
