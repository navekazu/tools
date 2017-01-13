package tools.cli;

import java.util.*;

public class CLIParameterParser {
    public static CLIParameters parseUnixStyle(CLIParameterRule rule, String... args) {
        return parse(Arrays.asList("--", "-"), rule, args);
    }

    public static CLIParameters parseWindowsStyle(CLIParameterRule rule, String... args) {
        return parse(Arrays.asList("/"), rule, args);
    }

    static CLIParameters parse(List<String> optionPrefixes, CLIParameterRule rule, String... args) {
        Map<String, String> options = new HashMap<>();
        List<String> operands = new ArrayList<>();

        // parse options
        for (int i=0; i<args.length; i++) {
//            if (args[i].startsWith(optionPrefixes)) {

//            }
        }

        return new CLIParameters(options, operands);
    }
}
