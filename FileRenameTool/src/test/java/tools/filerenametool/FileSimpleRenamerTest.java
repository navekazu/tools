package tools.filerenametool;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FileSimpleRenamerTest {
    @Test
    public void convertTest() throws Exception {
        FileSimpleRenamer r = new FileSimpleRenamer();
        assertEquals("a", r.convert("a"));
        assertEquals("Ａ", r.convert("A"));

    }


    @Test
    public void cutRepeatTest() throws Exception {
        FileSimpleRenamer r = new FileSimpleRenamer();
        assertEquals(" ", r.cutRepeat("  "));
    }
}
