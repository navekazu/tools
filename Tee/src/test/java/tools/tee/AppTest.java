package tools.tee;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {

    @Test
    public void isAppendOptionTest() {
        App app = new App();

        assertTrue(app.isAppendOption(new String[]{"-a"}));
        assertTrue(app.isAppendOption(new String[]{"--append"}));
        assertTrue(app.isAppendOption(new String[]{"-a", "--append"}));

        assertTrue(app.isAppendOption(new String[]{"-a", "foo"}));
        assertTrue(app.isAppendOption(new String[]{"foo", "-a"}));
        assertTrue(app.isAppendOption(new String[]{"foo", "-a", "foo"}));

        assertTrue(app.isAppendOption(new String[]{"--append", "foo"}));
        assertTrue(app.isAppendOption(new String[]{"foo", "--append"}));
        assertTrue(app.isAppendOption(new String[]{"foo", "--append", "foo"}));

        assertFalse(app.isAppendOption(new String[]{"foo"}));
        assertFalse(app.isAppendOption(new String[]{"foo", "foo"}));
    }

    @Test
    public void getOptionExcludeListTest() {
        App app = new App();
        List<String> list;

        list = app.getOptionExcludeList(new String[]{"-a"});
        assertEquals(0, list.size());
        assertEquals(new ArrayList<String>(), list);

        list = app.getOptionExcludeList(new String[]{"--append"});
        assertEquals(0, list.size());
        assertEquals(new ArrayList<String>(), list);

        list = app.getOptionExcludeList(new String[]{"-a", "foo"});
        assertEquals(1, list.size());
        assertEquals(Arrays.asList(new String[]{"foo"}), list);

        list = app.getOptionExcludeList(new String[]{"--append", "foo"});
        assertEquals(1, list.size());
        assertEquals(Arrays.asList(new String[]{"foo"}), list);

        list = app.getOptionExcludeList(new String[]{"foo"});
        assertEquals(1, list.size());
        assertEquals(Arrays.asList(new String[]{"foo"}), list);
    }
}
