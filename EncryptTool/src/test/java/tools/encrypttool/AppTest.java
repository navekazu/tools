package tools.encrypttool;

import org.junit.*;

import javax.crypto.Cipher;

import static org.junit.Assert.assertEquals;

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
}
