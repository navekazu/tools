package tools.dbconnector6.mapper;

import org.junit.*;
import tools.dbconnector6.entity.AppConfig;
import tools.dbconnector6.entity.AppConfigEvidenceMode;
import tools.dbconnector6.entity.AppConfigMainStage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AppConfigTest {
    @BeforeClass
    public static void beforeClass() throws Exception {
        MapperBase.setUtMode(true);
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
    public void unboxingTest() {
        AppConfigMapper mapper = new AppConfigMapper();
        AppConfig config;

        config = mapper.unboxing("EvidenceMode\ttrue\tfalse\t0");
        assertTrue(config instanceof AppConfigEvidenceMode);

        AppConfigEvidenceMode evidenceMode = (AppConfigEvidenceMode)config;
        assertEquals(true, evidenceMode.isEvidenceMode());
        assertEquals(false, evidenceMode.isIncludeHeader());
        assertEquals(0, evidenceMode.getEvidenceDelimiter());

        // 小数点あり
        config = mapper.unboxing("MainStage\ttrue\t0\t1\t2\t3\t4.1\t5.2\t6.3\t7.4");
        assertTrue(config instanceof AppConfigMainStage);

        AppConfigMainStage mainStage = (AppConfigMainStage)config;
        assertEquals(true, mainStage.isMaximized());
        assertEquals(0, mainStage.getX(), 0.0);
        assertEquals(1, mainStage.getY(), 0.0);
        assertEquals(2, mainStage.getWidth(), 0.0);
        assertEquals(3, mainStage.getHeight(), 0.0);
        assertEquals(4.1, mainStage.getPrimaryDividerPosition(), 0.0);
        assertEquals(5.2, mainStage.getLeftDividerPosition(), 0.0);
        assertEquals(6.3, mainStage.getRightDivider1Position(), 0.0);
        assertEquals(7.4, mainStage.getRightDivider2Position(), 0.0);

        // 小数点なし
        config = mapper.unboxing("MainStage\ttrue\t0\t1\t2\t3\t4\t5\t6\t7");
        assertTrue(config instanceof AppConfigMainStage);

        mainStage = (AppConfigMainStage)config;
        assertEquals(true, mainStage.isMaximized());
        assertEquals(0, mainStage.getX(), 0.0);
        assertEquals(1, mainStage.getY(), 0.0);
        assertEquals(2, mainStage.getWidth(), 0.0);
        assertEquals(3, mainStage.getHeight(), 0.0);
        assertEquals(4.0, mainStage.getPrimaryDividerPosition(), 0.0);
        assertEquals(5.0, mainStage.getLeftDividerPosition(), 0.0);
        assertEquals(6.0, mainStage.getRightDivider1Position(), 0.0);
        assertEquals(7.0, mainStage.getRightDivider2Position(), 0.0);
    }

    @Test
    public void autoboxingTest() {
        AppConfigMapper mapper = new AppConfigMapper();
        AppConfig config;

        AppConfigEvidenceMode evidenceMode = AppConfigEvidenceMode.builder()
                .evidenceMode(true)
                .includeHeader(false)
                .evidenceDelimiter(0)
                .build();
        assertEquals("EvidenceMode\ttrue\tfalse\t0", mapper.autoboxing(evidenceMode));

        // 小数点あり
        AppConfigMainStage mainStage = AppConfigMainStage.builder()
                .maximized(true)
                .x(0)
                .y(1)
                .width(2)
                .height(3)
                .primaryDividerPosition(4.1)
                .leftDividerPosition(5.2)
                .rightDivider1Position(6.3)
                .rightDivider2Position(7.4)
                .build();
        assertEquals("MainStage\ttrue\t0\t1\t2\t3\t4.1\t5.2\t6.3\t7.4", mapper.autoboxing(mainStage));

        // 小数点なし
        mainStage = AppConfigMainStage.builder()
                .maximized(true)
                .x(0)
                .y(1)
                .width(2)
                .height(3)
                .primaryDividerPosition(4)
                .leftDividerPosition(5)
                .rightDivider1Position(6)
                .rightDivider2Position(7)
                .build();
        assertEquals("MainStage\ttrue\t0\t1\t2\t3\t4.0\t5.0\t6.0\t7.0", mapper.autoboxing(mainStage));
    }

    @Test
    public void 書き込みTest() throws IOException {
        AppConfigMapper mapper = new AppConfigMapper();
        List<AppConfig> saveList = new ArrayList<>();

        AppConfigEvidenceMode evidenceMode = AppConfigEvidenceMode.builder()
                .evidenceMode(true)
                .includeHeader(false)
                .evidenceDelimiter(0)
                .build();
        saveList.add(evidenceMode);

        AppConfigMainStage mainStage = AppConfigMainStage.builder()
                .maximized(true)
                .x(0)
                .y(1)
                .width(2)
                .height(3)
                .primaryDividerPosition(4.1)
                .leftDividerPosition(5.2)
                .rightDivider1Position(6.3)
                .rightDivider2Position(7.4)
                .build();
        saveList.add(mainStage);

        mapper.save(saveList);

        List<AppConfig> selectList = mapper.selectAll();

        assertEquals(saveList.get(0), selectList.get(0));
        assertEquals(saveList.get(1), selectList.get(1));

    }
}
