package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ReservedWord {
    public enum ReservedWordType{
        SQL,
        TABLE,
        COLUMN,
    }

    private ReservedWordType type;
    private String word;

    @Override
    public String toString() {
        return word;
    }
}
