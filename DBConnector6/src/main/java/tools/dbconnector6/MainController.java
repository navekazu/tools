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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import tools.dbconnector6.controller.ControllerManager;
import tools.dbconnector6.entity.ReservedWord;
import tools.dbconnector6.entity.TableColumnTab;
import tools.dbconnector6.entity.TablePropertyTab;
import tools.dbconnector6.service.*;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static tools.dbconnector6.DbStructureTreeItem.ItemType.DATABASE;

public class MainController extends Application implements Initializable, MainControllerInterface {
    private static SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    private Stage primaryStage;

    @FXML
    private TextField filterTextField;

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
    private BackgroundCallback reservedWordUpdateService;

    private Stage reservedWordStage;
    private ReservedWordController reservedWordController;
    private List<ReservedWord> reservedWordList = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = ControllerManager.getControllerManager().getLoarder("main");
        ControllerManager.getControllerManager().getMainStage(loader, primaryStage).show();

        // 初期フォーカスを検索ワード入力欄に（initializeの中ではフォーカス移動できない）
        MainController controller = loader.getController();
        controller.focusQueryTextArea();
        controller.primaryStage = primaryStage;
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
        reservedWordUpdateService = new BackgroundCallback(new ReservedWordUpdateService(this, reservedWordList));

        dbStructureUpdateService.restart();
        tableStructureTabPaneUpdateService.restart();
        tableStructureUpdateService.restart();

        queryResultTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Controllerの読み込み
        FXMLLoader loader;
        loader = ControllerManager.getControllerManager().getLoarder("reservedWord");
        try {
            reservedWordStage = ControllerManager.getControllerManager().getTransparentSubStage(loader, "reservedWord");
        } catch (IOException e) {
            e.printStackTrace();
        }
        reservedWordController = loader.getController();
        reservedWordController.setMainControllerInterface(this);
        reservedWordController.setRservedWordList(reservedWordList);
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
        controller.setMainControllerInterface(this);
        stage.showAndWait();

        connection = controller.getConnection();
        if (connection!=null) {
            writeLog("Connected.");
            dbStructureUpdateService.restart();
            reservedWordUpdateService.restart();
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

    @FXML
    public void onQueryTextAreaKeyPressed(KeyEvent event) {
        if (reservedWordStage.isShowing() && isChangeFocusForReservedWordStage(event.getCode())) {
            event.consume();
            reservedWordStage.requestFocus();
            return;
        }

        if (!isTextInput(event.getCode())) {
            reservedWordStage.hide();
            return;
        }

        int caret = queryTextArea.getCaretPosition();
        String inputText = event.getText();
        String text = (new StringBuilder(queryTextArea.getText())).insert(caret, inputText).toString();
        String inputKeyword = inputWord(text, caret + inputText.length());       // キャレットより前の単語を取得

        if (reservedWordController.notifyQueryInput(event, inputKeyword)) {
            reservedWordStage.setX(0.0);
            reservedWordStage.setY(0.0);
            reservedWordStage.show();
            primaryStage.requestFocus();    // フォーカスは移動させない
        } else {
            reservedWordStage.hide();
        }
    }

    private String inputWord(String text, int caret) {
        StringBuilder caretForward = new StringBuilder(text.substring(0, caret));
        caretForward = caretForward.reverse();

        StringBuilder inputKeyword = new StringBuilder();
        for (int loop=0; loop<caretForward.length(); loop++){
            if (isSpaceInput(caretForward.charAt(loop))) {
                break;
            }
            inputKeyword.insert(0, caretForward.charAt(loop));
        }

        return inputKeyword.toString();
    }

    @FXML
    public void onQueryTextAreaKeyReleased(KeyEvent event) {
    }

    @FXML
    public void onQueryTextAreaKeyTyped(KeyEvent event) {
    }

    @Override
    public void selectReservedWord(String word) {
        reservedWordStage.hide();

        int caret = queryTextArea.getCaretPosition();
        String text = queryTextArea.getText();
        String inputKeyword = inputWord(text, caret);       // キャレットより前の単語を取得

        // キャレットより前の単語を削除
        queryTextArea.deleteText(caret-inputKeyword.length(), caret);

        // キャレット位置に選択した単語を挿入
        queryTextArea.insertText(queryTextArea.getCaretPosition(), word);
    }

    private boolean isChangeFocusForReservedWordStage(KeyCode code) {
        switch (code) {
            case TAB:
            case DOWN:
                return true;
        }

        return false;
    }
    private boolean isSpaceInput(char c) {
        switch(c) {
            case ' ':
            case '\t':
            case '\n':
            case '　':
                return true;
        }
        return false;
    }

    private boolean isTextInput(KeyCode code) {
        switch (code) {
            case A:
            case B:
            case C:
            case D:
            case E:
            case F:
            case G:
            case H:
            case I:
            case J:
            case K:
            case L:
            case M:
            case N:
            case O:
            case P:
            case Q:
            case R:
            case S:
            case T:
            case U:
            case V:
            case W:
            case X:
            case Y:
            case Z:
            case NUMPAD0:
            case NUMPAD1:
            case NUMPAD2:
            case NUMPAD3:
            case NUMPAD4:
            case NUMPAD5:
            case NUMPAD6:
            case NUMPAD7:
            case NUMPAD8:
            case NUMPAD9:
            case DOLLAR:
            case UNDERSCORE:
            case PLUS:
            case MINUS:
            case SLASH:
            case ASTERISK:
                return true;
        }

        return false;
    }

}
