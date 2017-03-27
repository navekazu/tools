package tools.tail.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private TextField pathField;

    @FXML
    private TextArea contentArea;

    @FXML
    private Label informationLabel;

    private FileObserver fileObserver;

    public void setPath(String path) {
        pathField.setText(path==null? "": path);
        startObserve();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileObserver = null;
    }

    private void startObserve() {
        if (pathField.getText().length()==0) {
            return;
        }

        if (fileObserver!=null) {
            fileObserver.stop();
        }

        fileObserver = new FileObserver(pathField.getText(), contentArea);
        Thread thread = new Thread(fileObserver);
        thread.start();
    }

    public void stop() {
        fileObserver.stop();
    }

    private static class FileObserver implements Runnable {
        private String path;
        private TextArea contentArea;
        private boolean run;

        public FileObserver(String path, TextArea contentArea) {
            this.path = path;
            this.contentArea = contentArea;
            this.run = true;
        }

        public synchronized void stop() {
            run = false;
        }
        private synchronized boolean getRun() {
            return run;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new FileReader(path))) {
                char[] c = new char[1];
                while (getRun()) {
                    while ((in.read(c)) != -1) {
                        contentArea.appendText(String.valueOf(c));
                    }
                    Thread.sleep(100);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
