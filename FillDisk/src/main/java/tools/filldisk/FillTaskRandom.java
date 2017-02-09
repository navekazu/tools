package tools.filldisk;

import java.util.Random;
import java.util.stream.IntStream;

public class FillTaskRandom extends FillTask {
    byte[] writeData = new byte[WRITE_SIZE];
    Random random = new Random();

    @Override
    public FillPattern getCurrentPattern() {
        return FillPattern.RANDOM;
    }

    @Override
    public byte[] getWriteData() {

        IntStream.range(0, WRITE_SIZE)
                .forEach(i -> writeData[i] = (byte)(random.nextInt(0xFF+1)));

        return writeData;
    }
}
