package tools.dbconnector6.controller;

import org.junit.*;
import tools.dbconnector6.serializer.DataSerializer;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MainControllerTest {
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
    public void inputWordのテスト() throws IOException {
        MainController mainController = new MainController();
        StringBuilder builder = new StringBuilder();
        builder.append("1234567890");
        String word;

        // "1234567890" の "1" と "2" の間にキャレットがある状態で、"c" を入力した場合
        word = mainController.inputWord("1234567890", 1, "c");
        assertEquals("1c", word);

        // "1234567890" の "1" と "2" の間にキャレットがある状態で、文字なしのイベントが起きた場合
        word = mainController.inputWord("1234567890", 1, "");
        assertEquals("1", word);

        // 最後にキャレットがある状態で、"select * from foo" を1文字ずつ入れた場合
        assertEquals("s",       mainController.inputWord("", 0, "s"));
        assertEquals("se",      mainController.inputWord("s", 1, "e"));
        assertEquals("sel",     mainController.inputWord("se", 2, "l"));
        assertEquals("sele",    mainController.inputWord("sel", 3, "e"));
        assertEquals("selec",   mainController.inputWord("sele", 4, "c"));
        assertEquals("select",  mainController.inputWord("selec", 5, "t"));
        assertEquals("",        mainController.inputWord("select", 6, " "));
        assertEquals("*",       mainController.inputWord("select ", 7, "*"));
        assertEquals("",        mainController.inputWord("select *", 8, " "));
        assertEquals("f",       mainController.inputWord("select * ", 9, "f"));
        assertEquals("fr",      mainController.inputWord("select * f", 10, "r"));
        assertEquals("fro",     mainController.inputWord("select * fr", 11, "o"));
        assertEquals("from",    mainController.inputWord("select * fro", 12, "m"));
        assertEquals("",        mainController.inputWord("select * from", 13, " "));
        assertEquals("f",       mainController.inputWord("select * from ", 14, "f"));
        assertEquals("fo",      mainController.inputWord("select * from f", 15, "o"));
        assertEquals("foo",     mainController.inputWord("select * from fo", 16, "o"));

        // "select *\nfoo"の改行後にキャレットがある状態で、"from" を1文字ずつ入れた場合
        assertEquals("f",       mainController.inputWord("select *\nfoo", 9, "f"));
        assertEquals("fr",      mainController.inputWord("select *\nffoo", 10, "r"));
        assertEquals("fro",     mainController.inputWord("select *\nfrfoo", 11, "o"));
        assertEquals("from",    mainController.inputWord("select *\nfrofoo", 12, "m"));
    }

    @Test
    public void getNextEmptyLineCaretPositionのテスト() {
        MainController mainController = new MainController();

        // 先頭にいるとき
        assertEquals(0,  mainController.getNextEmptyLineCaretPosition("012345678\n012345678\n", 0, -1));

        // 末尾にいるとき
        assertEquals(20,  mainController.getNextEmptyLineCaretPosition("012345678\n012345678\n", 20, 1));

        // 2行目の先頭にキャレットがある状態で上キーを押した
        assertEquals(0,  mainController.getNextEmptyLineCaretPosition("012345678\n012345678\n", 10, -1));

        // 2行目の先頭にキャレットがある状態で下キーを押した
        assertEquals(20,  mainController.getNextEmptyLineCaretPosition("012345678\n012345678\n", 10, 1));

        // 4行目の先頭にキャレットがある状態で上キーを押した
        // 2行目が空行で、3行目の先頭にキャレットが移動する
        assertEquals(4,  mainController.getNextEmptyLineCaretPosition("a0\n\nb0\nc0\n\nd0", 7, -1));

        // 4行目の先頭にキャレットがある状態で下キーを押した
        // 5行目が空行で、5行目の先頭にキャレットが移動する
        assertEquals(10,  mainController.getNextEmptyLineCaretPosition("a0\n\nb0\nc0\n\nd0", 7, 1));

        // 5行目の先頭にキャレットがある状態で上キーを押した
        // 2行目が空行で、3行目の先頭にキャレットが移動する
        assertEquals(4,  mainController.getNextEmptyLineCaretPosition("a0\n\nb0\nc0\n\nd0", 10, -1));

    }
}
