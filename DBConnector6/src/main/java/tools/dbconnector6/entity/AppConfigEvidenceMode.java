package tools.dbconnector6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class AppConfigEvidenceMode extends AppConfig {
    private boolean evidenceMode;
    private boolean includeHeader;
    private int evidenceDelimiter;

    public static String getLabel() {
        return "EvidenceMode";
    }
}
