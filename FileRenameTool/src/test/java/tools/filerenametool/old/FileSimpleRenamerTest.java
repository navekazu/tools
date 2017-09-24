package tools.filerenametool.old;

import org.junit.Test;
import tools.filerenametool.old.FileSimpleRenamer;

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

    @Test
    public void renameTest() throws Exception {
        FileSimpleRenamer r = new FileSimpleRenamer();
        r.rename("aaaa.txt");
    }
}
