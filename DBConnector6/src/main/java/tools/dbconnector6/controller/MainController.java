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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.entity.*;
import tools.dbconnector6.mapper.AppConfigMapper;
import tools.dbconnector6.queryresult.QueryResult;
import tools.dbconnector6.serializer.ApplicationLogSerializer;
import tools.dbconnector6.serializer.WorkingQuerySerializer;
import tools.dbconnector6.service.*;
import tools.dbconnector6.transfer.ResultDataTransfer;
import tools.dbconnector6.transfer.ResultDataTransferClipboard;
import tools.dbconnector6.util.QueryScriptReader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static tools.dbconnector6.controller.DbStructureTreeItem.ItemType.DATABASE;

/**
 * DBConnector6のメイン画面コントローラ
 */
public class MainController extends Application implements Initializable, MainControllerInterface {
    // 起動時のメインステージ（自分自身）
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
    @FXML private TextArea queryTextArea;               // メイン画面右側の、上に表示するクエリ入力欄
    @FXML private TableView queryResultTableView;       // メイン画面右側の、中央に表示するクエリ実行結果表示欄
    @FXML private TextArea logTextArea;                 // メイン画面右側の、下に表示するログ出力エリア

    // DB structure                                     メイン画面左上のデータベース構造を表示するエリア
    // +------------------------------------+
    // | xxx filterTextField [searchButton] |
    // | +--------------------------------+ |
    // | | dbStructureTreeView            | |
    // | |                                | |
    // | |                                | |
    // | +--------------------------------+ |
    // +------------------------------------+
    @FXML private TextField filterTextField;            // フィルタ文字列の入力欄
    @FXML private TreeView dbStructureTreeView;         // データベース構造を表示するTreeView
    private DbStructureTreeItem dbStructureRootItem;    // データベース構造を表示するTreeViewのルート要素

    // Table structure overview                         メイン画面左下のテーブル構造を表示するエリア（データベース構造で選択したテーブル等の構造を表示する）
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
    @FXML private TabPane tableStructureTabPane;        // タブコントロールペイン
    @FXML private Tab tablePropertyTab;                 // テーブルのプロパティを表示するタブ
    @FXML private Tab tableColumnTab;                   // テーブルのカラム一覧を表示するタブ
    @FXML private Tab tableIndexTab;                    // テーブルのインデックスを表示するタブ

    // tablePropertyTab                                 メイン画面左下のテーブル構造のうち、テーブルのプロパティを表示するタブ
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
    @FXML private TableView tablePropertyTableView;     // テーブルのプロパティを表示するTableView
    @FXML private TableColumn<TablePropertyTab, String> keyTableColumn;
    @FXML private TableColumn<TablePropertyTab, String> valueTableColumn;

    // tablePropertyTab                                 メイン画面左下のテーブル構造のうち、テーブルのカラム一覧を表示するタブ
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
    @FXML private TableView tableColumnTableView;       // テーブルのカラム一覧を表示するTableView
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

    // tableIndexTab                                    メイン画面左下のテーブル構造のうち、テーブルのインデックスを表示するタブ
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
    @FXML private ComboBox tableIndexNameComboBox;          // 表示するインデックスを選択するComboBox
    @FXML private TextField tableIndexPrimaryKeyTextField;  // 選択したインデックスがプライマリキーかを表す
    @FXML private TextField tableIndexUniqueKeyTextField;   // 選択したインデックスがユニークキーかを表す
    @FXML private ListView tableIndexListView;              // インデックスに使用するカラムの一覧

    // other UI
    @FXML private SplitPane primarySplitPane;               // メイン画面を左右に分けるペイン
    @FXML private SplitPane leftSplitPane;                  // メイン画面左側を上下に分けるペイン
    @FXML private SplitPane rightSplitPane;                 // メイン画面右側を上下に分けるペイン
    @FXML private CheckMenuItem evidenceMode;               // メニュー「Evidence mode > Evidence mode」のチェック状態
    @FXML private CheckMenuItem evidenceModeIncludeHeader;  // メニュー「Evidence mode > Include header」のチェック状態
    @FXML private ToggleGroup evidenceDelimiter;            // メニュー「Evidence mode > Evidence delimiter」の選択状態

