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
}
