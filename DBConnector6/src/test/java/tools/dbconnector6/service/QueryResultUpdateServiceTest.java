package tools.dbconnector6.service;

import org.junit.*;
import tools.dbconnector6.mapper.MapperBase;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class QueryResultUpdateServiceTest {
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
    public void splitSqlのテスト() {
        QueryResultUpdateService service = new QueryResultUpdateService(null);

        String[] splitSql;

        // 1つのSQL
        splitSql = service.splitSql("select * from foo");
        assertEquals(1, splitSql.length);
        assertEquals("select * from foo", splitSql[0]);

        splitSql = service.splitSql("select * from foo\n");
        assertEquals(1, splitSql.length);
        assertEquals("select * from foo", splitSql[0]);

        splitSql = service.splitSql("select * from foo;\n");
        assertEquals(1, splitSql.length);
        assertEquals("select * from foo", splitSql[0]);

        // 2つのSQL(セミコロン区切り)
        splitSql = service.splitSql("select * from foo;\nselect * from bar;\n");
        assertEquals(2, splitSql.length);
        assertEquals("select * from foo", splitSql[0]);
        assertEquals("select * from bar", splitSql[1]);

        splitSql = service.splitSql("select * from foo;\nselect * from bar\n");
        assertEquals(2, splitSql.length);
        assertEquals("select * from foo", splitSql[0]);
        assertEquals("select * from bar", splitSql[1]);

        splitSql = service.splitSql("select * from foo;\nselect * from bar");
        assertEquals(2, splitSql.length);
        assertEquals("select * from foo", splitSql[0]);
        assertEquals("select * from bar", splitSql[1]);

        // 2つのSQL(スラッシュ区切り)
        splitSql = service.splitSql("select * from foo/\nselect * from bar/\n");
        assertEquals(2, splitSql.length);
        assertEquals("select * from foo", splitSql[0]);
        assertEquals("select * from bar", splitSql[1]);

        splitSql = service.splitSql("select * from foo/\nselect * from bar\n");
        assertEquals(2, splitSql.length);
        assertEquals("select * from foo", splitSql[0]);
        assertEquals("select * from bar", splitSql[1]);

        splitSql = service.splitSql("select * from foo/\nselect * from bar");
        assertEquals(2, splitSql.length);
        assertEquals("select * from foo", splitSql[0]);
        assertEquals("select * from bar", splitSql[1]);

        // 改行が混じった2つのSQL(セミコロン区切り)
        splitSql = service.splitSql("select *\nfrom foo;\nselect *\nfrom bar;\n");
        assertEquals(2, splitSql.length);
        assertEquals("select *\nfrom foo", splitSql[0]);
        assertEquals("select *\nfrom bar", splitSql[1]);

        // 改行が混じった2つのSQL(セミコロン区切り)
        splitSql = service.splitSql("select *\nfrom foo;\nselect *\nfrom bar;\n");
        assertEquals(2, splitSql.length);
        assertEquals("select *\nfrom foo", splitSql[0]);
        assertEquals("select *\nfrom bar", splitSql[1]);

        // 連続した改行が混じった2つのSQL(セミコロン区切り)
        splitSql = service.splitSql("select *\nfrom foo;\n\nselect *\nfrom bar;\n");
        assertEquals(2, splitSql.length);
        assertEquals("select *\nfrom foo", splitSql[0]);
        assertEquals("select *\nfrom bar", splitSql[1]);

        // 改行の間が空文字の2つのSQL(セミコロン区切り)
        splitSql = service.splitSql("select *\nfrom foo;\n    \nselect *\nfrom bar;\n");
        assertEquals(2, splitSql.length);
        assertEquals("select *\nfrom foo", splitSql[0]);
        assertEquals("select *\nfrom bar", splitSql[1]);

        // 空文字
        splitSql = service.splitSql("");
        assertEquals(0, splitSql.length);
    }
}
