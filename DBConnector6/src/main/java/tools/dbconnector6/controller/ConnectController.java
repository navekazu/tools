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
import tools.dbconnector6.MainControllerInterface;
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

    private Connection connection;
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

        connection = null;
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

    public Connect getConnect() {
        return connect;
    }

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

    private Connect createConnect() {
        return  Connect.builder()
                .libraryPath(libraryPathTextField.getText().trim())
                .driver(driverTextField.getText().trim())
                .url(urlTextField.getText().trim())
                .user(userTextField.getText().trim())
                .password(passwordTextField.getText().trim())
                .build();
    }

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

    @FXML
    private void onKeyPressConnectTableView(KeyEvent event) {
        if (event.getCode()==KeyCode.ENTER) {
            event.consume();
            selectConnection();
        }
    }

    @FXML
    private void onMouseClickConnectTableView(MouseEvent event) {
        if (event.getClickCount()>=2 && event.getButton()== MouseButton.PRIMARY) {
            selectConnection();
        }
    }

    @FXML
    private void onLoad(ActionEvent event) {
        selectConnection();
    }

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

    @FXML
    private void onOk(ActionEvent event) throws Exception {
        Connection conn = connectDatabase();

        if (conn!=null) {
            this.connection = conn;
            this.connect = createConnect();

            connectTableView.getScene().getWindow().hide();
            mainControllerInterface.connectNotify();
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        this.connection = null;
        this.connect = null;
        connectTableView.getScene().getWindow().hide();
    }

    @FXML
    private void onTest(ActionEvent event) throws Exception {
        Connection conn = connectDatabase();
        if (conn!=null) {
            mainControllerInterface.showAlertDialog("Connect success.", "");
            conn.close();
        }
    }

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

    private boolean isEmptyString(String value) {
        return (value==null || "".equals(value));
    }

    public Connection getConnection() {
        return this.connection;
    }
}