    // stage & controller
    private StageAndControllerPair<ConnectController> connectPair;              // 接続するデータベースを選択・登録するステージとコントローラ
    private StageAndControllerPair<AlertController> alertDialogPair;            // 警告を表示するステージとコントローラ
    private StageAndControllerPair<EditorChooserController> editorChooserPair;  // エディタ選択を表示するステージとコントローラ
    private StageAndControllerPair<ReservedWordController> reservedWordPair;    // 予約語を表示するステージとコントローラ

    private Set<ReservedWord> reservedWordList = new HashSet<>();               // 予約語を格納する一覧
    private AppConfigEditor appConfigEditor = new AppConfigEditor();            // アプリケーション設定内容の永続化クラス

    // background service
    private BackgroundService dbStructureUpdateService;                         // メイン画面左上のデータベース構造を更新するサービス
    private BackgroundService tableStructureTabPaneUpdateService;               // メイン画面左下のタブを状態するサービス
    private BackgroundService tableStructureUpdateService;                      // メイン画面左下のテーブル構造を更新するサービス
    private BackgroundService queryExecuteService;                              // クエリを実行するサービス
    private BackgroundService reservedWordUpdateService;                        // 予約語一覧を更新するサービス
    private BackgroundService sqlEditorLaunchService;                           // SQLエディタを起動するサービス

    // other field
    private Connect connectParam;                                               // データベース接続パラメータ
    private String queryScript = null;                                          // ファイル選択ダイアログで選択したクエリスクリプトファイル（SQLバッチ実行スクリプト）

    /**
     * JavaFXアプリのエントリポイント。<br>
     * FXMLの読み込みとコントローラの初期化を行い、メイン画面を表示する。<br>
     * @param primaryStage アプリケーション・シーンを設定できる、このアプリケーションのプライマリ・ステージ
     * @throws Exception 何らかの例外が発生した場合
     */
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

    // クエリ入力欄にフォーカスを移動する
    private void focusQueryTextArea() {
        queryTextArea.requestFocus();
    }

    /**
     * コントローラのルート要素が完全に処理された後に、コントローラを初期化するためにコールされます。<br>
     * @param location ルート・オブジェクトの相対パスの解決に使用される場所、または場所が不明の場合は、null
     * @param resources ート・オブジェクトのローカライズに使用されるリソース、
     *                  またはルート・オブジェクトがローカライズされていない場合は、null。
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // データベース構造のルート要素を関連付け
        dbStructureRootItem = new DbStructureTreeItem(DATABASE, DATABASE.getName(), null);
        dbStructureTreeView.setRoot(dbStructureRootItem);
        dbStructureTreeView.getSelectionModel().selectedItemProperty().addListener(new DbStructureTreeViewChangeListener());

        // サービスの作成
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

        // 初期化のために一度サービスを実行する
        dbStructureUpdateService.restart();
        tableStructureTabPaneUpdateService.restart();
        tableStructureUpdateService.restart();

        queryResultTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Controllerの読み込み
        try {
            connectPair = SubController.createStageAndControllerPair("connect", this);
            reservedWordPair = SubController.createTransparentStageAndControllerPair("reservedWord", this);
            alertDialogPair = SubController.createStageAndControllerPair("alertDialog", this);
            editorChooserPair = SubController.createStageAndControllerPair("editorChooser", this);

            reservedWordPair.controller.setReservedWordList(reservedWordList);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // データベース接続画面を表示する
    private void showConnect() {
        closeConnection();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                connectPair.stage.showAndWait();
            }
        });
    }

    // データベース接続を閉じる
    private void closeConnection() {
        if (isConnectWithoutOutputMessage()) {
            try {
                connectParam.getConnection().close();
            } catch (SQLException e) {
                writeLog(e);
            }
            connectParam = null;
            writeLog("Disconnected.");
        }
        dbStructureUpdateService.restart();
        reservedWordUpdateService.restart();
    }

    /**
     * クエリ入力欄で、現在のキャレット位置の左側にある単語を返す。<br>
     * 「test input」の文字のpとuの間にキャレットがあった場合、返す文字列は「inp」となる。<br>
     * @param text クエリ入力欄の入力内容
     * @param caret 現在のキャレット位置
     * @param inputCharacter キーイベントで発生した入力文字列
     * @return 現在のキャレット位置の左側にある単語
     */
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

