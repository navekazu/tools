package tools.cli;

import org.junit.*;
import tools.encrypttool.App;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CLIParameterParserTest {
    @BeforeClass
    public static void beforeClass() throws Exception {
    }

    @AfterClass
    public static void afterClass() throws Exception {
    }

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void parseUnixStyleTest() {

        String[] args = new String[] {
                "-a", "a-value",
                "-b", "b-value",
                "-c",
                "-de",
                "aaa", "bbb", "ccc"
        };
        CLIParameters cliParameters = CLIParameterParser.parseUnixStyle(
                CLIParameterRule.builder()
                        .needValueParameters(new String[] {"a", "b"})
                        .optionOnlyParameters(new String[] {"c", "d", "e", "f"})
                        .build()
                , args
        );
        assertEquals(5, cliParameters.options.size());
        assertEquals("a-value", cliParameters.options.get("a"));
        assertEquals("b-value", cliParameters.options.get("b"));
        assertEquals(null, cliParameters.options.get("c"));
        assertEquals(null, cliParameters.options.get("d"));
        assertEquals(null, cliParameters.options.get("e"));
        assertFalse(cliParameters.options.containsKey("f"));
        assertEquals(3, cliParameters.operands.size());
        assertEquals("aaa", cliParameters.operands.get(0));
        assertEquals("bbb", cliParameters.operands.get(1));
        assertEquals("ccc", cliParameters.operands.get(2));
    }

    @Test
    public void parseWindowsStyleTest() {

        String[] args = new String[] {
                "/a", "a-value",
                "/b", "b-value",
                "/c",
                "/de",
                "aaa", "bbb", "ccc"
        };
        CLIParameters cliParameters = CLIParameterParser.parseWindowsStyle(
                CLIParameterRule.builder()
                        .needValueParameters(new String[] {"a", "b"})
                        .optionOnlyParameters(new String[] {"c", "d", "e", "f"})
                        .build()
                , args
        );
        assertEquals(5, cliParameters.options.size());
        assertEquals("a-value", cliParameters.options.get("a"));
        assertEquals("b-value", cliParameters.options.get("b"));
        assertEquals(null, cliParameters.options.get("c"));
        assertEquals(null, cliParameters.options.get("d"));
        assertEquals(null, cliParameters.options.get("e"));
        assertFalse(cliParameters.options.containsKey("f"));
        assertEquals(3, cliParameters.operands.size());
        assertEquals("aaa", cliParameters.operands.get(0));
        assertEquals("bbb", cliParameters.operands.get(1));
        assertEquals("ccc", cliParameters.operands.get(2));
    }

    @Test
    public void cliParameterRuleTest() {
        CLIParameterRule rule;

        // optionalが機能してnullにはなっていない？
        rule = CLIParameterRule.builder()
                .build();
        assertEquals(0, rule.needValueParameters.size());
        assertEquals(0, rule.optionOnlyParameters.size());

        // needValueParametersに値は入った？
        rule = CLIParameterRule.builder()
                .needValueParameters(new String[] {"a", "b"})
                .build();
        assertEquals(2, rule.needValueParameters.size());
        assertEquals("a", rule.needValueParameters.get(0));
        assertEquals("b", rule.needValueParameters.get(1));
        assertEquals(0, rule.optionOnlyParameters.size());

        // optionOnlyParametersに値は入った？
        rule = CLIParameterRule.builder()
                .optionOnlyParameters(new String[] {"c", "d", "e"})
                .build();
        assertEquals(0, rule.needValueParameters.size());
        assertEquals(3, rule.optionOnlyParameters.size());
        assertEquals("c", rule.optionOnlyParameters.get(0));
        assertEquals("d", rule.optionOnlyParameters.get(1));
        assertEquals("e", rule.optionOnlyParameters.get(2));

        // needValueParametersとoptionOnlyParametersに値は入った？
        rule = CLIParameterRule.builder()
                .needValueParameters(new String[] {"a", "b"})
                .optionOnlyParameters(new String[] {"c", "d", "e"})
                .build();
        assertEquals(2, rule.needValueParameters.size());
        assertEquals("a", rule.needValueParameters.get(0));
        assertEquals("b", rule.needValueParameters.get(1));
        assertEquals(3, rule.optionOnlyParameters.size());
        assertEquals("c", rule.optionOnlyParameters.get(0));
        assertEquals("d", rule.optionOnlyParameters.get(1));
        assertEquals("e", rule.optionOnlyParameters.get(2));
    }
}
