package tools.pomodorotimer.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tools.pomodorotimer.App;

import static org.junit.Assert.assertEquals;

public class MainControllerTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void test() {
        MainController mainController = new MainController();
        assertEquals(true,  mainController.isDoingTime(0));
        assertEquals(true,  mainController.isDoingTime(24));
        assertEquals(false, mainController.isDoingTime(25));
        assertEquals(false, mainController.isDoingTime(29));
    }
}
