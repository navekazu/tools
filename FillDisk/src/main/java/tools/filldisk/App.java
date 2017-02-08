package tools.filldisk;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
    }

    public void infinityExecute(String path) throws IOException {
        FillTask[] fillTasks = new FillTask[]{
                FillTaskFactory.createFillTask(FillPattern.BLANK),
                FillTaskFactory.createFillTask(FillPattern.FILL),
                FillTaskFactory.createFillTask(FillPattern.RANDOM),
        };

        int repeat = 1;
        while (true) {
            for (FillTask fillTask: fillTasks) {
                fillTask.eraseDisk(path);
                fillTask.fillDisk(path, repeat);
                repeat++;
            }
        }
    }
}
