package tools.pomodorotimer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AppTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void test() {
        App app = new App();
        assertEquals(true,  app.isDoingTime(0));
        assertEquals(true,  app.isDoingTime(24));
        assertEquals(false, app.isDoingTime(25));
        assertEquals(false, app.isDoingTime(29));
    }
}
