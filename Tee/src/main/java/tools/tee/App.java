package tools.tee;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class App {
    private List<Tee> teeList;
    public static void main(String[] args) {
        App app = new App();
        app.startTee(app.isAppendOption(args), app.getOptionExcludeList(args));
    }

    private void startTee(boolean appendOutput, List<String> fileList) {
        teeList = new ArrayList<>();

        fileList.stream()
            .forEach(file -> {
                Tee tee = new Tee();
                tee.open(file, appendOutput);
                teeList.add(tee);

                Thread t = new Thread(tee);
                t.start();
            });

        int data;
        try {
            while ((data=System.in.read())!=-1) {
                for (Tee tee: teeList) {
                    tee.write(data);
                }
                System.out.write(data);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static final String[] APPEND_OPTIONS = {"-a", "--append"};
    boolean isAppendOption(String[] args) {
        return Arrays.stream(args)
                .filter(s -> s!=null)
                .anyMatch(s1 -> Arrays.stream(APPEND_OPTIONS).anyMatch(s2 -> s1.equals(s2)));
    }

    List<String> getOptionExcludeList(String[] args) {
        return Arrays.stream(args)
                .filter(s -> s!=null)
                .filter(s1 -> !Arrays.stream(APPEND_OPTIONS).anyMatch(s2 -> s1.equals(s2)))
                .collect(Collectors.toList());
    }
}