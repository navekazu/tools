package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class TableColumnTab {
    private String name;
    private String type;
    private Integer size;
    private Integer decimalDigits;
    private String nullable;
    private Integer primaryKey;
    private String remarks;
    private String columnDefault;
    private String autoincrement;
    private String generatedColumn;
}
