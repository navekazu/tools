package tools.filesplitter;

import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AppTest {
    @Test
    public void getOrderSizeTest() {
        App app = new App();

        // 各単位をパースしているか？
        assertEquals(1, app.getOrderSize(new String[]{"-s", "1"}));
        assertEquals(1024L, app.getOrderSize(new String[]{"-s", "1k"}));
        assertEquals(1024L, app.getOrderSize(new String[]{"-s", "1K"}));
        assertEquals(1024L*1024L, app.getOrderSize(new String[]{"-s", "1m"}));
        assertEquals(1024L*1024L, app.getOrderSize(new String[]{"-s", "1M"}));
        assertEquals(1024L*1024L*1024L, app.getOrderSize(new String[]{"-s", "1g"}));
        assertEquals(1024L*1024L*1024L, app.getOrderSize(new String[]{"-s", "1G"}));
        assertEquals(1024L*1024L*1024L*1024L, app.getOrderSize(new String[]{"-s", "1t"}));
        assertEquals(1024L*1024L*1024L*1024L, app.getOrderSize(new String[]{"-s", "1T"}));
        assertEquals(1024L*1024L*1024L*1024L*1024L, app.getOrderSize(new String[]{"-s", "1p"}));
        assertEquals(1024L*1024L*1024L*1024L*1024L, app.getOrderSize(new String[]{"-s", "1P"}));
        assertEquals(1024L*1024L*1024L*1024L*1024L*1024L, app.getOrderSize(new String[]{"-s", "1e"}));
        assertEquals(1024L*1024L*1024L*1024L*1024L*1024L, app.getOrderSize(new String[]{"-s", "1E"}));

        // 整数をパースしているか？
        assertEquals(10240L, app.getOrderSize(new String[]{"-s", "10k"}));
        assertEquals(102400L, app.getOrderSize(new String[]{"-s", "100k"}));
        assertEquals(1024000L, app.getOrderSize(new String[]{"-s", "1000k"}));

        // 指定なしの場合は100MBが返ってくるか？
        assertEquals(1024L*1024L*100L, app.getOrderSize(new String[]{""}));

        // パースできない場合は例外が飛んでくるか？
        try{
            app.getOrderSize(new String[]{"-s", "1a"});
            fail();
        } catch (IllegalArgumentException e) {}
        try{
            app.getOrderSize(new String[]{"-s", "a"});
            fail();
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void getTargetFileListTest() {
        App app = new App();

        // ノーマル
        assertEquals("aaa", String.join("/", app.getTargetFileList(new String[]{"aaa"})));
        assertEquals("aaa/bbb", String.join("/", app.getTargetFileList(new String[]{"aaa", "bbb"})));
        assertEquals("aaa/bbb/ccc", String.join("/", app.getTargetFileList(new String[]{"aaa", "bbb", "ccc"})));

        // -s指定
        assertEquals("aaa", String.join("/", app.getTargetFileList(new String[]{"-s", "1m", "aaa"})));
        assertEquals("aaa/bbb", String.join("/", app.getTargetFileList(new String[]{"aaa", "-s", "1m", "bbb"})));
        assertEquals("aaa/bbb/ccc", String.join("/", app.getTargetFileList(new String[]{"aaa", "bbb", "-s", "1m", "ccc"})));
        assertEquals("aaa/bbb", String.join("/", app.getTargetFileList(new String[]{"aaa", "bbb", "-s", "ccc"})));
    }

    private String createTestFile01(String file) {
        byte[] data = new byte[] {
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f,
                0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b, 0x3c, 0x3d, 0x3e, 0x3f,
                0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f,
                0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x5b, 0x5c, 0x5d, 0x5e, 0x5f,
                0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f,
                0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0x7b, 0x7c, 0x7d, 0x7e, 0x7f,
        };

        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            out.write(data);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    // createTestFile01とは、先頭の1バイトが違う
    private String createTestFile02(String file) {
        byte[] data = new byte[] {
                0x11, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
                0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f,
                0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f,
                0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b, 0x3c, 0x3d, 0x3e, 0x3f,
                0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f,
                0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x5b, 0x5c, 0x5d, 0x5e, 0x5f,
                0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f,
                0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0x7b, 0x7c, 0x7d, 0x7e, 0x7f,
        };

        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            out.write(data);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Test
    public void splitFileTest() throws IOException, InterruptedException {
        App app = new App();

        // テスト用ファイルを分割
        String file1 = createTestFile01("test01.dat");
        app.splitFile(file1, 10, true, false);

        // テスト用ファイルをリネーム
        String file2 = file1+"_";
        Files.move(Paths.get(file1), Paths.get(file2), StandardCopyOption.REPLACE_EXISTING);

        // ファイル復元のバッチファイルを実行
        String batchFile = "test01.dat.bat";
        Runtime.getRuntime().exec(batchFile).waitFor();     // 終了するまで待機しないと、結合したファイルが存在しない場合がある
        assertTrue(compareFile(file1, file2));
    }

    @Test
    public void splitFileTest2() throws IOException, InterruptedException {
        App app = new App();

        // バッチファイルはできて、シェルファイルはできない？
        app.splitFile(createTestFile01("test02.dat"), 10, true, false);
        assertTrue(Files.exists(Paths.get("test02.dat.bat")));
        assertFalse(Files.exists(Paths.get("test02.dat.sh")));

        // バッチファイルはできなくて、シェルファイルはできる？
        app.splitFile(createTestFile01("test03.dat"), 10, false, true);
        assertFalse(Files.exists(Paths.get("test03.dat.bat")));
        assertTrue(Files.exists(Paths.get("test03.dat.sh")));

        // バッチファイルも、シェルファイルも、できない？
        app.splitFile(createTestFile01("test04.dat"), 10, false, false);
        assertFalse(Files.exists(Paths.get("test04.dat.bat")));
        assertFalse(Files.exists(Paths.get("test04.dat.sh")));

        // バッチファイルも、シェルファイルも、できる？
        app.splitFile(createTestFile01("test05.dat"), 10, true, true);
        assertTrue(Files.exists(Paths.get("test05.dat.bat")));
        assertTrue(Files.exists(Paths.get("test05.dat.sh")));
    }

    @Test
    public void compareFileTest() throws IOException, InterruptedException {
        String file1 = createTestFile01("test11.dat");
        String file2 = createTestFile02("test12.dat");
        String file3 = createTestFile01("test13.dat");
        assertFalse(compareFile(file1, file2));
        assertTrue(compareFile(file1, file3));
    }

    private boolean compareFile(String file1, String file2) throws IOException {
        Path path1 = Paths.get(file1);
        Path path2 = Paths.get(file2);

        // 存在する？
        if (!Files.exists(path1) || !Files.exists(path2) ) {
            return false;
        }

        // サイズが違う？
        if (Files.size(path1)!=Files.size(path2)) {
            return false;
        }

        // 内容が違う？
        try (BufferedInputStream in1 = new BufferedInputStream(Files.newInputStream(path1));
                BufferedInputStream in2 = new BufferedInputStream(Files.newInputStream(path2))) {
            int data1;
            int data2;

            while ((data1=in1.read())!=-1 && (data2=in2.read())!=-1) {
                if (data1!=data2) {
                    return false;
                }
            }
        }

        return true;
    }

    @Test
    public void getCreateBatchFileFlagTest() {
        App app = new App();
        assertTrue(app.getCreateBatchFileFlag(new String[]{"-bat"}));

    }

    @Test
    public void getCreateShellFileFlagTest() {
        App app = new App();

        assertTrue(app.getCreateShellFileFlag(new String[]{"-sh"}));
    }
}

