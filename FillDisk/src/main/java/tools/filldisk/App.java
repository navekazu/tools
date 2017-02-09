package tools.filldisk;

import java.io.IOException;
import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        Arrays.asList(args).parallelStream()
                .forEach(path -> {
                    App app = new App();
                    try {
                        app.infinityExecute(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static synchronized void log(String message, Object... args) {
        System.out.println(String.format(message, args));
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
