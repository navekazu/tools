package tools.templategenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratorParam {
    private String currentPath;
    private String projectName;
    private String packageName;
}