    // クエリ入力中に入力したキーが予約語一覧へのフォーカス移動のキーかを判定する
    // 予約語一覧が表示されていて、かつ入力したキーが「TAB」か「DOWN」キーの場合はtrue
    private boolean isChangeFocusForReservedWordStage(KeyCode code) {
        if(!reservedWordPair.stage.isShowing()) {
            return false;
        }
        return Arrays.stream(CHANGE_FOCUS_FOR_RESERVED_WORD_STAGE_CODES).anyMatch(c -> c == code);
    }
    private static final KeyCode[] CHANGE_FOCUS_FOR_RESERVED_WORD_STAGE_CODES = new KeyCode[] {
            KeyCode.TAB, KeyCode.DOWN,
    };

    // クエリ入力中に入力したキーが予約語一覧へのフォーカス移動の文字入力かを判定する
    // 予約語一覧が表示されていて、かつ入力した文字が「TAB」の場合はtrue
    private boolean isChangeFocusForReservedWordStage(String key) {
        if(!reservedWordPair.stage.isShowing()) {
            return false;
        }
        return Arrays.stream(CHANGE_FOCUS_FOR_RESERVED_WORD_STAGE_STRING).anyMatch(s -> s.equals(key));
    }
    private static final String[] CHANGE_FOCUS_FOR_RESERVED_WORD_STAGE_STRING = new String[] {
            "\t"
    };

    // クエリ入力中に入力したキーが予約語一覧を非表示にするキーかを判定する
    // 予約語一覧が表示されていて、かつ入力したキーが「ALT」か「Ctrl」か文字以外の場合はtrue
    private boolean isHideReservedWordStage(KeyEvent event) {
        if(!reservedWordPair.stage.isShowing()) {
            return false;
        }
        return (event.isAltDown() || event.isControlDown() || !isTextInput(event.getCode()));
    }

    // クエリ入力中に入力したキーが次もしくは前の空行までを一括選択するキーかを判定する。
    // クエリ入力中に「Shift」と「Ctrl」と「UP」もしくは「DOWN」を押した場合はtrue
    private boolean isSelectNextEmptyLine(KeyEvent event) {
        return event.isShiftDown() && event.isControlDown()
                && Arrays.stream(SELECT_NEXT_EMPTY_LINE_CODES).anyMatch(c -> c == event.getCode());
    }
    private static final KeyCode[] SELECT_NEXT_EMPTY_LINE_CODES = new KeyCode[] {
            KeyCode.UP, KeyCode.DOWN,
    };

