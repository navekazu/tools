package tools.filerenametool.old;

import org.junit.*;
import tools.filerenametool.old.FileRenameToolUtil;
import tools.filerenametool.old.FileRenamer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
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

    @Test
    public void readKeywordListTest() throws IOException {
        Path path = Paths.get(FileRenameToolUtil.getToolDirectory().toString(), "Keyword.txt");
        try (BufferedWriter out = Files.newBufferedWriter(path, Charset.forName("MS932"))) {
            out.write("AAA");
            out.newLine();
            out.write("BBB");
            out.newLine();
            out.write("   ");                       // 空行は無視される？
            out.newLine();
            out.newLine();
            out.write("#CCC");                      // コメント行は無視される？
        }

        FileRenamer fileRenamer = new FileRenamer();
        fileRenamer.readKeywordList("Keyword.txt");

        assertEquals(2, fileRenamer.keywordList.size());
        assertEquals("AAA", fileRenamer.keywordList.get(0));
        assertEquals("BBB", fileRenamer.keywordList.get(1));
    }

    @Test
    public void readChangeTitleMapTest() throws IOException {
        Path path = Paths.get(FileRenameToolUtil.getToolDirectory().toString(), "ChangeTitle.txt");
        try (BufferedWriter out = Files.newBufferedWriter(path, Charset.forName("MS932"))) {
            out.write("AAA\tAAA AAA");
            out.newLine();
            out.write("BBB\t\t\tBBB BBB BBB");
            out.newLine();
            out.write("   ");                       // 空行は無視される？
            out.newLine();                          // 空行は無視される？
            out.newLine();
            out.write("CCC");                       // タブで区切っていないのは無視される？
            out.newLine();
            out.write("DDD\t\t\tD D\tDD DD");       // タブが多すぎる行は無視される？
            out.newLine();
            out.write("#EEE\tEEE EEE");             // コメント行は無視される？
        }

        FileRenamer fileRenamer = new FileRenamer();
        fileRenamer.readChangeTitleMap("ChangeTitle.txt");

        assertEquals(2, fileRenamer.changeTitleMap.size());
        assertEquals("AAA AAA", fileRenamer.changeTitleMap.get("AAA"));
        assertEquals("BBB BBB BBB", fileRenamer.changeTitleMap.get("BBB"));
    }

}
