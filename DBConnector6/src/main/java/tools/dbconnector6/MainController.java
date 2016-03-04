package tools.dbconnector6;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tools.dbconnector6.controller.ControllerManager;
import tools.dbconnector6.entity.TableColumnTab;
import tools.dbconnector6.entity.TablePropertyTab;
import tools.dbconnector6.service.DbStructureUpdateService;
import tools.dbconnector6.service.QueryResultUpdateService;
import tools.dbconnector6.service.TableStructureUpdateService;
import tools.dbconnector6.service.TableStructureTabPaneUpdateService;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static tools.dbconnector6.DbStructureTreeItem.ItemType.DATABASE;

public class MainController extends Application implements Initializable, MainControllerInterface {
    private static SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    @FXML
    private TextField filterTextField;

    @FXML
    private TextField schemaTextField;

    @FXML
    private Button searchButton;

    @FXML
    private TreeView dbStructureTreeView;
    private DbStructureTreeItem dbStructurRootItem;

    @FXML
    private TabPane tableStructureTabPane;

    @FXML
    private Tab tablePropertyTab;

    @FXML
    private TableView tablePropertyTableView;
    @FXML
    private TableColumn<TablePropertyTab, String> keyTableColumn;
    @FXML
    private TableColumn<TablePropertyTab, String> valueTableColumn;

    @FXML
    private Tab tableColumnTab;

    @FXML
    private TableView tableColumnTableView;
    @FXML
    private TableColumn<TableColumnTab, String> nameTableColumn;
    @FXML
    private TableColumn<TableColumnTab, String> typeTableColumn;
    @FXML
    private TableColumn<TableColumnTab, Integer> sizeTableColumn;
    @FXML
    private TableColumn<TableColumnTab, Integer> decimalDigitsTableColumn;
    @FXML
    private TableColumn<TableColumnTab, String> nullableTableColumn;
    @FXML
    private TableColumn<TableColumnTab, Integer> primaryKeyTableColumn;
    @FXML
    private TableColumn<TableColumnTab, String> remarksTableColumn;
    @FXML
    private TableColumn<TableColumnTab, String> columnDefaultTableColumn;
    @FXML
    private TableColumn<TableColumnTab, String> autoincrementTableColumn;
    @FXML
    private TableColumn<TableColumnTab, String> generatedColumnTableColumn;

    @FXML
    private Tab tableIndexTab;

    @FXML
    private ComboBox tableIndexComboBox;
    @FXML
    private TextField tablePrimaryKeyTextField;
    @FXML
    private TextField tableUniqueKeyTextField;
    @FXML
    private ListView tableIndexListView;

    @FXML
    private TextArea queryTextArea;

    @FXML
    private TableView queryResultTableView;

    @FXML
    private TextArea logTextArea;

    private Connection connection;
    private BackgroundCallback dbStructureUpdateService;
    private BackgroundCallback tableStructureTabPaneUpdateService;
    private BackgroundCallback tableStructureUpdateService;
    private BackgroundCallback queryResultUpdateService;

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
        dbStructurRootItem = new DbStructureTreeItem(DATABASE, DATABASE.getName(), null);
        dbStructureTreeView.setRoot(dbStructurRootItem);
        dbStructureTreeView.getSelectionModel().selectedItemProperty().addListener(new DbStructureTreeViewChangeListener());

        dbStructureUpdateService = new BackgroundCallback(new DbStructureUpdateService(this));
        tableStructureTabPaneUpdateService = new BackgroundCallback(new TableStructureTabPaneUpdateService(this));
        tableStructureUpdateService = new BackgroundCallback(new TableStructureUpdateService(this));

        keyTableColumn.setCellValueFactory(new PropertyValueFactory<TablePropertyTab, String>("key"));
        valueTableColumn.setCellValueFactory(new PropertyValueFactory<TablePropertyTab, String>("value"));

        nameTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, String>("name"));
        typeTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, String>("type"));
        sizeTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, Integer>("size"));
        decimalDigitsTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, Integer>("decimalDigits"));
        nullableTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, String>("nullable"));
        primaryKeyTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, Integer>("primaryKey"));
        remarksTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, String>("remarks"));
        columnDefaultTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, String>("columnDefault"));
        autoincrementTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, String>("autoincrement"));
        generatedColumnTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, String>("generatedColumn"));

        queryResultUpdateService = new BackgroundCallback(new QueryResultUpdateService(this));

        dbStructureUpdateService.restart();
        tableStructureTabPaneUpdateService.restart();
        tableStructureUpdateService.restart();

        queryResultTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void onClose(ActionEvent event) throws SQLException {
        if (connection!=null) {
            connection.close();
            connection = null;
        }
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
            dbStructureUpdateService.restart();
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
        if (connection==null) {
            writeLog("No connect.");
        }
        try {
            connection.commit();
        } catch(Exception e) {
            writeLog(e.getMessage());
        }
    }

    @FXML
    private void onRollback(ActionEvent event) {
        if (connection==null) {
            writeLog("No connect.");
        }
        try {
            connection.rollback();
        } catch(Exception e) {
            writeLog(e.getMessage());
        }
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

    @FXML
    private void onSearchButton(ActionEvent event) {
        if (connection!=null) {
            dbStructureUpdateService.restart();
        }
    }

    public synchronized void writeLog(String message, Object... args) {
        String logText = logDateFormat.format(new Date())+" " + String.format(message, args);
        logTextArea.appendText(logText + "\n");
    }

    public Connection getConnection() {
        return connection;
    }

    public BackgroundCallback getDbStructureUpdateService() {
        return dbStructureUpdateService;
    }

    public BackgroundCallback getTableStructureTabPaneUpdateService() {
        return tableStructureTabPaneUpdateService;
    }

    public BackgroundCallback getTableStructureUpdateService() {
        return tableStructureUpdateService;
    }

    public BackgroundCallback getQueryResultUpdateService() {
        return queryResultUpdateService;
    }

    public DbStructureParam getDbStructureParam() {
        MainControllerInterface.DbStructureParam param = new MainControllerInterface.DbStructureParam();
        param.filterTextField = filterTextField;
        param.schemaTextField = schemaTextField;
        param.dbStructureTreeView = dbStructureTreeView;
        param.dbStructurRootItem = dbStructurRootItem;
        return param;
    }

    public MainControllerInterface.TableStructureTabParam getTableStructureTabParam() {
        MainControllerInterface.TableStructureTabParam param = new MainControllerInterface.TableStructureTabParam();
        param.tableStructureTabPane = tableStructureTabPane;

        param.tablePropertyTab = tablePropertyTab;
        param.tablePropertyTableView = tablePropertyTableView;
        param.keyTableColumn = keyTableColumn;
        param.valueTableColumn = valueTableColumn;

        param.tableColumnTab = tableColumnTab;
        param.tableColumnTableView = tableColumnTableView;
        param.nameTableColumn = nameTableColumn;
        param.typeTableColumn = typeTableColumn;
        param.sizeTableColumn = sizeTableColumn;
        param.decimalDigitsTableColumn = decimalDigitsTableColumn;
        param.nullableTableColumn = nullableTableColumn;
        param.primaryKeyTableColumn = primaryKeyTableColumn;
        param.remarksTableColumn = remarksTableColumn;
        param.columnDefaultTableColumn = columnDefaultTableColumn;
        param.autoincrementTableColumn = autoincrementTableColumn;
        param.generatedColumnTableColumn = generatedColumnTableColumn;

        param.tableIndexTab = tableIndexTab;
        param.tableIndexComboBox = tableIndexComboBox;
        param.tablePrimaryKeyTextField = tablePrimaryKeyTextField;
        param.tableUniqueKeyTextField = tableUniqueKeyTextField;
        param.tableIndexListView = tableIndexListView;

        return param;
    }

    public QueryParam getQueryParam() {
        MainControllerInterface.QueryParam param = new MainControllerInterface.QueryParam();

        param.queryTextArea = queryTextArea;
        param.queryResultTableView = queryResultTableView;

        return param;
    }

    public class DbStructureTreeViewChangeListener implements ChangeListener {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            tableStructureTabPaneUpdateService.restart();
        }
    }

}
