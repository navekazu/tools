package tools.dbconnector6.util;

import org.junit.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class QueryScriptReaderTest {
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
    public void readAllLinesのテスト() {
        Path path;

        try {

            // MS932のテスト
            readAllLinesのテスト("ms932Text.txt", "MS932");

            // UTF-8のテスト
            readAllLinesのテスト("utf8Text.txt", "UTF-8");

            // EUC-JPのテスト
            readAllLinesのテスト("eucjpText.txt", "EUC-JP");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void readAllLinesのテスト(String fileName, String canonicalName) throws IOException {
        StandardOpenOption[] options = new StandardOpenOption[]{
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING
        };
        String textText = "本日は晴天なり。";

        // プロダクトで利用するtempフォルダを、無ければ作成する
        Path parent = Paths.get(System.getProperty("user.home"), ".DBConnector6", "temp");
        Files.createDirectories(parent);

        Path path= Paths.get(parent.toString(), fileName);
        path.toFile().deleteOnExit();
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path, Charset.forName(canonicalName), options))) {
            out.print(textText);
        }

        List<String> list = QueryScriptReader.readAllLines(path);
        assertEquals(textText, list.get(0));
    }
}
