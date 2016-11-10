package tools.templategenerator;

import java.io.IOException;

public class App {


    public static void main(String[] args) {
        if (args.length!=3) {
            throw new IllegalArgumentException();
        }

        GeneratorParam param = GeneratorParam.builder()
                .currentPath(args[0])
                .projectName(args[1])
                .packageName(args[2])
                .build();

        try {
            Generator generator = new Generator();
            generator.generate(param);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}