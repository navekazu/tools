package tools.dbconnector6.ui;

import javafx.scene.control.TextArea;
import tools.dbconnector6.MainController;

public class QueryTextAreaController {
    private MainController mainController;
    private TextArea queryTextArea;

    public QueryTextAreaController(MainController mainController, TextArea queryTextArea) {
        this.mainController = mainController;
        this.queryTextArea = queryTextArea;

    }
}
