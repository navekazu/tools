package tools.autoinput;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

public class App {
    private Clipboard clipboard;
    private Robot robot;

    public App() throws Exception {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        clipboard = toolkit.getSystemClipboard();
        robot = new Robot();
    }

    public void execute(String[] inputValues) throws Exception {
        robot.delay(1000);
        for (String value: inputValues) {
            toClipboard(value);
            robot.delay(100);
            executePaste();
            robot.delay(100);
            nextFocus();
            robot.delay(100);
        }
    }

    private void toClipboard(String value) throws Exception {
        StringSelection ss = new StringSelection(value);
        clipboard.setContents(ss, ss);
    }

    private void executePaste() throws Exception {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    private void nextFocus() {
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
    }

    public static void main(String[] args) throws Exception {
        App app = new App();
        app.execute(args);
    }
}