    /**
     * クエリ入力欄のキャレット位置の、次もしくは前の空行までのキャレット位置を返す<br>
     * @param text クエリ入力欄の入力内容
     * @param caret 現在のキャレット位置
     * @param direction キャレット移動方向。キャレット位置より前に移動する場合は-1、後ろに移動する場合は1を設定
     * @return 移動先のキャレット位置
     */
    protected int getNextEmptyLineCaretPosition(String text, int caret, int direction) {
        // すでにインデックスを超えていたら抜ける
        if (caret+direction<0 || caret+direction>=text.length()) {
            return caret;
        }

        int nextCaret = caret+direction;
        char lastCh = (text.length()>=caret? ' ': text.charAt(caret));  // 末尾にキャレットがある場合に配列範囲外で例外が発生するので回避（仮の値でスペースを使用）

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


    // 空白文字かを判定する。
    // 空白文字は、半角空白・タブ文字・改行・全角空白・ピリオドがある。
    private boolean isSpaceInput(char ch) {
        return Arrays.stream(SPACE_INPUT_CHARS).anyMatch(c -> c == ch);
    }
    private static final Character[] SPACE_INPUT_CHARS = new Character[] {
            ' ', '\t', '\n', '　', '.',
    };

    // 文字の入力かを判定する。
    private boolean isTextInput(KeyCode code) {
        return Arrays.stream(TEXT_INPUT_CODES).anyMatch(c -> c == code);
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


    /***************************************************************************
     *                                                                         *
     * Event handler                                                           *
     *                                                                         *
     **************************************************************************/

    ////////////////////////////////////////////////////////////////////////////
    // menu action

    // メニュー「File > Close」のアクションイベントハンドラ
    // データベース接続を解除してアプリケーションを終了する
    @FXML
    private void onClose(ActionEvent event) {
        closeConnection();
        Platform.exit();
    }

    // メニュー「Edit > Undo」のアクションイベントハンドラ
    @FXML
    private void onUndo(ActionEvent event) {
        // TODO: TextAreaのデフォルトの動作を更新したい・・・
    }

    // メニュー「Edit > Redo」のアクションイベントハンドラ
    @FXML
    private void onRedo(ActionEvent event) {
        // TODO: TextAreaのデフォルトの動作を更新したい・・・
    }

    // メニュー「Edit > Copy」のアクションイベントハンドラ
    // 実行結果一覧にフォーカスがある場合、一覧の選択している内容をクリップボードにコピーする
    @FXML
    private void onCopy(ActionEvent event) {
        if (!queryResultTableView.isFocused()) {
            return ;
        }

        ResultDataTransfer resultDataTransfer = new ResultDataTransferClipboard(isEvidenceMode(), isEvidenceModeIncludeHeader(), getEvidenceDelimiter());
        resultDataTransfer.setHeader(queryResultTableView.getColumns());
        List<QueryResult> list = queryResultTableView.getSelectionModel().getSelectedItems();
        list.stream().forEach(item -> resultDataTransfer.addData(item.getRecordData()));
        resultDataTransfer.transfer();
        event.consume();
    }

    // メニュー「Edit > Setting SQL editor」のアクションイベントハンドラ
    // SQLエディタの設定画面を表示する
    @FXML
    private void onSettingSqlEditor(ActionEvent event) {
        editorChooserPair.controller.setEditorPath(appConfigEditor.getEditorPath());
        editorChooserPair.stage.showAndWait();

        if (editorChooserPair.controller.isOk()) {
            appConfigEditor.setEditorPath(editorChooserPair.controller.getEditorPath());
        }
    }

    // メニュー「Edit > Call SQL editor」のアクションイベントハンドラ
    // SQLエディタを呼び出し、編集中のクエリをSQLエディタで編集させる
    @FXML
    private void onCallSqlEditor(ActionEvent event) {
        sqlEditorLaunchService.restart();
    }

    // メニュー「Database > Connect」のアクションイベントハンドラ
    // データベース接続画面を表示する
    @FXML
    private void onConnect(ActionEvent event) {
        showConnect();
    }

    // メニュー「Database > Disconnect」のアクションイベントハンドラ
    // データベース接続を解除する
    @FXML
    private void onDisconnect(ActionEvent event) {
        closeConnection();
    }

    // メニュー「Database > Execute query」のアクションイベントハンドラ
    // クエリを実行する
    @FXML
    private void onExecuteQuery(ActionEvent event) {
        queryExecuteService.restart();
    }

    // メニュー「Database > Paste & Execute query」のアクションイベントハンドラ
    @FXML
    private void onPasteAndExecuteQuery(ActionEvent event) {
        // TODO: クリップボードの内容をクエリ入力欄に貼り付けると同時に、貼り付けた内容のSQLを実行する
    }

    // メニュー「Database > Cancel query」のアクションイベントハンドラ
    // 実行中のクエリを実行解除する
    @FXML
    private void onCancelQuery(ActionEvent event) {
        if (!isConnect()) {
            return ;
        }
        queryExecuteService.cancel();
    }

    // メニュー「Database > Query script」のアクションイベントハンドラ
    // クエリスクリプトファイルを選択し、実行する
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
            List<String> allLines = QueryScriptReader.readAllLines(selectedFile.toPath());
            StringBuilder stringBuilder = new StringBuilder();
            allLines.stream().forEach(e -> stringBuilder.append(e).append("\n"));
            queryScript = stringBuilder.toString();
            queryExecuteService.restart();

        } catch (IOException e) {
            writeLog(e);
        }
    }

