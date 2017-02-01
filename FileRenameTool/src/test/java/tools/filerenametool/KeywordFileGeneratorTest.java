package tools.filerenametool;

import org.junit.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class KeywordFileGeneratorTest {
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
    public void generateTest() throws IOException {
        try (BufferedWriter out = Files.newBufferedWriter(Paths.get("generateTest.txt"), Charset.forName("MS932"))) {
            out.write("foo=bar");
            out.newLine();
            out.write("some=one");
            out.newLine();
            out.write("key=キーワード1");
            out.newLine();
            out.write("some=なにか");
            out.newLine();
            out.write("key=キーワード2");
            out.newLine();
        }
        KeywordFileGenerator keywordFileGenerator = new KeywordFileGenerator();
        keywordFileGenerator.generate("generateTest.txt", "keywordTest.txt");
    }
}
