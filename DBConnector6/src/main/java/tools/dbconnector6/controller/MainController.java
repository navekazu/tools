package tools.dbconnector6.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tools.dbconnector6.BackgroundService;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.entity.*;
import tools.dbconnector6.mapper.AppConfigMapper;
import tools.dbconnector6.serializer.ApplicationLogSerializer;
import tools.dbconnector6.serializer.WorkingQuerySerializer;
import tools.dbconnector6.service.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;

import static tools.dbconnector6.controller.DbStructureTreeItem.ItemType.DATABASE;

/**
 * DBConnector6のメイン画面コントローラ
 */
public class MainController extends Application implements Initializable, MainControllerInterface {
    private Stage primaryStage;

    // Scene overview
    // +-----------------+---------------------------------------------------+
    // | DB structure    | Query input area                                  |
    // |                 |   queryTextArea                                   |
    // |                 |                                                   |
    // |                 +---------------------------------------------------+
    // |                 | Executed result area                              |
    // |                 |   queryResultTableView                            |
    // |                 |                                                   |
    // |                 |                                                   |
    // |                 |                                                   |
    // +-----------------+                                                   |
    // | Table structure |                                                   |
    // |                 +---------------------------------------------------+
    // |                 | Log area                                          |
    // |                 |   logTextArea                                     |
    // +-----------------+---------------------------------------------------+
    @FXML private TextArea queryTextArea;
    @FXML private TableView queryResultTableView;
    @FXML private TextArea logTextArea;

    // DB structure
    // +------------------------------------+
    // | xxx filterTextField [searchButton] |
    // | +--------------------------------+ |
    // | | dbStructureTreeView            | |
    // | |                                | |
    // | |                                | |
    // | +--------------------------------+ |
    // +------------------------------------+
    @FXML private TextField filterTextField;
    @FXML private Button searchButton;
    @FXML private TreeView dbStructureTreeView;
    private DbStructureTreeItem dbStructurRootItem;

    // Table structure overview
    // +-----------------------------------------------------------+
    // | +-------------------+-----------------+-----------------+ |
    // | | tablePropertyTab  | tableColumnTab  | tableIndexTab   | |
    // | |                                                       | |
    // | |                                                       | |
    // | |                                                       | |
    // | |                                                       | |
    // | | tableStructureTabPane                                 | |
    // | |                                                       | |
    // | +-------------------------------------------------------+ |
    // +-----------------------------------------------------------+
    @FXML private TabPane tableStructureTabPane;
    @FXML private Tab tablePropertyTab;
    @FXML private Tab tableColumnTab;
    @FXML private Tab tableIndexTab;

    // tablePropertyTab
    // +-----------------------------------------------------------+
    // | +-------------------+-----------------+-----------------+ |
    // | | tablePropertyTab  |                 |                 | |
    // | |                                                       | |
    // | | +---------------------------------------------------+ | |
    // | | |keyTableColumn|valueTableColumn                    | | |
    // | | |                                                   | | |
    // | | | tablePropertyTableView                            | | |
    // | | |                                                   | | |
    // | | +---------------------------------------------------+ | |
    // | +-------------------------------------------------------+ |
    // +-----------------------------------------------------------+
    @FXML private TableView tablePropertyTableView;
    @FXML private TableColumn<TablePropertyTab, String> keyTableColumn;
    @FXML private TableColumn<TablePropertyTab, String> valueTableColumn;
    @FXML private TableView tableColumnTableView;

    // tablePropertyTab
    // +-----------------------------------------------------------+
    // | +-------------------+-----------------+-----------------+ |
    // | |                   | tableColumnTab  |                 | |
    // | |                                                       | |
    // | | +---------------------------------------------------+ | |
    // | | |nameTableColumn|typeTableColumn|sizeTableColumn    | | |
    // | | |  decimalDigitsTableColumn|nullableTableColumn     | | |
    // | | |  primaryKeyTableColumn|remarksTableColumn         | | |
    // | | |  columnDefaultTableColumn|autoincrementTableColumn| | |
    // | | |  generatedColumnTableColumn                       | | |
    // | | |                                                   | | |
    // | | | tableColumnTableView                              | | |
    // | | |                                                   | | |
    // | | +---------------------------------------------------+ | |
    // | +-------------------------------------------------------+ |
    // +-----------------------------------------------------------+
    @FXML private TableColumn<TableColumnTab, String> nameTableColumn;
    @FXML private TableColumn<TableColumnTab, String> typeTableColumn;
    @FXML private TableColumn<TableColumnTab, Integer> sizeTableColumn;
    @FXML private TableColumn<TableColumnTab, Integer> decimalDigitsTableColumn;
    @FXML private TableColumn<TableColumnTab, String> nullableTableColumn;
    @FXML private TableColumn<TableColumnTab, Integer> primaryKeyTableColumn;
    @FXML private TableColumn<TableColumnTab, String> remarksTableColumn;
    @FXML private TableColumn<TableColumnTab, String> columnDefaultTableColumn;
    @FXML private TableColumn<TableColumnTab, String> autoincrementTableColumn;
    @FXML private TableColumn<TableColumnTab, String> generatedColumnTableColumn;

