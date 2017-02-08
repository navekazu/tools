package tools.filldisk;

import static tools.filldisk.FillPattern.*;
import static tools.filldisk.FillPattern.BLANK;

public class FillTaskFactory {
    public static FillTask createFillTask(FillPattern pattern) {
        switch (pattern) {
            case BLANK:  // 0x00
                return new FillTaskBlank();
            case FILL:   // 0xFF
                return new FillTaskFill();
            default:
                return new FillTaskRandom();
        }
    }
}
