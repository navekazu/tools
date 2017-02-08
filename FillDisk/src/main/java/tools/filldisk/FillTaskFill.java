package tools.filldisk;

import java.util.stream.IntStream;

public class FillTaskFill extends FillTask {
    static final byte[] WRITE_DATA = new byte[WRITE_SIZE];
    static {
        IntStream.range(0, WRITE_SIZE)
                .forEach(i -> WRITE_DATA[i] = (byte)0xFF);
    }

    @Override
    public FillPattern getCurrentPattern() {
        return FillPattern.FILL;
    }

    @Override
    public byte[] getWriteData() {
        return WRITE_DATA;
    }
}
