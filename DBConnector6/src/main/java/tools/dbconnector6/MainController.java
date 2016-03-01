package tools.dbconnector6;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tools.dbconnector6.controller.ControllerManager;
import tools.dbconnector6.entity.Connect;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainController extends Application implements Initializable, MessageInterface {
    private static SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    @FXML
    private TextField filterTextField;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private TreeView dbStructureTreeView;

    @FXML
    private TabPane tableStructureTabPane;

    @FXML
    private Tab tablePropertyTab;

    @FXML
    private Tab tableColumnTab;

    @FXML
    private Tab tableIndexTab;

    @FXML
    private TextArea queryTextArea;

    @FXML
    private TableView queryResultTableView;

    @FXML
    private TextArea logTextArea;

    private Connection connection;
    private QueryResultUpdateService queryResultUpdateService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = ControllerManager.getControllerManager().getLoarder("main");
        ControllerManager.getControllerManager().getMainStage(loader, primaryStage).show();

        // 初期フォーカスを検索ワード入力欄に（initializeの中ではフォーカス移動できない）
        MainController c = loader.getController();
        c.focusQueryTextArea();
    }

    public void focusQueryTextArea() {
        queryTextArea.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        queryResultUpdateService = new QueryResultUpdateService();
        queryResultUpdateService.setRecordProperty(queryResultTableView.getItems());
        queryResultUpdateService.setColumnProperty(queryResultTableView.getColumns());
    }

    @FXML
    private void onClose(ActionEvent event) {
    }

    @FXML
    private void onUndo(ActionEvent event) {
    }

    @FXML
    private void onRedo(ActionEvent event) {
    }

    @FXML
    private void onCut(ActionEvent event) {
    }

    @FXML
    private void onCopy(ActionEvent event) {
    }

    @FXML
    private void onPaste(ActionEvent event) {
    }

    @FXML
    private void onSettingSqlEditor(ActionEvent event) {
    }

    @FXML
    private void onCallSqlEditor(ActionEvent event) {
    }

    @FXML
    private void onConnect(ActionEvent event) throws IOException, SQLException {

        if (connection!=null) {
            connection.close();
            connection = null;
        }

        FXMLLoader loader = ControllerManager.getControllerManager().getLoarder("connect");
        Stage stage = ControllerManager.getControllerManager().getSubStage(loader, "connect");

        ConnectController controller = loader.getController();
        controller.setMessageInterface(this);
        stage.showAndWait();

        connection = controller.getConnection();
        if (connection!=null) {
            writeLog("Connected.");
        }

        stage.close();
    }

    @FXML
    private void onDisconnect(ActionEvent event) throws SQLException {
        if (connection!=null) {
            connection.close();
            connection = null;
            writeLog("Disconnected.");
        }
    }

    @FXML
    private void onExecuteQuery(ActionEvent event) {
        queryResultUpdateService.restart();
    }


    @FXML
    private void onPasteAndExecuteQuery(ActionEvent event) {
    }

    @FXML
    private void onCancelQuery(ActionEvent event) {
    }

    @FXML
    private void onQueryScript(ActionEvent event) {
    }

    @FXML
    private void onCommit(ActionEvent event) {
    }

    @FXML
    private void onRollback(ActionEvent event) {
    }

    @FXML
    private void onCheckIsolation(ActionEvent event) {
    }

    @FXML
    private void onEvidenceMode(ActionEvent event) {
    }

    @FXML
    private void onIncludeHeader(ActionEvent event) {
    }

    @FXML
    private void onEvidenceDelimiterTab(ActionEvent event) {
    }

    @FXML
    private void onEvidenceDelimiterComma(ActionEvent event) {
    }

    @FXML
    private void onEvidenceDelimiterSpace(ActionEvent event) {
    }

    public void writeLog(String message) {
        String logText = logDateFormat.format(new Date())+" "+message;
        logTextArea.setText(logTextArea.getText() + logText + "\n");
    }

    public class QueryResultUpdateService extends Service {
        private ObjectProperty<ObservableList<TableColumn<String,String>>> columnProperty = new SimpleObjectProperty();
        private ObjectProperty<ObservableList<Map<String, String>>> recordProperty = new SimpleObjectProperty();

        public final void setColumnProperty(ObservableList<TableColumn<String,String>> list) {
            columnProperty.set(list);
        }
        public final ObjectProperty<ObservableList<TableColumn<String,String>>> columnProperty() {
            return columnProperty;
        }
        public final void setRecordProperty(ObservableList<Map<String, String>> list) {
            recordProperty.set(list);
        }
        public final ObjectProperty<ObservableList<Map<String, String>>> objectProperty() {
            return recordProperty;
        }

        @Override
        protected Task createTask() {
            final ObservableList<TableColumn<String,String>> colList = columnProperty().get();
            final ObservableList<Map<String, String>> recordList = objectProperty().get();
            return new Task<Void>() {
                @Override
                protected Void call() {
//                    ObservableList<TableColumn<String,String>> colList = columnProperty.get();
                    colList.clear();
                    colList.add(new TableColumn("test1"));
//                    colList.add(new TableColumn("test2"));
//                    colList.add(new TableColumn("test3"));

                    recordList.clear();
                    for (int loop = 0; loop<10; loop++) {
                        Map<String, String> l = new HashMap<>();
                        l.put("test1", "data "+loop);
                        recordList.add(l);
                    }

//                    ObservableList<Connect> list = recordProperty.get();
//                    list.add(Connect.builder().libraryPath("aaaa").build());

//                    TableColumn<RData, String> col =
//                            new TableColumn<RData, String>("列" + i);
                    return null;
                }
            };
        }
    }
}
