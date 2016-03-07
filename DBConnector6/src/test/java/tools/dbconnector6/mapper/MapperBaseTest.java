package tools.dbconnector6.mapper;

import org.junit.*;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class MapperBaseTest {
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
    public void blankToOneSpaceのテスト() {
        ConnectMapper connectMapper = new ConnectMapper();
        assertEquals(connectMapper.blankToOneSpace(""), " ");
        assertEquals(connectMapper.blankToOneSpace(" "), " ");
        assertEquals(connectMapper.blankToOneSpace("\t"), " ");
        assertEquals(connectMapper.blankToOneSpace("\n"), " ");
        assertEquals(connectMapper.blankToOneSpace("a"), "a");
    }

    @Test
    public void getArchiveFilePathのテスト() {
        ConnectMapper connectMapper = new ConnectMapper();
        assertEquals(connectMapper.getArchiveFilePath(),
                Paths.get(System.getProperty("user.home"), ".DBConnector6", "config", connectMapper.getArchiveFileName()));
    }

}
