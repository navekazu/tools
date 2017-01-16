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
        String option = null;
        boolean needValueFlag = false;

        // parse options
        for (int i=0; i<args.length; i++) {
            final String arg = args[i];

            if (needValueFlag) {
                options.put(option, arg);
                option = null;
                needValueFlag = false;

            } else {
                Optional<String> optionPrefix = optionPrefixes.stream()
                        .filter(s -> arg.startsWith(s))
                        .findFirst();
                if (optionPrefix.isPresent()) {
                    // オプションが指定されている
                    final String optionParameter = arg.substring(optionPrefix.get().length());

                    // 値が必要？
                    if (rule.needValueParameters.stream().anyMatch(s -> s.equals(optionParameter))) {
                        option = arg.substring(optionPrefix.get().length());
                        needValueFlag = true;
                    } else {
                        // 値は不要？（その場合はoptionOnlyParametersの部分一致でOK）
                        rule.optionOnlyParameters.stream()
                                .filter(s -> optionParameter.indexOf(s)!=-1)
                                .forEach(s -> options.put(s, null));
                    }
                } else {
                    operands.add(arg);
                }
            }
        }

        return new CLIParameters(options, operands);
    }
}
