package tools.dbconnector6.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import tools.dbconnector6.entity.Connect;
import tools.dbconnector6.entity.ConnectHistory;
import tools.dbconnector6.mapper.ConnectHistoryMapper;
import tools.dbconnector6.mapper.ConnectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.*;

/**
 * 接続先データベースの登録・選択画面用コントローラ。<br>
 */
public class ConnectController extends SubController implements Initializable {

    // Scene overview
    // +-----------------------------------------------------------------------------------------------+
    // | +----------------------+-----------------+--------------+---------------+-------------------+ |
    // | |libraryPathTableColumn|driverTableColumn|urlTableColumn|userTableColumn|passwordTableColumn| |
    // | |                                                                                           | |
    // | | connectTableView                                                                          | |
    // | +-------------------------------------------------------------------------------------------+ |
    // |                     [addButton] [updateButton] [deleteButton] [loadButton]                    |
    // |       historyComboBox                                                                         |
    // |       libraryPathTextField                                                                    |
    // |       driverTextField                                                                         |
    // |       urlTextField                                                                            |
    // |       userTextField                                                                           |
    // |       passwordTextField                                                                       |
    // |                                                        [okButton] [cancelButton] [testButton] |
    // +-----------------------------------------------------------------------------------------------+
    @FXML private TableView connectTableView;                           // 接続先一覧
    @FXML private TableColumn<Connect, String> libraryPathTableColumn;  //   列：ライブラリパス
    @FXML private TableColumn<Connect, String> driverTableColumn;       //   列：ドライバ名
    @FXML private TableColumn<Connect, String> urlTableColumn;          //   列：URL
    @FXML private TableColumn<Connect, String> userTableColumn;         //   列：ユーザー名
    @FXML private TableColumn<Connect, String> passwordTableColumn;     //   列：パスワード

    @FXML private ComboBox historyComboBox;                             // 接続履歴
    @FXML private TextField libraryPathTextField;                       // ライブラリパス入力欄
    @FXML private TextField driverTextField;                            // ドライバ名入力欄
    @FXML private TextField urlTextField;                               // URL入力欄
    @FXML private TextField userTextField;                              // ユーザー名入力欄
    @FXML private PasswordField passwordTextField;                      // パスワード入力欄

    // データベース接続した際のエンティティと接続子
    private Connect connect;

    /**
     * コントローラのルート要素が完全に処理された後に、コントローラを初期化するためにコールされます。<br>
     * @param location ルート・オブジェクトの相対パスの解決に使用される場所、または場所が不明の場合は、null
     * @param resources ート・オブジェクトのローカライズに使用されるリソース、
     *                  またはルート・オブジェクトがローカライズされていない場合は、null。
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TableColumnとエンティティの関連付け
        libraryPathTableColumn.setCellValueFactory(new PropertyValueFactory<Connect, String>("libraryPath"));
        driverTableColumn.setCellValueFactory(new PropertyValueFactory<Connect, String>("driver"));
        urlTableColumn.setCellValueFactory(new PropertyValueFactory<Connect, String>("url"));
        userTableColumn.setCellValueFactory(new PropertyValueFactory<Connect, String>("user"));
        passwordTableColumn.setCellValueFactory(new PropertyValueFactory<Connect, String>("maskedPassword"));

        connect = null;

        // 接続リストの読み込み
        try {
            ObservableList<Connect> tableList = connectTableView.getItems();
            tableList.clear();
            ConnectMapper mapper = new ConnectMapper();
            List<Connect> list = mapper.selectAll();
            tableList.addAll(list);
        } catch (IOException e) {
            mainControllerInterface.writeLog(e);
        }

        // 接続履歴の読み込み
        try {
            ObservableList<ConnectHistory> connectHistoryList = historyComboBox.getItems();
            connectHistoryList.clear();
            ConnectHistoryMapper mapper = new ConnectHistoryMapper();
            List<ConnectHistory> list = mapper.selectAll();
            Collections.reverse(list);
            connectHistoryList.addAll(list);

            historyComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener(){
                @Override public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    historyComboBoxChangeHandler(observable, oldValue, newValue);
                }
            });
            if (list.size()>=1) {
                historyComboBox.getSelectionModel().select(0);
            }

        } catch (IOException e) {
            mainControllerInterface.writeLog(e);
        }
    }

    /**
     * データベース接続エンティティを返す。<br>
     * データベースに未接続の場合は null を、接続済みの場合は接続時のエンティティと接続子（Connection）を返す。
     * @return データベースに未接続の場合は null を、接続済みの場合は接続時のエンティティと接続子（Connection）。
     */
    public Connect getConnect() {
        return connect;
    }

