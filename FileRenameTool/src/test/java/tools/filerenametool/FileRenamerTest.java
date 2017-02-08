package tools.filerenametool;

import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileRenamerTest {
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
    public void splitNameTest() throws IOException {
        FileRenamer fileRenamer = new FileRenamer();
        FileRenamer.FileName fileName;

        fileName = fileRenamer.splitName("xxxx(20170207-2355).ts");
        assertEquals("xxxx", fileName.title);
        assertEquals("(20170207-2355)", fileName.datetime);
        assertEquals("", fileName.subtitle);
        assertEquals(".ts", fileName.suffix);

        fileName = fileRenamer.splitName("xxxx (20170207-2355).ts");
        assertEquals("xxxx", fileName.title);
        assertEquals("(20170207-2355)", fileName.datetime);
        assertEquals("", fileName.subtitle);
        assertEquals(".ts", fileName.suffix);

        fileName = fileRenamer.splitName("xx xx (20170207-2355).ts");
        assertEquals("xx xx", fileName.title);
        assertEquals("(20170207-2355)", fileName.datetime);
        assertEquals("", fileName.subtitle);
        assertEquals(".ts", fileName.suffix);

        fileName = fileRenamer.splitName("xx xx (20170207-2355) yyy.ts");
        assertEquals("xx xx", fileName.title);
        assertEquals("(20170207-2355)", fileName.datetime);
        assertEquals("yyy", fileName.subtitle);
        assertEquals(".ts", fileName.suffix);

        fileName = fileRenamer.splitName("xx xx (20170208-0000) (20170207-2355) yyy.ts");
        assertEquals("xx xx (20170208-0000)", fileName.title);
        assertEquals("(20170207-2355)", fileName.datetime);
        assertEquals("yyy", fileName.subtitle);
        assertEquals(".ts", fileName.suffix);
    }

    @Test
    public void moveBehindTest() throws IOException {
        FileRenamer fileRenamer = new FileRenamer();
        fileRenamer.keywordList.add("キー ワード");

        assertEquals("xxxx (20170207-2355).ts", fileRenamer.moveBehind("xxxx(20170207-2355).ts"));
        assertEquals("タイトル#10 (20170207-2355).ts", fileRenamer.moveBehind("タイトル＃10 (20170207-2355).ts"));
        assertEquals("xx (20170207-2355) yy.ts", fileRenamer.moveBehind("xx yy (20170207-2355).ts"));
        assertEquals("xx (20170207-2355) yy.ts", fileRenamer.moveBehind("xx　yy (20170207-2355).ts"));
        assertEquals("xx (20170207-2355) zz yy.ts", fileRenamer.moveBehind("xx yy (20170207-2355) zz.ts"));
        assertEquals("キー ワード (20170207-2355).ts", fileRenamer.moveBehind("キー ワード (20170207-2355).ts"));
        assertEquals("キー ワード (20170207-2355) xxx.ts", fileRenamer.moveBehind("キー ワード xxx (20170207-2355).ts"));
        assertEquals("キー ワード (20170207-2355) xxx yyy.ts", fileRenamer.moveBehind("キー ワード xxx yyy (20170207-2355).ts"));

        assertEquals("タイトル (20170207-2355) 「サブタイトル」.ts", fileRenamer.moveBehind("タイトル「サブタイトル」 (20170207-2355).ts"));
        assertEquals("タイトル (20170207-2355) 【サブタイトル】.ts", fileRenamer.moveBehind("タイトル【サブタイトル】 (20170207-2355).ts"));
        assertEquals("タイトル (20170207-2355) ～サブタイトル～.ts", fileRenamer.moveBehind("タイトル～サブタイトル～ (20170207-2355).ts"));
    }

    @Test
    public void executeTest() throws IOException {
        FileRenamer fileRenamer = new FileRenamer();
        Path[] paths = new Path[]{
                Paths.get("c:\\temp\\aaa (20170207-2355).ts"),
                Paths.get("c:\\temp\\aaa bbb (20170207-2355).ts"),
                Paths.get("c:\\temp\\aaa.ts"),
                Paths.get("c:\\temp\\新しいテキスト ドキュメント.txt"),
        };
        Path[] shouldBePaths = new Path[]{
                Paths.get("c:\\temp\\aaa (20170207-2355).ts"),
                Paths.get("c:\\temp\\aaa (20170207-2355) bbb.ts"),
                Paths.get("c:\\temp\\aaa.ts"),
                Paths.get("c:\\temp\\新しいテキスト ドキュメント.txt"),
        };

        for (Path path: paths) {
            Files.createFile(path);
            fileRenamer.execute(path.toString());
        }

        for (Path path: shouldBePaths) {
            assertTrue(Files.exists(path));
            Files.delete(path);
        }
    }
}
