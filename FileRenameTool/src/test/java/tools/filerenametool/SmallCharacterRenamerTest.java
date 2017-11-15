package tools.filerenametool;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SmallCharacterRenamerTest {
    @Test
    public void getSmallCharacterTest() throws Exception {
        SmallCharacterRenamer renamer = new SmallCharacterRenamer();
        assertEquals("test", renamer.getSmallCharacter("test"));
        assertEquals("test", renamer.getSmallCharacter("ｔｅｓｔ"));
    }
}
