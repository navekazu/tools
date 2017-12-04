package tools.portlistener;

import java.net.ServerSocket;
import java.io.IOException;

public class App {
    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0]);
            ServerSocket ss = new ServerSocket(port);
            ss.close();
            System.out.println("'" + port + "' is blank port.");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}