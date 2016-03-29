package tools.dbconnector6.serializer;

import org.junit.*;
import tools.dbconnector6.mapper.MapperBase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

public class TemporaryQuerySerializerTest {
    @BeforeClass
    public static void beforeClass() throws Exception {
        DataSerializer.setUtMode(true);
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
    public void createTempolaryFileのテスト() throws IOException {
        TemporaryQuerySerializer serializer = new TemporaryQuerySerializer();
        Path path = serializer.createTempolaryFile("");       // VM終了後にテンポラリファイルが消えているか確認する
        assertTrue(Files.exists(path));
    }
}
