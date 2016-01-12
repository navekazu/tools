package tools.passwordmanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair {
    private String key;
    private String value;
    private boolean keyEditable;
    private boolean orderChangeable;

    public boolean isBlank() {
        if (key==null && value==null) {
            return true;
        }
        if ("".equals(key) && "".equals(value)) {
            return true;
        }
        return false;
    }
}
