package tools.encrypttool;

import org.junit.*;

import javax.crypto.Cipher;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AppTest {

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
    public void getInputFilesTest() {
        assertEquals(Arrays.asList(new String[]{"bbb"}), App.getInputFiles(new String[]{"aaa", "bbb"}));
        assertEquals(Arrays.asList(new String[]{"bbb", "ccc"}), App.getInputFiles(new String[]{"aaa", "bbb", "ccc"}));
        assertEquals(Arrays.asList(new String[]{"bbb", "ccc", "ddd"}), App.getInputFiles(new String[]{"aaa", "bbb", "ccc", "ddd"}));
    }

    @Test
    public void getOpmodeTest() {
        App encryptTool = new App();

        // 暗号化モードと返ってくるか？
        assertEquals(Cipher.ENCRYPT_MODE, encryptTool.getOpmode("test.file"));                                  // ファイル名だけ（スペースなし）
        assertEquals(Cipher.ENCRYPT_MODE, encryptTool.getOpmode("test test.file"));                             // ファイル名だけ（スペースあり）
        assertEquals(Cipher.ENCRYPT_MODE, encryptTool.getOpmode("test\\test.file"));                            // 相対パス（スペースなし）
        assertEquals(Cipher.ENCRYPT_MODE, encryptTool.getOpmode("test test\\test.file"));                       // 相対パス（スペースあり）
        assertEquals(Cipher.ENCRYPT_MODE, encryptTool.getOpmode("c:\\test\\test.file"));                        // 絶対パス（スペースなし）
        assertEquals(Cipher.ENCRYPT_MODE, encryptTool.getOpmode("c:\\test test\\test.file"));                   // 絶対パス（スペースあり）
        assertEquals(Cipher.ENCRYPT_MODE, encryptTool.getOpmode("c:\\test\\test test.file"));                   // 絶対パス（スペースなし）
        assertEquals(Cipher.ENCRYPT_MODE, encryptTool.getOpmode("c:\\test test\\test test.file"));              // 絶対パス（スペースあり）

        // 複合化モードと返ってくるか？
        assertEquals(Cipher.DECRYPT_MODE, encryptTool.getOpmode("test.file.encrypted"));                        // ファイル名だけ（スペースなし）
        assertEquals(Cipher.DECRYPT_MODE, encryptTool.getOpmode("test test.file.encrypted"));                   // ファイル名だけ（スペースあり）
        assertEquals(Cipher.DECRYPT_MODE, encryptTool.getOpmode("test\\test.file.encrypted"));                  // 相対パス（スペースなし）
        assertEquals(Cipher.DECRYPT_MODE, encryptTool.getOpmode("test test\\test.file.encrypted"));             // 相対パス（スペースあり）
        assertEquals(Cipher.DECRYPT_MODE, encryptTool.getOpmode("c:\\test\\test.file.encrypted"));              // 絶対パス（スペースなし）
        assertEquals(Cipher.DECRYPT_MODE, encryptTool.getOpmode("c:\\test test\\test.file.encrypted"));         // 絶対パス（スペースあり）
        assertEquals(Cipher.DECRYPT_MODE, encryptTool.getOpmode("c:\\test\\test test.file.encrypted"));         // 絶対パス（スペースなし）
        assertEquals(Cipher.DECRYPT_MODE, encryptTool.getOpmode("c:\\test test\\test test.file.encrypted"));    // 絶対パス（スペースあり）
    }
    @Test
    public void getOutputFileNameTest() {
        App encryptTool = new App();

        // 暗号化モードのファイル名で返ってくるか？
        assertEquals("test.file.encrypted",                     encryptTool.getOutputFileName("test.file"));                                  // ファイル名だけ（スペースなし）
        assertEquals("test test.file.encrypted",                encryptTool.getOutputFileName("test test.file"));                             // ファイル名だけ（スペースあり）
        assertEquals("test\\test.file.encrypted",               encryptTool.getOutputFileName("test\\test.file"));                            // 相対パス（スペースなし）
        assertEquals("test test\\test.file.encrypted",          encryptTool.getOutputFileName("test test\\test.file"));                       // 相対パス（スペースあり）
        assertEquals("c:\\test\\test.file.encrypted",           encryptTool.getOutputFileName("c:\\test\\test.file"));                        // 絶対パス（スペースなし）
        assertEquals("c:\\test test\\test.file.encrypted",      encryptTool.getOutputFileName("c:\\test test\\test.file"));                   // 絶対パス（スペースあり）
        assertEquals("c:\\test\\test test.file.encrypted",      encryptTool.getOutputFileName("c:\\test\\test test.file"));                   // 絶対パス（スペースなし）
        assertEquals("c:\\test test\\test test.file.encrypted", encryptTool.getOutputFileName("c:\\test test\\test test.file"));              // 絶対パス（スペースあり）

        // 複合化モードのファイル名で返ってくるか？
        assertEquals("test.file",                       encryptTool.getOutputFileName("test.file.encrypted"));                        // ファイル名だけ（スペースなし）
        assertEquals("test test.file",                  encryptTool.getOutputFileName("test test.file.encrypted"));                   // ファイル名だけ（スペースあり）
        assertEquals("test\\test.file",                 encryptTool.getOutputFileName("test\\test.file.encrypted"));                  // 相対パス（スペースなし）
        assertEquals("test test\\test.file",            encryptTool.getOutputFileName("test test\\test.file.encrypted"));             // 相対パス（スペースあり）
        assertEquals("c:\\test\\test.file",             encryptTool.getOutputFileName("c:\\test\\test.file.encrypted"));              // 絶対パス（スペースなし）
        assertEquals("c:\\test test\\test.file",        encryptTool.getOutputFileName("c:\\test test\\test.file.encrypted"));         // 絶対パス（スペースあり）
        assertEquals("c:\\test\\test test.file",        encryptTool.getOutputFileName("c:\\test\\test test.file.encrypted"));         // 絶対パス（スペースなし）
        assertEquals("c:\\test test\\test test.file",   encryptTool.getOutputFileName("c:\\test test\\test test.file.encrypted"));    // 絶対パス（スペースあり）
    }
/*
    @Test
    public void updateTest() throws IOException {

        byte[] data = new byte[]{
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,
                0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29,
        };

        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("sample_plain.txt"))) {
            out.write(data);

        } catch (IOException e) {
            e.printStackTrace();
        }

        App encryptTool = new App();
        assertEquals(true, encryptTool.update(Cipher.ENCRYPT_MODE, "sample_plain.txt", "1234567890123456", "sample_encrypt.txt"));
        assertEquals(true, encryptTool.update(Cipher.DECRYPT_MODE, "sample_encrypt.txt", "1234567890123456", "sample_result.txt"));
        assertEquals(true, Arrays.equals(Files.readAllBytes(Paths.get("sample_plain.txt")), Files.readAllBytes(Paths.get("sample_result.txt"))));

        assertEquals(true, encryptTool.update(Cipher.ENCRYPT_MODE, "sample_plain.txt", "password", "sample_encrypt.txt"));
        assertEquals(true, encryptTool.update(Cipher.DECRYPT_MODE, "sample_encrypt.txt", "password", "sample_result.txt"));
        assertEquals(true, Arrays.equals(Files.readAllBytes(Paths.get("sample_plain.txt")), Files.readAllBytes(Paths.get("sample_result.txt"))));
    }

    @Test
    public void createKeyFileTest() {
        App encryptTool = new App();
        encryptTool.createKeyFile("test", App.KEY_FILE_PATH+"_test");
    }

    @Test
    public void readKeyFileTest() {
        App encryptTool = new App();
        byte[] result = encryptTool.readKeyFile(App.KEY_FILE_PATH+"_test");
        byte[] answer = new byte[]{
                (byte)0x9f, (byte)0x86, (byte)0xd0, (byte)0x81, (byte)0x88, (byte)0x4c, (byte)0x7d, (byte)0x65,
                (byte)0x9a, (byte)0x2f, (byte)0xea, (byte)0xa0, (byte)0xc5, (byte)0x5a, (byte)0xd0, (byte)0x15,
                (byte)0xa3, (byte)0xbf, (byte)0x4f, (byte)0x1b, (byte)0x2b, (byte)0x0b, (byte)0x82, (byte)0x2c,
                (byte)0xd1, (byte)0x5d, (byte)0x6c, (byte)0x15, (byte)0xb0, (byte)0xf0, (byte)0x0a, (byte)0x08,
        };
        assertTrue(Arrays.equals(answer, result));
    }
*/
}