    // tableIndexTab
    // +-----------------------------------------------------------+
    // | +-------------------+-----------------+-----------------+ |
    // | |                   |                 | tableIndexTab   | |
    // | |                                                       | |
    // | | xxx tableIndexNameComboBox                            | |
    // | | xxx tableIndexPrimaryKeyTextField                     | |
    // | | xxx tableIndexUniqueKeyTextField                      | |
    // | | +---------------------------------------------------+ | |
    // | | |                                                   | | |
    // | | | tableIndexListView                                | | |
    // | | |                                                   | | |
    // | | +---------------------------------------------------+ | |
    // | +-------------------------------------------------------+ |
    // +-----------------------------------------------------------+
    @FXML private ComboBox tableIndexNameComboBox;
    @FXML private TextField tableIndexPrimaryKeyTextField;
    @FXML private TextField tableIndexUniqueKeyTextField;
    @FXML private ListView tableIndexListView;

    // other UI
    @FXML private SplitPane primarySplitPane;
    @FXML private SplitPane leftSplitPane;
    @FXML private SplitPane rightSplitPane;
    @FXML private CheckMenuItem evidenceMode;
    @FXML private CheckMenuItem evidenceModeIncludeHeader;
    @FXML private ToggleGroup evidenceDelimiter;

    // stage & controller
    private Stage reservedWordStage;
    private ReservedWordController reservedWordController;
    private Set<ReservedWord> reservedWordList = new HashSet<>();

    private Stage alertDialogStage;
    private AlertController alertDialogController;

    private Stage connectStage;
    private ConnectController connectController;

    private Stage editorChooserStage;
    private EditorChooserController editorChooserController;
    private AppConfigEditor appConfigEditor = new AppConfigEditor();;

    // service
    private BackgroundService dbStructureUpdateService;
    private BackgroundService tableStructureTabPaneUpdateService;
    private BackgroundService tableStructureUpdateService;
    private BackgroundService queryExecuteService;
    private BackgroundService reservedWordUpdateService;
    private BackgroundService sqlEditorLaunchService;

    // other field
    private Connection connection;
    private Connect connectParam;
    private String queryScript = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = ControllerManager.getControllerManager().getLoarder("main");
        ControllerManager.getControllerManager().getMainStage(loader, primaryStage);

        MainController controller = loader.getController();
        primaryStage.setOnShown(new MainWindowShownHandler(controller));
        primaryStage.setOnCloseRequest(new MainWindowCloseRequestHandler(controller));

        controller.primaryStage = primaryStage;

        primaryStage.show();