    /**
     * データベース接続子（Connection）を返す。<br>
     * 接続していない場合は null を返す。
     * @return データベース接続子。接続していない場合は null。
     */
    public Connection getConnection() {
        return connect==null? null: connect.getConnection();
    }

    /**
     * DB接続用に入力した入力欄の各パラメータを基にデータベース接続エンティティを作成する。<br>
     * @return データベース接続エンティティ
     */
    private Connect createConnect() {
        return  Connect.builder()
                .libraryPath(libraryPathTextField.getText().trim())
                .driver(driverTextField.getText().trim())
                .url(urlTextField.getText().trim())
                .user(userTextField.getText().trim())
                .password(passwordTextField.getText().trim())
                .connection(null)
                .build();
    }

    /**
     * DB接続用に入力した入力欄の検証を行う。<br>
     * ライブラリパス、ドライバ名、URL、ユーザ名、パスワードの全てが空欄の場合は検証NGで false を返す。
     * @return 検証OKの場合は true、それ以外はfalseを返す
     */
    private boolean validTextField() {
        if ("".equals(libraryPathTextField.getText().trim())
                && "".equals(driverTextField.getText().trim())
                && "".equals(urlTextField.getText().trim())
                && "".equals(userTextField.getText().trim())
                && "".equals(passwordTextField.getText().trim())) {
            return false;
        }
        return true;
    }

    // 現在の接続先一覧の内容を永続化する
    private void saveConnectList() {
        ConnectMapper mapper = new ConnectMapper();
        ObservableList<Connect> tableList = connectTableView.getItems();
        List<Connect> list = new ArrayList<>();
        list.addAll(tableList);
        try {
            mapper.save(list);
        } catch (IOException e) {
            mainControllerInterface.writeLog(e);
        }
    }

    // 接続先一覧の選択行の内容を、各入力欄に反映する
    private void selectConnection() {
        int index = connectTableView.getSelectionModel().getSelectedIndex();
        if (index==-1) {
            return ;
        }

        ObservableList<Connect> tableList = connectTableView.getItems();

        Connect connect = tableList.get(index);
        libraryPathTextField.setText(connect.getLibraryPath());
        driverTextField.setText(connect.getDriver());
        urlTextField.setText(connect.getUrl());
        userTextField.setText(connect.getUser());
        passwordTextField.setText(connect.getPassword());
    }

    // DB接続用に入力した各パラメータの値を使ってデータベース接続をする。
    // 接続に成功したら、その内容を接続履歴に永続化する。
    private Connection connectDatabase() throws Exception {
        if (!validTextField()) {
            return null;
        }

        Connection conn = null;
        try {
            Properties info = new Properties();

            if (!isEmptyString(userTextField.getText())) {
                info.setProperty("user", userTextField.getText());
            }
            if (!isEmptyString(passwordTextField.getText())) {
                info.setProperty("password", passwordTextField.getText());
            }

            if (!(isEmptyString(libraryPathTextField.getText()) && isEmptyString(driverTextField.getText()))) {
                // ドライバ指定あり
                URL[] lib = {new File(libraryPathTextField.getText()).toURI().toURL()};
                URLClassLoader loader = URLClassLoader.newInstance(lib);
                Class<Driver> cd = (Class<Driver>) loader.loadClass(driverTextField.getText());
                Driver driver = cd.newInstance();
                conn = driver.connect(urlTextField.getText(), info);
            } else {
                // ドライバ指定なし
//              Class.forName(entity.getDriver());
                conn = DriverManager.getConnection(urlTextField.getText(), info);
            }
            conn.setAutoCommit(false);

            // 接続に成功したら、履歴に追加する
            ConnectHistory history = ConnectHistory.builder()
                    .connectedDate(new Date())
                    .libraryPath(libraryPathTextField.getText())
                    .driver(driverTextField.getText())
                    .url(urlTextField.getText())
                    .user(userTextField.getText())
                    .password(passwordTextField.getText())
                    .build();
            ConnectHistoryMapper mapper = new ConnectHistoryMapper();
            List<ConnectHistory> list = mapper.selectAll();
            list.add(history);
            mapper.save(list);

        } catch (Throwable e) {
            mainControllerInterface.showAlertDialog("Connect failed.", e.toString());
        }

        return conn;
    }

