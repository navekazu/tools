package tools.gitcommander;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.PipedOutputStream;
import java.io.PipedInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class GitCommander {
    private Runtime runtime;
    private Process process;
    private PipedThread prcOutToStdIn;
    private PipedThread prcErrToStdErr;
    private PipedThread stdOutToPrcIn;


    private void init() throws IOException {
        runtime = Runtime.getRuntime();
        process = runtime.exec("cmd");

        prcOutToStdIn = new PipedThread(process.getErrorStream(), System.err, null, null);
        prcErrToStdErr = new PipedThread(process.getInputStream(), System.out, null, null);
        stdOutToPrcIn = new PipedThread(System.in, process.getOutputStream(), "\n ", "git");

        (new Thread(prcOutToStdIn)).start();
        (new Thread(prcErrToStdErr)).start();
        (new Thread(stdOutToPrcIn)).start();
    }

    private class PipedThread implements Runnable {
        private InputStream in;
        private OutputStream out;
        private byte[] trigger;
        private byte[] command;

        public PipedThread(InputStream in, OutputStream out, String trigger, String command) {
            this.in = in;
            this.out = out;
            this.trigger = trigger==null? null: trigger.getBytes();
            this.command = command==null? null: command.getBytes();
        }

        public void run() {
            try {
                int lastData = 0;
                int data = 0;

                while ((data=in.read()) != -1) {
                    if (trigger!=null) {
                        if (trigger[0]==(byte)lastData
                            &&trigger[1]==(byte)data) {
                            out.write(command);
                        }
                    }

                    out.write(data);
                    out.flush();
                    lastData = data;
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void serve() throws IOException {
        String line;


    }

    public static void main(String... args) throws IOException {
        GitCommander gc = new GitCommander();
        gc.init();
        gc.serve();
    }
}