    // メニュー「Database > Commit」のアクションイベントハンドラ
    // データベースにコミットを送信する
    @FXML
    private void onCommit(ActionEvent event) {
        if (!isConnect()) {
            return ;
        }
        try {
            connectParam.getConnection().commit();
            writeLog("Commit success.");
        } catch(Exception e) {
            writeLog(e);
        }
    }

    // メニュー「Database > Rollback」のアクションイベントハンドラ
    // データベースにロールバックを送信する
    @FXML
    private void onRollback(ActionEvent event) {
        if (!isConnect()) {
            return ;
        }
        try {
            connectParam.getConnection().rollback();
            writeLog("Rollback success.");
        } catch(Exception e) {
            writeLog(e);
        }
    }

    // メニュー「Database > Isolation」のアクションイベントハンドラ
    // 現在のトランザクション分離レベルをログエリアに出力する
    @FXML
    private void onCheckIsolation(ActionEvent event) throws SQLException {
        if (!isConnect()) {
            return ;
        }
        writeLog("Transaction isolation: %s", ISOLATIONS.get(connectParam.getConnection().getTransactionIsolation()));
    }
    private static final Map<Integer, String> ISOLATIONS = new HashMap<>();
    static {
        ISOLATIONS.put(Connection.TRANSACTION_READ_UNCOMMITTED, "UNCOMMITTED");
        ISOLATIONS.put(Connection.TRANSACTION_READ_COMMITTED, "READ_COMMITTED");
        ISOLATIONS.put(Connection.TRANSACTION_REPEATABLE_READ, "REPEATABLE_READ");
        ISOLATIONS.put(Connection.TRANSACTION_SERIALIZABLE, "SERIALIZABLE");
        ISOLATIONS.put(Connection.TRANSACTION_NONE, "NONE");
    }

    ////////////////////////////////////////////////////////////////////////////
    // DB structure event

