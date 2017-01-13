package tools.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CLIParameters {
    public final Map<String, String> options;
    public final List<String> operands;

    public CLIParameters(Map<String, String> options, List<String> operands) {
        this.options = options;
        this.operands = operands;
    }
}
