package tools.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CLIParameterParser {
    public static CLIParameters parseUnixStyle(CLIParameterRule rule, String... args) {
        return parse("-", rule, args);
    }

    public static CLIParameters parseWindowsStyle(CLIParameterRule rule, String... args) {
        return parse("/", rule, args);
    }

    static CLIParameters parse(String optionPrefix, CLIParameterRule rule, String... args) {
        Map<String, String> options = new HashMap<>();
        List<String> operands = new ArrayList<>();

        // parse options
        for (int i=0; i<args.length; i++) {
            if (args[i].startsWith(optionPrefix)) {

            }
        }

        return new CLIParameters(options, operands);
    }
}