    // メイン画面左上のデータベース構造の選択変更イベントリスナ
    private class DbStructureTreeViewChangeListener implements ChangeListener {
        /**
         * 選択変更イベント。<br>
         * メイン画面左下のテーブル構造の更新を実行する
         * @param observable 値が変更されたObservableValue
         * @param oldValue   古い値
         * @param newValue   新しい値
         */
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            tableStructureTabPaneUpdateService.restart();
        }
    }

    // メイン画面左上のSearchボタンのアクションイベントハンドラ
    // メイン画面左上のデータベース構造の更新を行う
    @FXML
    private void onSearchButton(ActionEvent event) {
        if (!isConnect()) {
            return ;
        }
        dbStructureUpdateService.restart();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Table structure event

    // メイン画面左下のテーブル構造の、テーブルのインデックスを表示するコンボボックスを変更した時のアクションイベント
    // 選択したインデックスの内容でタブ内の項目を更新する
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

    // クエリ入力欄でのキー押下イベントハンドラ
    // 予約語画面へのフォーカス移動や行選択の処理を行う
    @FXML
    private void onQueryTextAreaKeyPressed(KeyEvent event) {
        // 予約語ウィンドウにフォーカス移動
        if (isChangeFocusForReservedWordStage(event.getCode())) {
            event.consume();
            reservedWordPair.stage.requestFocus();
            return;
        }

        // 予約語ウィンドウを非表示
        if (isHideReservedWordStage(event)) {
            reservedWordPair.stage.hide();
        }

        // 次の空行までを選択
        if (isSelectNextEmptyLine(event)) {
            int anchor = queryTextArea.getAnchor();
            int caret = queryTextArea.getCaretPosition();
            int direction = event.getCode()==KeyCode.UP? -1: 1;     // 上キーならマイナス方向、下ならプラス方向
            queryTextArea.selectRange(anchor, getNextEmptyLineCaretPosition(queryTextArea.getText(), caret, direction)-direction);
        }
    }

    // クエリ入力欄でのキー入力イベントハンドラ
    // 予約語画面へのフォーカス移動や予約語画面の表示制御を行う
    @FXML
    private void onQueryTextAreaKeyTyped(KeyEvent event) {
        if (isChangeFocusForReservedWordStage(event.getCharacter())) {
            return;
        }

        String text = queryTextArea.getText();
        int caret = queryTextArea.getCaretPosition();
        String inputText = event.getCharacter();
        String inputKeyword = inputWord(text, caret, inputText);       // キャレットより前の単語を取得

        if (reservedWordPair.controller.isInputReservedWord(event, inputKeyword)) {
            // キャレット位置に選択画面を出す
            InputMethodRequests imr = queryTextArea.getInputMethodRequests();
            reservedWordPair.stage.setX(imr.getTextLocation(0).getX());
            reservedWordPair.stage.setY(imr.getTextLocation(0).getY());
            reservedWordPair.stage.show();
            primaryStage.requestFocus();    // フォーカスは移動させない
        } else {
            reservedWordPair.stage.hide();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // MainWindow event

    // メイン画面のウィンドウ表示イベントハンドラクラス
    private class MainWindowShownHandler implements EventHandler<WindowEvent> {
        // メイン画面のコントローラ
        private MainController controller;

        /**
         * コンストラクタ
         * @param controller メイン画面のコントローラ
         */
        public MainWindowShownHandler(MainController controller) {
            this.controller = controller;

        }

        /**
         * メイン画面のウィンドウ表示イベントハンドラ。<br>
         * 設定ファイルの内容でメイン画面の表示位置を変更する。<br>
         * @param event ウィンドウの表示/非表示アクションに関連するイベント
         */
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

    // メイン画面のウィンドウクローズイベントハンドラクラス
    private class MainWindowCloseRequestHandler implements EventHandler<WindowEvent> {
        // メイン画面のコントローラ
        private MainController controller;

        /**
         * コンストラクタ
         * @param controller メイン画面のコントローラ
         */
        public MainWindowCloseRequestHandler(MainController controller) {
            this.controller = controller;

        }

        /**
         * メイン画面のウィンドウクローズイベントハンドラ。<br>
         * メイン画面の表示位置を設定ファイルに書き込む。<br>
         * @param event ウィンドウの表示/非表示アクションに関連するイベント
         */
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

    // クエリ実行結果やデータベース構造など、セカンダリボタン（右ボタン）のダブルクリック時にダブルクリックしたテキストをクエリ入力欄へ貼り付ける
    private void addQueryWordEvent(MouseEvent event) {
        // 右ダブルクリック以外は終了
        if ((event.getClickCount()!=2) || (event.getButton()!=MouseButton.SECONDARY)) {
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

    /**
     * 通常ログの出力。<br>
     * 出力はString#formatメソッドの書式文字列で指定し、ログ出力エリアに出力する。<br>
     * @param format 書式文字列
     * @param args   書式文字列の書式指示子により参照される引数
     * @see java.lang.String
     */
    @Override
    public void writeLog(String format, Object... args) {
        final String logText = LOG_DATE_FORMAT.format(new Date())+" " + String.format(format, args);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                logTextArea.appendText(logText + "\n");
            }
        });
    }
    private static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    /**
     * 例外ログの出力。<br>
     * 例外ログを、ログ出力エリアとログファイルに出力する。<br>
     * @param e 例外発生時の例外オブジェクト
     */
    @Override
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

    /**
     * 予約語選択画面の選択結果の通知。<br>
     * クエリー入力欄に現在入力中の単語を指定された単語に置き換える。<br>
     * @param word 選択結果の予約語
     */
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

    /**
     * メイン画面にフォーカスを移動させる。<br>
     */
    @Override
    public void mainControllerRequestFocus() {
        primaryStage.requestFocus();
    }

    /**
     * 予約語選択画面を閉じる
     */
    @Override
    public void hideReservedWordStage() {
        reservedWordPair.stage.hide();
    }

    /**
     * 警告画面をモーダルで表示する。<br>
     * @param message メッセージタイトル
     * @param detail  メッセージの詳細
     */
    @Override
    public void showAlertDialog(String message, String detail) {
        alertDialogPair.controller.setContents(message, detail);
        alertDialogPair.controller.setWaitMode(false);
        alertDialogPair.stage.showAndWait();
    }

    /**
     * 待機画面をモーダルで表示する。<br>
     * @param message メッセージタイトル
     * @param detail  メッセージの詳細
     */
    public void showWaitDialog(String message, String detail) {
        alertDialogPair.controller.setContents(message, detail);
        alertDialogPair.controller.setWaitMode(true);
        alertDialogPair.stage.showAndWait();
    }

    /**
     * 待機画面を閉じる。<br>
     */
    public void hideWaitDialog() {
        alertDialogPair.stage.hide();
    }

    /**
     * 現在のエビデンスモードを取得する。<br>
     * @return エビデンスモードが有効の場合は true、それ以外は false。
     */
    @Override
    public boolean isEvidenceMode() {
        return evidenceMode.isSelected();
    }

    /**
     * エビデンスにヘッダー列（カラム列）を含めるかを取得する。<br>
     * @return ヘッダー列（カラム列）を含める場合は true、それ以外は false。
     */
    @Override
    public boolean isEvidenceModeIncludeHeader() {
        return evidenceModeIncludeHeader.isSelected();
    }

    /**
     * エビデンスの列の区切り文字を取得する。<br>
     * @return 区切り文字
     */
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
    private static final String[] EVIDENCE_DELIMITERS = new String[] {"\t", ",", " "};

    /**
     * 実行対象のクエリを取得する。<br>
     * SQLスクリプトを読み込んでの実行中であれば読み込んだスクリプトの内容を返す。<br>
     * それ以外はクエリ入力欄の内容を返すが、
     * クエリ入力欄で選択中のクエリがあればその内容を、
     * 選択中のクエリが無ければクエリ入力欄全体の内容を返す。<br>
     * @return 実行対象のクエリ
     */
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

    /**
     * クエリ入力欄の現在選択中の文字列を取得する。
     * @return クエリ入力欄の現在選択中の文字列
     */
    @Override
    public String getSelectedQuery() {
        return queryTextArea.getSelectedText();
    }

    /**
     * 指定された単語でクエリ入力欄の現在選択中のテキストを置き換える。<br>
     * @param word 置き換えする単語
     */
    @Override
    public void updateSelectedQuery(String word) {
        int caret = queryTextArea.getCaretPosition();
        int anchor = queryTextArea.getAnchor();
        queryTextArea.replaceText((anchor < caret ? anchor : caret), (anchor > caret ? anchor : caret), word);    // "begin<=end" の関係でないとNG
    }

    /**
     * 指定された単語をクエリ入力欄の現在キャレット位置に挿入する、<br>
     * クエリ結果の行タイトル（カラム名）やデータベース構造一覧の項目（テーブル名）を右ダブルクリックした際に、
     * ダブルクリックしたテキストをクエリ入力欄に挿入する入力補完機能を実現する。<br>
     * シフトを押しながら右ダブルクリックした場合、挿入した単語の後にカンマを追加する。<br>
     * @param word クエリ入力欄に挿入する単語
     * @param shiftDown シフトを押しながら挿入する場合はtrue、それ以外はfalse
     */
    @Override
    public void addQueryWord(String word, boolean shiftDown) {
        updateSelectedQuery(word + (shiftDown? ", ": ""));
        queryTextArea.requestFocus();
    }

    /**
     * 現在設定されているテキストエディタへのパスを返す。<br>
     * 設定されていない場合は空文字を返す。<br>
     * @return テキストエディタへのパス
     */
    @Override
    public String getEditorPath() {
        return appConfigEditor.getEditorPath();
    }

    /**
     * データベース接続画面から、データベース接続時にメイン画面へ接続した旨の通知をする。<br>
     * 通知を受け取ったメイン画面は、データベース構造表示等の画面更新を行う。<br>
     */
    @Override
    public void connectNotify() {
        Connection con = connectPair.controller.getConnection();
        if (con!=null) {
            writeLog("Connected.");
            connectParam = connectPair.controller.getConnect();
            dbStructureUpdateService.restart();
            reservedWordUpdateService.restart();
        }
    }

    /**
     * 現在接続しているデータベースへの接続情報（ドライバ名URLやユーザ名）を取得する。<br>
     * @return データベースへの接続情報
     */
    @Override
    public Connect getConnectParam() {
        return connectParam;
    }

    /**
     * 現在接続しているデータベースへのコネクションを取得する。<br>
     * @return データベースコネクション
     */
    @Override
    public Connection getConnection() {
        return connectParam==null? null: connectParam.getConnection();
    }

    /**
     * データベース接続確認。未接続時にログエリアへメッセージを出力する。<br>
     * ログエリアへメッセージを出力しない場合はisConnectWithoutMessage()メソッドを利用する。<br>
     * @return データベース接続時は true 、それ以外は false を返す。
     * @see tools.dbconnector6.MainControllerInterface#isConnectWithoutOutputMessage
     */
    @Override
    public boolean isConnect() {
        boolean result = isConnectWithoutOutputMessage();
        if (!result) {
            writeLog("No connect.");
        }
        return result;
    }

    /**
     * データベース接続確認。未接続時にログエリアへメッセージを出力しない。
     * ログエリアへメッセージを出力する場合はisConnect()メソッドを利用する。<br>
     * @return データベース接続時は true 、それ以外は false を返す。
     * @see tools.dbconnector6.MainControllerInterface#isConnect
     */
    @Override
    public boolean isConnectWithoutOutputMessage() {
        return connectParam==null? false: connectParam.getConnection()!=null;
    }

    /**
     * TableStructureUpdateServiceクラスのrestartメソッドを呼んで更新を要求する。<br>
     */
    @Override
    public void requestTableStructureUpdate() {
        tableStructureUpdateService.restart();
    }

    /**
     * メイン画面左上のデータベース構造のUI参照をまとめた構造体を取得する。<br>
     * @return メイン画面左上のデータベース構造のUI参照をまとめた構造体
     */
    @Override
    public DbStructureParam getDbStructureParam() {
        MainControllerInterface.DbStructureParam param = new MainControllerInterface.DbStructureParam();
        param.filterTextField = filterTextField;
        param.dbStructureTreeView = dbStructureTreeView;
        param.dbStructureRootItem = dbStructureRootItem;
        return param;
    }

    /**
     * メイン画面左下のテーブル構造のUI参照をまとめた構造体を取得する。<br>
     * @return メイン画面左下のテーブル構造のUI参照をまとめた構造体
     */
    @Override
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

    /**
     * メイン画面右のクエリ入力・結果一覧のUI参照をまとめた構造体を取得する。<br>
     * @return メイン画面右のクエリ入力・結果一覧のUI参照をまとめた構造体
     */
    @Override
    public QueryParam getQueryParam() {
        MainControllerInterface.QueryParam param = new MainControllerInterface.QueryParam();

        param.queryTextArea = queryTextArea;
        param.queryResultTableView = queryResultTableView;

        return param;
    }
}
