package tools.filldisk;

import java.util.stream.IntStream;

public class FillTaskBlank extends FillTask {
    static final byte[] WRITE_DATA = new byte[WRITE_SIZE];
    static {
        IntStream.range(0, WRITE_SIZE)
                .forEach(i -> WRITE_DATA[i] = (byte)0x00);
    }

    @Override
    public FillPattern getCurrentPattern() {
        return FillPattern.BLANK;
    }

    @Override
    public byte[] getWriteData() {
        return WRITE_DATA;
    }
}
