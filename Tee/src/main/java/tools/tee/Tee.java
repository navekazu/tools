package tools.tee;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.io.OutputStream;
import java.io.FileOutputStream;

public class Tee implements Runnable {
    private OutputStream out;

    public Tee() {
    }

    public void run() {

    }

    public void open(String outputFile, boolean appendOutput) {
        try {
            out = new FileOutputStream(outputFile, appendOutput);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (out!=null) {
                out.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void write(int data) {
        try {
            if (out==null) {
                return ;
            }
            out.write(data);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}