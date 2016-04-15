package tools.dbconnector6;

import javafx.application.Application;
import tools.dbconnector6.controller.MainController;

/**
 * ツールのローンチクラス。<br>
 */
public class App {
    /**
     * 唯一のエントリポイント。<br>
     * @param args 実行時引数
     */
    public static void main( String[] args ) {
        Application.launch(MainController.class, args);
    }
}
