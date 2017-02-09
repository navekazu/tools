package tools.filldisk;

import org.junit.*;

import java.io.IOException;

public class FillTaskTest {
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
    public void fillDiskBlankTest() throws IOException {
        FillTask task;
        task = FillTaskFactory.createFillTask(FillPattern.BLANK);
        task.eraseDisk("D:\\work");
//        task.fillDisk("D:\\work", 1);
    }

    @Test
    public void fillDiskFillTest() throws IOException {
        FillTask task;
        task = FillTaskFactory.createFillTask(FillPattern.FILL);
        task.eraseDisk("D:\\work");
//        task.fillDisk("D:\\work", 1);
    }

    @Test
    public void fillDiskRandomTest() throws IOException {
        FillTask task;
        task = FillTaskFactory.createFillTask(FillPattern.RANDOM);
        task.eraseDisk("D:\\work");
//        task.fillDisk("D:\\work", 1);
    }


}
