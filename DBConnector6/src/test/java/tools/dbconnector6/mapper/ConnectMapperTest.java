package tools.dbconnector6.mapper;

import org.junit.*;
import tools.dbconnector6.entity.Connect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ConnectMapperTest {
    @BeforeClass
    public static void beforeClass() throws Exception {
        MapperBase.testFileName = "_test";
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
    public void clearメソッドはファイルがなくても例外が発生しないこと() throws IOException {
        ConnectMapper connectMapper = new ConnectMapper();
        connectMapper.clear();
        connectMapper.clear();
    }

    @Test
    public void ファイルがない場合_selectAllは空のリストを返す() throws IOException {
        ConnectMapper connectMapper = new ConnectMapper();
        connectMapper.clear();
        assertEquals(connectMapper.selectAll().size(), 0);
    }

    @Test
    public void saveのテスト() throws IOException {
        ConnectMapper connectMapper = new ConnectMapper();
        List<Connect> list = new ArrayList<>();
        Connect c1 = Connect.builder()
                .libraryPath("l")
                .driver("d")
                .url("url")
                .user("user")
                .password("p")
                .build();
        list.add(c1);
        connectMapper.save(list);
    }

    @Test
    public void saveとselectAllのテスト() throws IOException {
        ConnectMapper connectMapper = new ConnectMapper();
        List<Connect> list = new ArrayList<>();

        Connect c0 = Connect.builder()
                .libraryPath("l0")
                .driver("d0")
                .url("url0")
                .user("user0")
                .password("p0")
                .build();
        list.add(c0);

        Connect c1 = Connect.builder()
                .libraryPath("l1")
                .driver("d1")
                .url("url1")
                .user("user1")
                .password("p1")
                .build();
        list.add(c1);

        Connect c2 = Connect.builder()
                .libraryPath("l2")
                .driver("d2")
                .url("url2")
                .user("user2")
                .password("p2")
                .build();
        list.add(c2);

        connectMapper.save(list);

        list = connectMapper.selectAll();
        assertEquals(list.size(), 3);
        assertEquals(list.get(0), c0);
        assertEquals(list.get(1), c1);
        assertEquals(list.get(2), c2);
    }
}
