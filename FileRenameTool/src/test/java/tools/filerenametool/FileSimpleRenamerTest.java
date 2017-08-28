package tools.filerenametool;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FileSimpleRenamerTest {
    @Test
    public void convertTest() throws Exception {
        FileSimpleRenamer r = new FileSimpleRenamer();
        assertEquals("a", r.convert("ａ"));
        assertEquals("A", r.convert("Ａ"));
        assertEquals("1", r.convert("１"));
        assertEquals("@", r.convert("＠"));
        assertEquals("1", r.convert("一"));
    }


    @Test
    public void cutRepeatTest() throws Exception {
        FileSimpleRenamer r = new FileSimpleRenamer();
        assertEquals(" ", r.cutRepeat("  "));
    }
}
