package tools.tail;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class App2 {
    public static void main(String[] args) {
        if (args.length==0) {
            return ;
        }

        App2 app2 = new App2();
        app2.tail(args[0]);
    }

    public App2() {
    }

    public void tail(String file) {
        try (InputStream in = new FileInputStream(file)) {
            int data;

            while (true) {
                data=in.read();
                if (data==-1) {
                    Thread.sleep(500);
                    continue;
                }
                System.out.write(data);
            }

        } catch(IOException e){
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