    // 指定されたStringが空文字か判定する。
    // nullの場合も空文字と判定する。
    private boolean isEmptyString(String value) {
        return (value==null || "".equals(value));
    }

    /***************************************************************************
     *                                                                         *
     * Event handler                                                           *
     *                                                                         *
     **************************************************************************/

    ////////////////////////////////////////////////////////////////////////////
    // connectTableView event

    // 接続先一覧でのキーイベントハンドラ
    // エンター押下時に選択行の内容を入力欄に反映する
    @FXML
    private void onKeyPressConnectTableView(KeyEvent event) {
        if (event.getCode()==KeyCode.ENTER) {
            event.consume();
            selectConnection();
        }
    }

    // 接続先一覧でのマウスイベントハンドラ
    // プライマリボタン（左ボタン）のダブルクリック時に選択行の内容を入力欄に反映する
    @FXML
    private void onMouseClickConnectTableView(MouseEvent event) {
        if (event.getClickCount()>=2 && event.getButton()== MouseButton.PRIMARY) {
            selectConnection();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // edit connectTableView event

    // Addボタンのアクションイベントハンドラ
    // DB接続用に入力した各パラメータを、接続先一覧の末尾に追加する
    @FXML
    private void onAdd(ActionEvent event) {
        if (!validTextField()) {
            return ;
        }

        Connect connect = createConnect();

        ObservableList<Connect> tableList = connectTableView.getItems();
        tableList.add(connect);
        connectTableView.getSelectionModel().select(tableList.size()-1);

        saveConnectList();
    }

    // Updateボタンのアクションイベントハンドラ
    // DB接続用に入力した各パラメータを、接続先一覧の現在選択行に更新する
    @FXML
    private void onUpdate(ActionEvent event) {
        if (!validTextField()) {
            return ;
        }

        int index = connectTableView.getSelectionModel().getSelectedIndex();
        if (index==-1) {
            return ;
        }

        Connect connect = createConnect();

        ObservableList<Connect> tableList = connectTableView.getItems();
        tableList.remove(index);
        tableList.add(index, connect);
        connectTableView.getSelectionModel().select(index);

        saveConnectList();
    }

    // Deleteボタンのアクションイベントハンドラ
    // 接続先一覧の現在選択行を削除する
    @FXML
    private void onDelete(ActionEvent event) {
        int index = connectTableView.getSelectionModel().getSelectedIndex();
        if (index==-1) {
            return ;
        }

        ObservableList<Connect> tableList = connectTableView.getItems();
        tableList.remove(index);

        saveConnectList();
    }

    // Loadボタンのアクションイベントハンドラ
    // 接続先一覧の現在選択行の内容を入力欄に反映する
    @FXML
    private void onLoad(ActionEvent event) {
        selectConnection();
    }

    // 接続履歴の選択変更イベントハンドラ
    // 変更した履歴の内容を入力欄に反映する
    private void historyComboBoxChangeHandler(ObservableValue observable, Object oldValue, Object newValue) {
        int index = historyComboBox.getSelectionModel().getSelectedIndex();
        if (index==-1) {
            return;
        }

        List<ConnectHistory> list = historyComboBox.getItems();
        ConnectHistory history = list.get(index);
        libraryPathTextField.setText(history.getLibraryPath());
        driverTextField.setText(history.getDriver());
        urlTextField.setText(history.getUrl());
        userTextField.setText(history.getUser());
        passwordTextField.setText(history.getPassword());
    }

    ////////////////////////////////////////////////////////////////////////////
    // bottom button event

    // OKボタンのアクションイベントハンドラ
    // 入力内容を基にDB接続を行う
    @FXML
    private void onOk(ActionEvent event) throws Exception {
        Connection conn = connectDatabase();

        if (conn!=null) {
            this.connect = createConnect();
            this.connect.setConnection(conn);

            connectTableView.getScene().getWindow().hide();
            mainControllerInterface.connectNotify();
        }
    }

    // Cancelボタンのアクションイベントハンドラ
    // 自画面を閉じる
    @FXML
    private void onCancel(ActionEvent event) {
        this.connect = null;
        connectTableView.getScene().getWindow().hide();
    }

    // Testボタンのアクションイベントハンドラ
    // 入力内容を基にDB接続を行いすぐに接続を解除することで、接続確認を行う。
    @FXML
    private void onTest(ActionEvent event) throws Exception {
        Connection conn = connectDatabase();
        if (conn!=null) {
            mainControllerInterface.showAlertDialog("Connect success.", "");
            conn.close();
        }
    }

}