        // 初期フォーカスを検索ワード入力欄に（initializeの中ではフォーカス移動できない）
        controller.focusQueryTextArea();
    }

    private void focusQueryTextArea() {
        queryTextArea.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbStructurRootItem = new DbStructureTreeItem(DATABASE, DATABASE.getName(), null);
        dbStructureTreeView.setRoot(dbStructurRootItem);
        dbStructureTreeView.getSelectionModel().selectedItemProperty().addListener(new DbStructureTreeViewChangeListener());

        // service
        queryExecuteService = new BackgroundService(new QueryExecuteService(this), this);
        reservedWordUpdateService = new BackgroundService(new ReservedWordUpdateService(this, reservedWordList), this);
        sqlEditorLaunchService = new BackgroundService(new SqlEditorLaunchService(this), this);
        dbStructureUpdateService = new BackgroundService(new DbStructureUpdateService(this), this);
        tableStructureTabPaneUpdateService = new BackgroundService(new TableStructureTabPaneUpdateService(this), this);
        tableStructureUpdateService = new BackgroundService(new TableStructureUpdateService(this), this);

        // TableColumnとプロパティの紐付け
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

        // キーワードを右ダブルクリックしたときにクエリとして貼り付ける
        dbStructureTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> { addQueryWordEvent(event); });
        tableColumnTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> { addQueryWordEvent(event); });
        queryResultTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> { addQueryWordEvent(event); });
        tableIndexListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> { addQueryWordEvent(event); });


        dbStructureUpdateService.restart();
        tableStructureTabPaneUpdateService.restart();
        tableStructureUpdateService.restart();

        queryResultTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Controllerの読み込み
        // ToDo: きれいにする
        FXMLLoader loader;
        loader = ControllerManager.getControllerManager().getLoarder("connect");
        try {
            connectStage = ControllerManager.getControllerManager().getSubStage(loader, "connect");
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectController = loader.getController();
        connectController.setMainControllerInterface(this);

        loader = ControllerManager.getControllerManager().getLoarder("reservedWord");
        try {
            reservedWordStage = ControllerManager.getControllerManager().getTransparentSubStage(loader, "reservedWord");
        } catch (IOException e) {
            e.printStackTrace();
        }
        reservedWordController = loader.getController();
        reservedWordController.setMainControllerInterface(this);
        reservedWordController.setRservedWordList(reservedWordList);

        loader = ControllerManager.getControllerManager().getLoarder("alertDialog");
        try {
            alertDialogStage = ControllerManager.getControllerManager().getSubStage(loader, "alertDialog");
            alertDialogStage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        alertDialogController = loader.getController();
        alertDialogController.setMainControllerInterface(this);

        loader = ControllerManager.getControllerManager().getLoarder("editorChooser");
        try {
            editorChooserStage = ControllerManager.getControllerManager().getSubStage(loader, "editorChooser");
            editorChooserStage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        editorChooserController = loader.getController();
        editorChooserController.setMainControllerInterface(this);
    }

    private void showConnect() {
        closeConnection();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                connectStage.showAndWait();
            }
        });
    }
    private void closeConnection() {
        if (isConnectWithoutMessage()) {
            try {
                connection.close();
            } catch (SQLException e) {
                writeLog(e);
            }
            connection = null;
            connectParam = null;
            writeLog("Disconnected.");
        }
        dbStructureUpdateService.restart();
        reservedWordUpdateService.restart();
    }

    private static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    public void writeLog(String message, Object... args) {
        final String logText = LOG_DATE_FORMAT.format(new Date())+" " + String.format(message, args);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                logTextArea.appendText(logText + "\n");
            }
        });
    }

    public void writeLog(Throwable e) {
        ApplicationLogSerializer applicationLogSerializer = new ApplicationLogSerializer();
        try {
            e.printStackTrace();
            applicationLogSerializer.appendText(e.toString());
        } catch (IOException e1) {
            writeLog(e1.toString());
            e1.printStackTrace();
        }
        writeLog(e.toString());
    }

    public void connectNotify() {
        Connection con = connectController.getConnection();
        if (con!=null) {
            writeLog("Connected.");
            connection = con;
            connectParam = connectController.getConnect();
            dbStructureUpdateService.restart();
            reservedWordUpdateService.restart();
        }
    }

    public Connection getConnection() {
        return connection;
    }
    public Connect getConnectParam() {
        return connectParam;
    }


    public BackgroundService getDbStructureUpdateService() {
        return dbStructureUpdateService;
    }

    public BackgroundService getTableStructureTabPaneUpdateService() {
        return tableStructureTabPaneUpdateService;
    }

    public BackgroundService getTableStructureUpdateService() {
        return tableStructureUpdateService;
    }

    public BackgroundService getQueryExecuteService() {
        return queryExecuteService;
    }

    public DbStructureParam getDbStructureParam() {
        MainControllerInterface.DbStructureParam param = new MainControllerInterface.DbStructureParam();
        param.filterTextField = filterTextField;
        param.dbStructureTreeView = dbStructureTreeView;
        param.dbStructureRootItem = dbStructurRootItem;
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
        param.tableIndexNameComboBox = tableIndexNameComboBox;
        param.tableIndexPrimaryKeyTextField = tableIndexPrimaryKeyTextField;
        param.tableIndexUniqueKeyTextField = tableIndexUniqueKeyTextField;
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

    protected String inputWord(String text, int caret, String inputCharacter) {
        // キーイベント前の入力内容に、キーイベントで入力した文字をキャレットの位置に入れる
        StringBuilder realText = new StringBuilder(text);
        realText.insert(caret, inputCharacter);

        // キャレット位置から前に走査してスペースまでを単語として抜き出す
        StringBuilder inputKeyword = new StringBuilder();
        for (int loop=caret-1 + inputCharacter.length(); loop>=0; loop--){
            if (isSpaceInput(realText.charAt(loop))) {
                break;
            }
            inputKeyword.insert(0, realText.charAt(loop));
        }
        return inputKeyword.toString();
    }

    @Override
    public void selectReservedWord(String word) {
        int caret = queryTextArea.getCaretPosition();
        String text = queryTextArea.getText();
        String inputKeyword = inputWord(text, caret, "");       // キャレットより前の単語を取得

        // キャレットより前の単語を削除
        queryTextArea.deleteText(caret-inputKeyword.length(), caret);

        // キャレット位置に選択した単語を挿入
        queryTextArea.insertText(queryTextArea.getCaretPosition(), word);
    }

    @Override
    public void mainControllerRequestFocus() {
        primaryStage.requestFocus();
    }

    @Override
    public void hideReservedWordStage() {
        reservedWordStage.hide();
    }

    @Override
    public void showAlertDialog(String message, String detail) {
        alertDialogController.setContents(message, detail);
        alertDialogController.setWaitMode(false);
        alertDialogStage.showAndWait();
    }
    public void showWaitDialog(String message, String detail) {
        alertDialogController.setContents(message, detail);
        alertDialogController.setWaitMode(true);
        alertDialogStage.showAndWait();
    }
    public void hideWaitDialog() {
        alertDialogStage.hide();
    }

    @Override
    public boolean isEvidenceMode() {
        return evidenceMode.isSelected();
    }

    @Override
    public boolean isEvidenceModeIncludeHeader() {
        return evidenceModeIncludeHeader.isSelected();
    }

    private static final String[] EVIDENCE_DELIMITERS = new String[] {"\t", ",", " "};
    @Override
    public String getEvidenceDelimiter() {
        int selectedIndex = 0;
        for (Toggle toggle: evidenceDelimiter.getToggles()) {
            if (toggle.isSelected()) {
                break;
            }
            selectedIndex++;
        }

        return EVIDENCE_DELIMITERS[selectedIndex];
    }

    @Override
    public String getQuery() {
        String sql;

        if (queryScript!=null) {
            // 読み込んだスクリプトがあるなら、それを実行
            sql = queryScript;
            queryScript = null;

        } else {
            // スクリプトを読み込んでいないなら、クエリ入力している内容を実行

            //  選択したテキストが実行するSQLだが、選択テキストがない場合はテキストエリア全体をSQLとする
            sql = queryTextArea.getSelectedText();
            if (sql.length() <= 0) {
                sql = queryTextArea.getText();
            }
        }

        return sql;
    }

    @Override
    public String getSelectedQuery() {
        return queryTextArea.getSelectedText();
    }

    @Override
    public String getEditorPath() {
        return appConfigEditor.getEditorPath();
    }

    @Override
    public void updateSelectedQuery(String query) {
        int caret = queryTextArea.getCaretPosition();
        int anchor = queryTextArea.getAnchor();
        queryTextArea.replaceText((anchor<caret? anchor: caret), (anchor>caret? anchor: caret), query);    // "begin<=end" の関係でないとNG
    }

    @Override
    public void addQueryWord(String word, boolean shiftDown) {
        updateSelectedQuery(word + (shiftDown? ", ": ""));
        queryTextArea.requestFocus();
    }

    @Override
    public boolean isConnect() {
        boolean result = isConnectWithoutMessage();
        if (!result) {
            writeLog("No connect.");
        }
        return result;
    }

    @Override
    public boolean isConnectWithoutMessage() {
        if (connection==null) {
            return false;
        }
        return true;
    }


    private static final KeyCode[] CHANGE_FOCUS_FOR_RESERVED_WORD_STAGE_CODES = new KeyCode[] {
            KeyCode.TAB, KeyCode.DOWN,
    };
    private static final String[] CHANGE_FOCUS_FOR_RESERVED_WORD_STAGE_STRING = new String[] {
            "\t"
    };
    private boolean isChangeFocusForReservedWordStage(KeyCode code) {
        if(!reservedWordStage.isShowing()) {
            return false;
        }
        return Arrays.stream(CHANGE_FOCUS_FOR_RESERVED_WORD_STAGE_CODES).anyMatch(c -> c == code);
    }
    private boolean isChangeFocusForReservedWordStage(String key) {
        if(!reservedWordStage.isShowing()) {
            return false;
        }
        return Arrays.stream(CHANGE_FOCUS_FOR_RESERVED_WORD_STAGE_STRING).anyMatch(s -> s.equals(key));
    }

    private boolean isHideReservedWordStage(KeyEvent event) {
        if(!reservedWordStage.isShowing()) {
            return false;
        }
        return (event.isAltDown() || event.isControlDown() || !isTextInput(event.getCode()));
    }

    private static final KeyCode[] SELECT_NEXT_EMPTY_LINE_CODES = new KeyCode[] {
            KeyCode.UP, KeyCode.DOWN,
    };
    private boolean isSelectNextEmptyLine(KeyEvent event) {
        return event.isShiftDown() && event.isControlDown()
                && Arrays.stream(SELECT_NEXT_EMPTY_LINE_CODES).anyMatch(c -> c == event.getCode());
    }

    protected int getNextEmptyLineCaretPosition(String text, int caret, int direction) {
        // すでにインデックスを超えていたら抜ける
        if (caret+direction<0 || caret+direction>=text.length()) {
            return caret;
        }

        int nextCaret = caret+direction;
        char lastCh = text.charAt(caret);

        // 現在位置が改行なら1文字進める
        if (lastCh=='\n') {
            lastCh = text.charAt(nextCaret);
            nextCaret = nextCaret+direction;
        }

        for (; nextCaret<text.length()&&nextCaret>0; nextCaret+=direction) {
            if (lastCh=='\n' && text.charAt(nextCaret)=='\n') {
                if (direction<=-1) {
                    // "\n\n"の2文字分先行しているので、2文字戻す
                    nextCaret -= (direction*2);
                }
                break;
            }
            lastCh = text.charAt(nextCaret);
        }

        return nextCaret;
    }


    private static final Character[] SPACE_INPUT_CHARS = new Character[] {
        ' ', '\t', '\n', '　', '.',
    };
    private boolean isSpaceInput(char ch) {
        return Arrays.stream(SPACE_INPUT_CHARS).anyMatch(c -> c == ch);
    }

    private static final KeyCode[] TEXT_INPUT_CODES = new KeyCode[] {
            KeyCode.A, KeyCode.B, KeyCode.C, KeyCode.D, KeyCode.E, KeyCode.F, KeyCode.G, KeyCode.H, KeyCode.I, KeyCode.J, KeyCode.K, KeyCode.L, KeyCode.M,
            KeyCode.N, KeyCode.O, KeyCode.P, KeyCode.Q, KeyCode.R, KeyCode.S, KeyCode.T, KeyCode.U, KeyCode.V, KeyCode.W, KeyCode.X, KeyCode.Y, KeyCode.Z,
            KeyCode.NUMPAD0, KeyCode.NUMPAD1, KeyCode.NUMPAD2, KeyCode.NUMPAD3, KeyCode.NUMPAD4,
            KeyCode.NUMPAD5, KeyCode.NUMPAD6, KeyCode.NUMPAD7, KeyCode.NUMPAD8, KeyCode.NUMPAD9,
            KeyCode.DIGIT0, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3, KeyCode.DIGIT4,
            KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8, KeyCode.DIGIT9,
            KeyCode.PLUS, KeyCode.MINUS, KeyCode.SLASH, KeyCode.ASTERISK,
            KeyCode.BACK_SLASH, KeyCode.BACK_SPACE, KeyCode.OPEN_BRACKET, KeyCode.CLOSE_BRACKET,KeyCode.AT,
            KeyCode.SEMICOLON, KeyCode.COLON, KeyCode.PERIOD
    };
    private boolean isTextInput(KeyCode code) {
        return Arrays.stream(TEXT_INPUT_CODES).anyMatch(c -> c == code);
    }


    /***************************************************************************
     *                                                                         *
     * Event handler                                                           *
     *                                                                         *
     **************************************************************************/

    ////////////////////////////////////////////////////////////////////////////
    // menu action

    @FXML
    private void onClose(ActionEvent event) {
        closeConnection();
    }

    @FXML
    private void onUndo(ActionEvent event) {
        // TODO: TextAreaのデフォルトの動作を更新したい・・・
    }

    @FXML
    private void onRedo(ActionEvent event) {
        // TODO: TextAreaのデフォルトの動作を更新したい・・・
    }

    @FXML
    private void onCopy(ActionEvent event) {
        // TODO: SQL実行結果の表をクリップボードにコピーする
    }

    @FXML
    private void onSettingSqlEditor(ActionEvent event) {
        editorChooserController.setContents(appConfigEditor.getEditorPath());
        editorChooserStage.showAndWait();

        if (editorChooserController.isOk()) {
            appConfigEditor.setEditorPath(editorChooserController.getEditorPath());
        }
    }

    @FXML
    private void onCallSqlEditor(ActionEvent event) {
        sqlEditorLaunchService.restart();
    }

    @FXML
    private void onConnect(ActionEvent event) {
        showConnect();
    }

    @FXML
    private void onDisconnect(ActionEvent event) {
        closeConnection();
    }

    @FXML
    private void onExecuteQuery(ActionEvent event) {
        queryExecuteService.restart();
    }

    @FXML
    private void onPasteAndExecuteQuery(ActionEvent event) {
        // TODO: クリップボードの内容をクエリ入力欄に貼り付けると同時に、貼り付けた内容のSQLを実行する
    }

    @FXML
    private void onCancelQuery(ActionEvent event) {
        if (!isConnect()) {
            return ;
        }
        queryExecuteService.cancel();
    }

    @FXML
    private void onQueryScript(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select SQL script");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Query script", "*.sql"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text file", "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files", "*.*"));

        File selectedFile = fileChooser.showOpenDialog(queryTextArea.getScene().getWindow());
        if (selectedFile == null) {
            return;
        }

        try {
            List<String> allLines = Files.readAllLines(selectedFile.toPath());
            StringBuilder stringBuilder = new StringBuilder();
            allLines.stream().forEach(e -> stringBuilder.append(e).append("\n"));
            queryScript = stringBuilder.toString();
            queryExecuteService.restart();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCommit(ActionEvent event) {
        if (!isConnect()) {
            return ;
        }
        try {
            connection.commit();
            writeLog("Commit success.");
        } catch(Exception e) {
            writeLog(e);
        }
    }

    @FXML
    private void onRollback(ActionEvent event) {
        if (!isConnect()) {
            return ;
        }
        try {
            connection.rollback();
            writeLog("Rollback success.");
        } catch(Exception e) {
            writeLog(e);
        }
    }

    private static final Map<Integer, String> ISOLATIONS = new HashMap<>();
    static {
        ISOLATIONS.put(Connection.TRANSACTION_READ_UNCOMMITTED, "UNCOMMITTED");
        ISOLATIONS.put(Connection.TRANSACTION_READ_COMMITTED, "READ_COMMITTED");
        ISOLATIONS.put(Connection.TRANSACTION_REPEATABLE_READ, "REPEATABLE_READ");
        ISOLATIONS.put(Connection.TRANSACTION_SERIALIZABLE, "SERIALIZABLE");
        ISOLATIONS.put(Connection.TRANSACTION_NONE, "NONE");
    }
    @FXML
    private void onCheckIsolation(ActionEvent event) throws SQLException {
        if (!isConnect()) {
            return ;
        }
        writeLog("Transaction isolation: %s", ISOLATIONS.get(connection.getTransactionIsolation()));
    }

    @FXML
    private void onSearchButton(ActionEvent event) {
        if (!isConnect()) {
            return ;
        }
        dbStructureUpdateService.restart();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Table structure event

    @FXML
    private void onTableIndexNameComboBox(ActionEvent event) {
        int index = tableIndexNameComboBox.getSelectionModel().getSelectedIndex();
        if (index==-1) {
            tableIndexPrimaryKeyTextField.setText("");
            tableIndexUniqueKeyTextField.setText("");
            tableIndexListView.getItems().clear();
            return;
        }

        List<TableIndexTab> list = tableIndexNameComboBox.getItems();
        TableIndexTab tableIndexTab = list.get(index);

        tableIndexPrimaryKeyTextField.setText(tableIndexTab.isPrimaryKey()? "Yes": "No");
        tableIndexUniqueKeyTextField.setText(tableIndexTab.isUniqueKey()? "Yes": "No");
        tableIndexListView.getItems().clear();
        tableIndexListView.getItems().addAll(tableIndexTab.getColumnList());
    }

    ////////////////////////////////////////////////////////////////////////////
    // queryTextArea event

    @FXML
    public void onQueryTextAreaKeyPressed(KeyEvent event) {
        // 予約語ウィンドウにフォーカス移動
        if (isChangeFocusForReservedWordStage(event.getCode())) {
            event.consume();
            reservedWordStage.requestFocus();
            return;
        }

        // 予約語ウィンドウを非表示
        if (isHideReservedWordStage(event)) {
            reservedWordStage.hide();
        }

        // 次の空行までを選択
        if (isSelectNextEmptyLine(event)) {
            int anchor = queryTextArea.getAnchor();
            int caret = queryTextArea.getCaretPosition();
            int direction = event.getCode()==KeyCode.UP? -1: 1;     // 上キーならマイナス方向、下ならプラス方向
            queryTextArea.selectRange(anchor, getNextEmptyLineCaretPosition(queryTextArea.getText(), caret, direction)-direction);
        }
    }

    @FXML
    public void onQueryTextAreaKeyReleased(KeyEvent event) {
    }

    @FXML
    public void onQueryTextAreaKeyTyped(KeyEvent event) {
        if (isChangeFocusForReservedWordStage(event.getCharacter())) {
            return;
        }

        String text = queryTextArea.getText();
        int caret = queryTextArea.getCaretPosition();
        String inputText = event.getCharacter();
        String inputKeyword = inputWord(text, caret, inputText);       // キャレットより前の単語を取得

        if (reservedWordController.isInputReservedWord(event, inputKeyword)) {
            // キャレット位置に選択画面を出す
            InputMethodRequests imr = queryTextArea.getInputMethodRequests();
            reservedWordStage.setX(imr.getTextLocation(0).getX());
            reservedWordStage.setY(imr.getTextLocation(0).getY());
            reservedWordStage.show();
            primaryStage.requestFocus();    // フォーカスは移動させない
        } else {
            reservedWordStage.hide();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // MainWindow event

    private class MainWindowShownHandler implements EventHandler<WindowEvent> {
        private MainController controller;
        public MainWindowShownHandler(MainController controller) {
            this.controller = controller;

        }

        @Override
        public void handle(WindowEvent event) {
            try {
                // アプリケーション設定を読み込む
                AppConfigMapper mapper = new AppConfigMapper();
                List<AppConfig> selectList = mapper.selectAll();

                for (AppConfig c: selectList) {
                    // メイン画面の配置
                    if (c instanceof AppConfigMainStage) {
                        AppConfigMainStage appConfigMainStage = (AppConfigMainStage)c;
                        controller.primaryStage.setMaximized(appConfigMainStage.isMaximized());
                        controller.primaryStage.setX(appConfigMainStage.getX());
                        controller.primaryStage.setY(appConfigMainStage.getY());
                        controller.primaryStage.setWidth(appConfigMainStage.getWidth());
                        controller.primaryStage.setHeight(appConfigMainStage.getHeight());
                        controller.primarySplitPane.setDividerPosition(0, appConfigMainStage.getPrimaryDividerPosition());
                        controller.leftSplitPane.setDividerPosition(0, appConfigMainStage.getLeftDividerPosition());
                        controller.rightSplitPane.setDividerPosition(0, appConfigMainStage.getRightDivider1Position());
                        controller.rightSplitPane.setDividerPosition(1, appConfigMainStage.getRightDivider2Position());

                    }

                    // エビデンスモードの復元
                    if (c instanceof AppConfigEvidenceMode) {
                        AppConfigEvidenceMode appConfigEvidenceMode = (AppConfigEvidenceMode)c;
                        controller.evidenceMode.setSelected(appConfigEvidenceMode.isEvidenceMode());
                        controller.evidenceModeIncludeHeader.setSelected(appConfigEvidenceMode.isIncludeHeader());
                        controller.evidenceDelimiter.getToggles().get(appConfigEvidenceMode.getEvidenceDelimiter()).setSelected(true);

                    }

                    // エディタパスの復元
                    if (c instanceof AppConfigEditor) {
                        AppConfigEditor appConfigEditor = (AppConfigEditor)c;
                        controller.appConfigEditor.setEditorPath(appConfigEditor.getEditorPath());
                    }
                }

                // 作業中クエリの復元
                WorkingQuerySerializer workingQuerySerializer = new WorkingQuerySerializer();
                String workingQuery = workingQuerySerializer.readText().trim();
                controller.queryTextArea.setText(workingQuery);
                controller.queryTextArea.positionCaret(workingQuery.length());

                // DB接続画面を表示
                controller.showConnect();
            } catch (IOException e) {
                writeLog(e);
            }
        }
    }

    private class MainWindowCloseRequestHandler implements EventHandler<WindowEvent> {
        private MainController controller;
        public MainWindowCloseRequestHandler(MainController controller) {
            this.controller = controller;

        }

        @Override
        public void handle(WindowEvent event) {
            // DB切断
            controller.closeConnection();

            try {
                // アプリケーション設定を書き込む
                WorkingQuerySerializer workingQuerySerializer = new WorkingQuerySerializer();
                workingQuerySerializer.updateText(controller.queryTextArea.getText());

                AppConfigMapper mapper = new AppConfigMapper();
                List<AppConfig> list = new ArrayList<>();

                // メイン画面の配置を保存
                AppConfigMainStage appConfigMainStage = new AppConfigMainStage();
                appConfigMainStage.setMaximized(controller.primaryStage.isMaximized());
                appConfigMainStage.setX(controller.primaryStage.getX());
                appConfigMainStage.setY(controller.primaryStage.getY());
                appConfigMainStage.setWidth(controller.primaryStage.getWidth());
                appConfigMainStage.setHeight(controller.primaryStage.getHeight());
                appConfigMainStage.setPrimaryDividerPosition(controller.primarySplitPane.getDividerPositions()[0]);
                appConfigMainStage.setLeftDividerPosition(controller.leftSplitPane.getDividerPositions()[0]);
                appConfigMainStage.setRightDivider1Position(controller.rightSplitPane.getDividerPositions()[0]);
                appConfigMainStage.setRightDivider2Position(controller.rightSplitPane.getDividerPositions()[1]);
                list.add(appConfigMainStage);

                // エビデンスモードの保存
                AppConfigEvidenceMode appConfigEvidenceMode = new AppConfigEvidenceMode();
                appConfigEvidenceMode.setEvidenceMode(controller.evidenceMode.isSelected());
                appConfigEvidenceMode.setIncludeHeader(controller.evidenceModeIncludeHeader.isSelected());
                int selectedIndex = 0;
                for (Toggle toggle: controller.evidenceDelimiter.getToggles()) {
                    if (toggle.isSelected()) {
                        break;
                    }
                    selectedIndex++;
                }
                appConfigEvidenceMode.setEvidenceDelimiter(selectedIndex);
                list.add(appConfigEvidenceMode);

                // エディタパスの保存
                list.add(controller.appConfigEditor);

                mapper.save(list);

            } catch (IOException e) {
                writeLog(e);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // queryResultTableView event
    private void addQueryWordEvent(MouseEvent event) {
        // 右ダブルクリック以外は終了
        if ((event.getButton()!=MouseButton.SECONDARY) || (event.getClickCount()!=2)) {
            return ;
        }

        Node node = (Node)event.getSource();
        EventTarget target = event.getTarget();
        String columnName = null;

        // dbStructureTreeViewの場合、TABLE（テーブル・ビュー・シノニムなど）かFUNCTIONかPROCEDUREのみ許可
        if (dbStructureTreeView.getId().equals(node.getId())) {
            Text text = (Text)event.getTarget();
            TreeCell treeCell = (TreeCell)text.getParent();
            DbStructureTreeItem item = (DbStructureTreeItem)treeCell.getTreeItem();
            switch(item.getItemType()) {
                case DATABASE:
                case GROUP:
                case SCHEMA:
                    return;

                case TABLE:
                case FUNCTION:
                case PROCEDURE:
                    columnName = item.getValue();
                default:
            }
        }

        // TableView・ListViewの場合
        if (node instanceof TableView || node instanceof ListView) {
            // Textなら親のLabelを取得
            //  カラムを縮めて「Test」が「Te...」となったとき、Textから値を取ると「Te...」になる。
            //  親のLabelから値を取ると、ちゃんと縮める前の「Test」が取れる。
            if (target instanceof Text) {
                target = ((Node)target).getParent();    // Labelが取れる
            }

            if (target instanceof Labeled) {
                Labeled labeled = (Labeled) target;
                columnName = labeled.getText();

            } else if (target instanceof TableColumn) {
                TableColumn tableColumn = (TableColumn)target;
                columnName = tableColumn.getText();

            }
        }

        if (columnName==null) {
            return ;
        }

        addQueryWord(columnName, event.isShiftDown());
    }

    /***************************************************************************
     *                                                                         *
     * MainControllerInterface implementation                                  *
     *                                                                         *
     **************************************************************************/

}
