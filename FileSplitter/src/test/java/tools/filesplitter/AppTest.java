package tools.filesplitter;

import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
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

    private String createTestFile01() {
        String file = "test01.dat";
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

    @Test
    public void splitFileTest() {
        String file;
        App app = new App();

        file = createTestFile01();
        app.splitFile(file, 10);
    }
}
