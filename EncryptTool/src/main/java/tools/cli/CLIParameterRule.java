package tools.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CLIParameterRule {
    public final List<String> optionOnlyParameters;
    public final List<String> needValueParameters;

    private CLIParameterRule() {
        optionOnlyParameters = null;
        needValueParameters = null;
    }

    public CLIParameterRule(List<String> optionOnlyParameters, List<String> needValueParameters) {
        this.optionOnlyParameters = optionOnlyParameters;
        this.needValueParameters = needValueParameters;
    }

    public static CLIParameterRuleBuilder builder() {
        return new CLIParameterRuleBuilder();
    }

    public static class CLIParameterRuleBuilder {
        private List<String> optionOnlyParameters = null;
        private List<String> needValueParameters = null;

        public CLIParameterRuleBuilder optionOnlyParameters(String... optionOnlyParameters) {
            this.optionOnlyParameters = Arrays.asList(optionOnlyParameters);
            return this;
        }

        public CLIParameterRuleBuilder needValueParameters(String... needValueParameters) {
            this.needValueParameters = Arrays.asList(needValueParameters);
            return this;
        }

        public CLIParameterRule build() {
            Optional<List<String>> optionOnlyParametersOptional = Optional.ofNullable(this.optionOnlyParameters);
            Optional<List<String>> needValueParametersOptional = Optional.ofNullable(this.needValueParameters);

            return new CLIParameterRule(optionOnlyParametersOptional.orElse(new ArrayList<>()), needValueParametersOptional.orElse(new ArrayList<>()));
        }
    }
}
